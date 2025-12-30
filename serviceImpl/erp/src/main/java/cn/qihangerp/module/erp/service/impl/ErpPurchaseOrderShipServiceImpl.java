package cn.qihangerp.module.erp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.utils.DateUtils;
import cn.qihangerp.model.entity.ErpPurchaseOrder;
import cn.qihangerp.model.entity.ErpPurchaseOrderShip;
import cn.qihangerp.model.query.PurchaseOrderSearchBo;
import cn.qihangerp.module.erp.mapper.ErpPurchaseOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.erp.service.ErpPurchaseOrderShipService;
import cn.qihangerp.module.erp.mapper.ErpPurchaseOrderShipMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author 1
* @description 针对表【erp_purchase_order_ship(采购订单物流表)】的数据库操作Service实现
* @createDate 2025-09-09 10:40:41
*/
@AllArgsConstructor
@Service
public class ErpPurchaseOrderShipServiceImpl extends ServiceImpl<ErpPurchaseOrderShipMapper, ErpPurchaseOrderShip>
    implements ErpPurchaseOrderShipService{
    private final ErpPurchaseOrderShipMapper shipMapper;
    private final ErpPurchaseOrderMapper orderMapper;

    private final String DATE_PATTERN =
            "^(?:(?:(?:\\d{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|(?:(?:(?:\\d{2}(?:0[48]|[2468][048]|[13579][26])|(?:(?:0[48]|[2468][048]|[13579][26])00))-0?2-29))$)|(?:(?:(?:\\d{4}-(?:0?[13578]|1[02]))-(?:0?[1-9]|[12]\\d|30))$)|(?:(?:(?:\\d{4}-0?[13-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|30))$)|(?:(?:(?:\\d{2}(?:0[48]|[13579][26]|[2468][048])|(?:(?:0[48]|[13579][26]|[2468][048])00))-0?2-29))$)$";
    private final Pattern DATE_FORMAT = Pattern.compile(DATE_PATTERN);
    @Override
    public PageResult<ErpPurchaseOrderShip> queryPageList(PurchaseOrderSearchBo bo, PageQuery pageQuery) {
        if(org.springframework.util.StringUtils.hasText(bo.getStartTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getStartTime());
            boolean b = matcher.find();
            if(b){
                bo.setStartTime(bo.getStartTime()+" 00:00:00");
            }
        }
        if(org.springframework.util.StringUtils.hasText(bo.getEndTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getEndTime());
            boolean b = matcher.find();
            if(b){
                bo.setEndTime(bo.getEndTime()+" 23:59:59");
            }
        }

        LambdaQueryWrapper<ErpPurchaseOrderShip> queryWrapper = new LambdaQueryWrapper<ErpPurchaseOrderShip>()
                .eq(bo.getSupplierId()!=null, ErpPurchaseOrderShip::getSupplierId,bo.getSupplierId())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()), ErpPurchaseOrderShip::getOrderNum,bo.getOrderNum())
                .eq(bo.getOrderStatus()!=null, ErpPurchaseOrderShip::getStatus,bo.getOrderStatus())
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()), ErpPurchaseOrderShip::getShipTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()), ErpPurchaseOrderShip::getShipTime,bo.getEndTime())
                ;


        Page<ErpPurchaseOrderShip> pages = shipMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateScmPurchaseOrderShip(ErpPurchaseOrderShip erpPurchaseOrderShip)
    {
        ErpPurchaseOrderShip ship = shipMapper.selectById(erpPurchaseOrderShip.getId());
        if(ship== null) return -1;
        else if(ship.getStatus()!=0)return -2;
        // 更新采购单状态
        ErpPurchaseOrder order = new ErpPurchaseOrder();
        order.setId(erpPurchaseOrderShip.getOrderId());
        order.setStatus(2);
        order.setReceivedTime(erpPurchaseOrderShip.getReceiptTime());
        order.setUpdateTime(DateUtils.getNowDate());
        order.setUpdateBy(erpPurchaseOrderShip.getUpdateBy());
        orderMapper.updateById(order);
        //更新
        ErpPurchaseOrderShip update = new ErpPurchaseOrderShip();
        update.setId(ship.getId());
        update.setUpdateTime(DateUtils.getNowDate());
        update.setUpdateBy(erpPurchaseOrderShip.getUpdateBy());
        update.setStatus(1);
        update.setRemark(erpPurchaseOrderShip.getRemark());
        update.setReceiptTime(erpPurchaseOrderShip.getReceiptTime());
//        update.setReceiptTime(DateUtils.getNowDate());
        update.setId(erpPurchaseOrderShip.getId());
        return shipMapper.updateById(update);
    }

}




