package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.mapper.*;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.bo.RefundProcessingBo;
import cn.qihangerp.model.bo.RefundSearchBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.service.ErpWarehouseSalesAfterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【o_supplier_refund(供应商售后表)】的数据库操作Service实现
* @createDate 2025-02-22 11:22:40
*/
@AllArgsConstructor
@Service
public class ErpWarehouseSalesAfterServiceImpl extends ServiceImpl<ErpWarehouseSalesAfterMapper, ErpWarehouseSalesAfter>
    implements ErpWarehouseSalesAfterService {
    private final ErpWarehouseSalesAfterMapper mapper;
    private final ORefundAfterSaleMapper afterSaleMapper;
    private final ORefundMapper refundMapper;
    private final OOrderItemMapper orderItemMapper;
    private final OOrderMapper orderMapper;
    private final OGoodsSkuMapper goodsSkuMapper;
    @Override
    public PageResult<ErpWarehouseSalesAfter> queryPageList(RefundSearchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseSalesAfter> queryWrapper = new LambdaQueryWrapper<ErpWarehouseSalesAfter>()
                .eq(StringUtils.hasText(bo.getRefundNum()), ErpWarehouseSalesAfter::getRefundNum,bo.getRefundNum())
                .eq(StringUtils.hasText(bo.getOrderNum()), ErpWarehouseSalesAfter::getOrderNum,bo.getOrderNum())
                .eq(bo.getRefundType()!=null, ErpWarehouseSalesAfter::getRefundType,bo.getRefundType())
                .eq(bo.getSupplierId()!=null, ErpWarehouseSalesAfter::getSupplierId,bo.getSupplierId())
                .eq(bo.getHasProcessing()!=null, ErpWarehouseSalesAfter::getHasProcessing,bo.getHasProcessing());

        Page<ErpWarehouseSalesAfter> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    /**
     * 售后处理
     * @param processingBo
     * @return
     */
    @Transactional
    @Override
    public ResultVo<Long> refundProcessing(RefundProcessingBo processingBo, String createBy,Long supplierId) {
        if (processingBo.getRefundId() == null) return ResultVo.error(500, "缺少参数refundId");

        ErpWarehouseSalesAfter supplierRefund = mapper.selectById(processingBo.getRefundId());
        if (supplierRefund == null) return ResultVo.error(500,"没有找到退款单");
        else if (supplierRefund.getHasProcessing() == 1) {
            return ResultVo.error(500,"已经处理过了");
        } else if (supplierRefund.getSupplierId().longValue() != supplierId.longValue()) {
            return ResultVo.error(500,"没有权限处理");
        }

        // 查询相关订单
        ORefund refund = refundMapper.selectById(supplierRefund.getORefundId());
        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum, refund.getOrderNum()));
        List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderNum, refund.getOrderNum()).eq(OOrderItem::getSkuId, refund.getSkuId()));
        List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, refund.getSkuNum()));

        // 保存售后结果
        ORefundAfterSale afterSale = new ORefundAfterSale();
        afterSale.setRefundNum(refund.getRefundNum());
        afterSale.setRefundId(processingBo.getRefundId().toString());
        afterSale.setType(processingBo.getType());
        afterSale.setShopId(refund.getShopId());
        afterSale.setShopType(refund.getShopType());
        afterSale.setOrderNum(refund.getOrderNum());
        afterSale.setSubOrderNum(refund.getOrderItemNum());
        afterSale.setOOrderId(Long.parseLong(oOrders.get(0).getId()));
        afterSale.setOOrderItemId(Long.parseLong(oOrderItems.get(0).getId()));
        afterSale.setSkuId(refund.getSkuId());
        afterSale.setQuantity(refund.getQuantity().intValue());
        afterSale.setTitle(refund.getGoodsName());
        afterSale.setImg(refund.getGoodsImage());
        afterSale.setSkuInfo(refund.getGoodsSku());
        afterSale.setSkuCode(refund.getSkuNum());
        afterSale.setOGoodsId(oGoodsSkus.isEmpty()?0L:Long.parseLong(oGoodsSkus.get(0).getGoodsId()));
        afterSale.setOGoodsSkuId(oGoodsSkus.isEmpty()?0L:Long.parseLong(oGoodsSkus.get(0).getId()));
        afterSale.setHasGoodsSend(processingBo.getHasGoodsSend());
        afterSale.setSendLogisticsCode(processingBo.getSendLogisticsCode());
        afterSale.setReturnLogisticsCode(processingBo.getReturnLogisticsCode());
        afterSale.setReceiverName(processingBo.getReceiverName());
        afterSale.setReceiverTel(processingBo.getReceiverTel());
        afterSale.setReceiverAddress(processingBo.getReceiverAddress());
        afterSale.setReissueLogisticsCode(processingBo.getReissueLogisticsCode());
        afterSale.setRemark(processingBo.getRemark());
        afterSale.setStatus(processingBo.getType() == 0?10:0);
        afterSale.setCreateTime(new Date());
        afterSale.setCreateBy(createBy);

        afterSaleMapper.insert(afterSale);

        // 更新ORefund
        ErpWarehouseSalesAfter updates = new ErpWarehouseSalesAfter();
        updates.setId(supplierRefund.getId());
        updates.setUpdateBy(createBy+"操作售后处理");
        updates.setUpdateTime(new Date());
        updates.setHasProcessing(1);
        updates.setAfterSaleId(afterSale.getId());
        mapper.updateById(updates);

        // 更新ORefund
        ORefund update = new ORefund();
        update.setId(refund.getId());
        update.setUpdateBy(createBy+"操作售后处理");
        update.setUpdateTime(new Date());
        update.setHasProcessing(1);
        update.setAfterSaleId(afterSale.getId());
        refundMapper.updateById(update);

        return ResultVo.success();
    }
}




