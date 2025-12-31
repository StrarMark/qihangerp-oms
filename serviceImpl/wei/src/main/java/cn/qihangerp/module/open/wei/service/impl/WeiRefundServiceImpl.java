package cn.qihangerp.module.open.wei.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.model.entity.WeiGoodsSku;
import cn.qihangerp.model.entity.WeiRefund;
import cn.qihangerp.module.open.wei.mapper.WeiGoodsSkuMapper;
import cn.qihangerp.module.open.wei.mapper.WeiOrderItemMapper;
import cn.qihangerp.module.open.wei.mapper.WeiRefundMapper;
import cn.qihangerp.module.open.wei.service.WeiRefundService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author TW
* @description 针对表【oms_wei_refund(视频号小店退款)】的数据库操作Service实现
* @createDate 2024-06-20 17:07:27
*/
@AllArgsConstructor
@Service
public class WeiRefundServiceImpl extends ServiceImpl<WeiRefundMapper, WeiRefund>
    implements WeiRefundService {
    private final WeiRefundMapper mapper;
    private final WeiGoodsSkuMapper goodsSkuMapper;
    private final WeiOrderItemMapper orderItemMapper;
//    private final MQClientService mqClientService;

    @Override
    public PageResult<WeiRefund> queryPageList(WeiRefund bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WeiRefund> queryWrapper = new LambdaQueryWrapper<WeiRefund>()
                .eq(bo.getShopId()!=null, WeiRefund::getShopId,bo.getShopId())
                .eq(StringUtils.hasText(bo.getOrderId()), WeiRefund::getOrderId,bo.getOrderId())
                ;

        Page<WeiRefund> page = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(page);
    }

    @Transactional
    @Override
    public ResultVo<Integer> saveRefund(Long shopId, WeiRefund refund) {
        try {
            List<WeiRefund> refunds = mapper.selectList(new LambdaQueryWrapper<WeiRefund>().eq(WeiRefund::getAfterSaleOrderId, refund.getAfterSaleOrderId()));
            WeiRefund newRefund = new WeiRefund();
            if (refunds != null && refunds.size() > 0) {
                newRefund = refunds.get(0);
                // 存在，修改
                WeiRefund update = new WeiRefund();
                update.setId(refunds.get(0).getId());
                update.setOrderId(refund.getOrderId());
                update.setStatus(refund.getStatus());
                update.setUpdateTime(refund.getUpdateTime());
                update.setReturnWaybillId(refund.getReturnWaybillId());
                update.setReturnDeliveryName(refund.getReturnDeliveryName());
                update.setReturnDeliveryId(refund.getReturnDeliveryId());
                update.setComplaintId(refund.getComplaintId());
                if(refund.getSkuId()!=null) {
                    List<WeiGoodsSku> pddGoodsSku = goodsSkuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getSkuId, refund.getSkuId()));
                    if (pddGoodsSku != null && !pddGoodsSku.isEmpty()) {
                        update.setOGoodsId(pddGoodsSku.get(0).getErpGoodsId());
                        update.setOGoodsSkuId(pddGoodsSku.get(0).getErpGoodsSkuId());
                    }
                }
                mapper.updateById(update);

                return ResultVo.error(ResultVoEnum.DataExist, "退款已经存在，更新成功");
            } else {
                newRefund = refund;
                // 不存在，新增
                if(refund.getSkuId()!=null) {
                    List<WeiGoodsSku> pddGoodsSku = goodsSkuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getSkuId, refund.getSkuId()));
                    if (pddGoodsSku != null && !pddGoodsSku.isEmpty()) {
                        refund.setOGoodsId(pddGoodsSku.get(0).getErpGoodsId());
                        refund.setOGoodsSkuId(pddGoodsSku.get(0).getErpGoodsSkuId());
                    }
                }
                refund.setShopId(shopId);
                mapper.insert(refund);
                return ResultVo.success();
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultVo.error(ResultVoEnum.SystemException, "系统异常：" + e.getMessage());
        }
    }
}




