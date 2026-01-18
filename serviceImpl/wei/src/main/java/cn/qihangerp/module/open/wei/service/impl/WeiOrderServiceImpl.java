package cn.qihangerp.module.open.wei.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.mapper.ErpOrderItemMapper;
import cn.qihangerp.mapper.ErpOrderMapper;
import cn.qihangerp.model.entity.OOrder;
import cn.qihangerp.model.entity.OOrderItem;
import cn.qihangerp.model.entity.WeiGoodsSku;
import cn.qihangerp.model.entity.WeiOrder;
import cn.qihangerp.model.entity.WeiOrderItem;
import cn.qihangerp.model.bo.WeiOrderConfirmBo;
import cn.qihangerp.module.open.wei.mapper.WeiGoodsSkuMapper;
import cn.qihangerp.module.open.wei.mapper.WeiOrderItemMapper;
import cn.qihangerp.module.open.wei.mapper.WeiOrderMapper;
import cn.qihangerp.module.open.wei.service.WeiOrderService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
* @author TW
* @description 针对表【oms_wei_order】的数据库操作Service实现
* @createDate 2024-06-03 16:48:31
*/
@AllArgsConstructor
@Service
public class WeiOrderServiceImpl extends ServiceImpl<WeiOrderMapper, WeiOrder>
    implements WeiOrderService {
    private final WeiOrderMapper mapper;
    private final WeiOrderItemMapper itemMapper;
    private final WeiGoodsSkuMapper goodsSkuMapper;
    private final ErpOrderMapper erpOrderMapper;
    private final ErpOrderItemMapper erpOrderItemMapper;
//    private final MQClientService mqClientService;

    @Override
    public PageResult<WeiOrder> queryPageList(WeiOrder bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WeiOrder> queryWrapper = new LambdaQueryWrapper<WeiOrder>()
                .eq(bo.getShopId()!=null, WeiOrder::getShopId,bo.getShopId())
                .eq(StringUtils.hasText(bo.getOrderId()), WeiOrder::getOrderId,bo.getOrderId())
                .eq(bo.getStatus()!=null, WeiOrder::getStatus,bo.getStatus())
                ;
        if(bo.getErpSendStatus()!=null){
            if(bo.getErpSendStatus()==-1) {
                queryWrapper.lt(WeiOrder::getErpSendStatus,3);
            }else {
                queryWrapper.eq(WeiOrder::getErpSendStatus, bo.getErpSendStatus());
            }
        }

        Page<WeiOrder> page = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(page.getRecords()!=null){
            for (var order:page.getRecords()) {
                order.setItems(itemMapper.selectList(new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getOrderId,order.getOrderId())));
            }
        }
        return PageResult.build(page);
    }

    @Transactional
    @Override
    public ResultVo<Integer> saveOrder(Long shopId, WeiOrder order) {
        try {
            List<WeiOrder> orders = mapper.selectList(new LambdaQueryWrapper<WeiOrder>().eq(WeiOrder::getOrderId, order.getOrderId()));
            if (orders != null && orders.size() > 0) {
                // 存在，修改
                WeiOrder update = new WeiOrder();
                update.setId(orders.get(0).getId());
                update.setOrderId(order.getOrderId());
                update.setStatus(order.getStatus());
                update.setUpdateTime(order.getUpdateTime());
                update.setPayInfo(order.getPayInfo());
                update.setAftersaleDetail(order.getAftersaleDetail());
                update.setDeliveryProductInfo(order.getDeliveryProductInfo());

                mapper.updateById(update);
                // 更新item
                for (var item : order.getItems()) {
                    List<WeiOrderItem> taoOrderItems = itemMapper.selectList(
                            new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getSkuId, item.getSkuId()).eq(WeiOrderItem::getOrderId,order.getOrderId())
                    );

                    if (taoOrderItems != null && taoOrderItems.size() > 0) {
                        // 更新
                        WeiOrderItem itemUpdate = new WeiOrderItem();
                        itemUpdate.setId(taoOrderItems.get(0).getId());
                        itemMapper.updateById(itemUpdate);
                    } else {
                        // 新增
                        item.setShopId(shopId);
                        item.setOrderId(order.getOrderId());
                        itemMapper.insert(item);
                    }
                }
                return ResultVo.error(ResultVoEnum.DataExist, "订单已经存在，更新成功");
            } else {
                // 不存在，新增

                order.setShopId(shopId);
                mapper.insert(order);
                // 添加item
                for (var item : order.getItems()) {

                    item.setShopId(shopId);
                    item.setOrderId(order.getOrderId());
                    itemMapper.insert(item);
                }
                return ResultVo.success();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultVo.error(ResultVoEnum.SystemException, "系统异常：" + e.getMessage());
        }
    }


    @Override
    public WeiOrder queryDetailById(Long id) {
        WeiOrder weiOrder = mapper.selectById(id);
        if(weiOrder!=null){
            weiOrder.setItems(itemMapper.selectList(new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getOrderId,weiOrder.getOrderId())));
        }
        return weiOrder;
    }
    @Override
    public WeiOrder queryDetailByOrderId(String orderId) {
        List<WeiOrder> weiOrders = mapper.selectList(new LambdaQueryWrapper<WeiOrder>().eq(WeiOrder::getOrderId,orderId));
        if(weiOrders!=null&&weiOrders.size()>0){
            weiOrders.get(0).setItems(itemMapper.selectList(new LambdaQueryWrapper<WeiOrderItem>().eq(WeiOrderItem::getOrderId,orderId)));
            return weiOrders.get(0);
        }else return null;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> confirmOrder(WeiOrderConfirmBo confirmBo) {
        WeiOrder weiOrder = mapper.selectById(confirmBo.getOrderId());
        if(weiOrder==null) return ResultVo.error("订单数据不存在");
        if(weiOrder.getAuditStatus()!=0) return ResultVo.error("已经确认过了！");

        List<WeiOrderItem> pddOrderItems = itemMapper.selectList(
                new LambdaQueryWrapper<WeiOrderItem>()
                        .eq(WeiOrderItem::getOrderId, weiOrder.getOrderId()));
        if(pddOrderItems==null || pddOrderItems.isEmpty()){
            return ResultVo.error("找不到订单item");
        }

        OOrder erpOrder = erpOrderMapper.selectOne(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum,weiOrder.getOrderId()));
        if(erpOrder!=null) {
            // 已经确认过了，更新自己
            WeiOrder douOrderUpdate = new WeiOrder();
            douOrderUpdate.setId(weiOrder.getId());
            douOrderUpdate.setAuditStatus(1);
            douOrderUpdate.setAuditTime(new Date());
            mapper.updateById(douOrderUpdate);

            return ResultVo.error("已经确认过了");
        }
        OOrder order = new OOrder();
        order.setOrderNum(weiOrder.getOrderId());
        order.setShopType(EnumShopType.WEI.getIndex());
        order.setShopId(weiOrder.getShopId());
//        order.setShipType(confirmBo.getShipType());
        order.setShipType(0);
        order.setBuyerMemo("");
        order.setSellerMemo("");
        order.setGoodsAmount(weiOrder.getProductPrice()!=null?weiOrder.getProductPrice().doubleValue()/100:0.0);
        order.setPostFee(weiOrder.getFreight()!=null?weiOrder.getFreight().doubleValue()/100:0.0);
        order.setSellerDiscount(weiOrder.getDiscountedPrice()!=null?weiOrder.getDiscountedPrice().doubleValue()/100:0.0);
        order.setPlatformDiscount(0.0);
        order.setChangeAmount(0.0);
        order.setAmount(weiOrder.getOrderPrice()!=null?weiOrder.getOrderPrice().doubleValue()/100:0.0);
        order.setPayment(order.getPayment().doubleValue()/100);
        order.setPayDiscount(0.0);
        order.setReceiverName(confirmBo.getReceiver());
        order.setReceiverMobile(confirmBo.getMobile());
        order.setAddress(confirmBo.getAddress());
        order.setProvince(confirmBo.getProvince());
        order.setCity(confirmBo.getCity());
        order.setTown(confirmBo.getTown());
        order.setOrderStatus(weiOrder.getStatus().toString());
        if(weiOrder.getStatus().intValue()==10){
            order.setOrderStatusText("待付款");
        }else if(weiOrder.getStatus().intValue()==12){
            order.setOrderStatusText("礼物待收下");
        }else if(weiOrder.getStatus().intValue()==13){
            order.setOrderStatusText("一起买待成团");
        }else if(weiOrder.getStatus().intValue()==20){
            order.setOrderStatusText("待发货");
        }else if(weiOrder.getStatus().intValue()==21){
            order.setOrderStatusText("部分发货");
        }else if(weiOrder.getStatus().intValue()==30){
            order.setOrderStatusText("待收货");
        }else if(weiOrder.getStatus().intValue()==100){
            order.setOrderStatusText("完成");
        }else if(weiOrder.getStatus().intValue()==200){
            order.setOrderStatusText("全部商品售后之后，订单取消");
        }else if(weiOrder.getStatus().intValue()==250){
            order.setOrderStatusText("未付款用户主动取消或超时未付款订单自动取消");
        }

        order.setOrderCreateTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(weiOrder.getCreateTime()), ZoneId.systemDefault()));
        order.setOrderUpdateTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(weiOrder.getUpdateTime()), ZoneId.systemDefault()));

        order.setShipper(0l);
        order.setShipStatus(0);
        order.setCreateTime(new Date());
        order.setCreateBy("手动确认订单");
        erpOrderMapper.insert(order);
        //插入item
        for (var item : pddOrderItems) {
            OOrderItem oOrderItem = new OOrderItem();
            // 确认订单时查找OGoodsSkuId是否存在
            List<WeiGoodsSku> skus = goodsSkuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getSkuId, item.getSkuId()));
            if (skus != null && !skus.isEmpty()) {
                oOrderItem.setGoodsId(skus.get(0).getErpGoodsId());
                oOrderItem.setGoodsSkuId(skus.get(0).getErpGoodsSkuId());
            }else {
                return ResultVo.error("店铺商品找不到绑定的商品库商品");
            }
            oOrderItem.setOrderId(order.getId());
            oOrderItem.setOrderNum(order.getOrderNum());
            oOrderItem.setSubOrderNum(order.getOrderNum()+"-"+item.getSkuId());
            oOrderItem.setShopType(EnumShopType.WEI.getIndex());
            oOrderItem.setShopId(weiOrder.getShopId());
            // 商品信息
            oOrderItem.setProductId(item.getProductId());
            oOrderItem.setSkuId(item.getSkuId());
            oOrderItem.setGoodsTitle(item.getTitle());
            oOrderItem.setGoodsImg(item.getThumbImg());
            oOrderItem.setGoodsNum(item.getOutProductId());
            if(StringUtils.hasText(item.getSkuAttrs())) {
                try {
                    String skuName = "";
                    JSONArray jsonArray = JSONArray.parseArray(item.getSkuAttrs());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject it = jsonArray.getJSONObject(i);
                        skuName += " "+it.getString("attr_value");
                    }
                    oOrderItem.setGoodsSpec(skuName);
                } catch (Exception e) {
                    oOrderItem.setGoodsSpec("");
                }
            }else oOrderItem.setGoodsSpec("");

            oOrderItem.setSkuNum(item.getOutSkuId());
            oOrderItem.setGoodsPrice(item.getSalePrice()!=null?item.getSalePrice().doubleValue()/100:0.0);
            oOrderItem.setQuantity(item.getSkuCnt());
            // 价格信息
            Integer goodsAmount = item.getSalePrice() * item.getSkuCnt();
            oOrderItem.setGoodsAmount(goodsAmount.doubleValue()/100);
            oOrderItem.setItemAmount(item.getRealPrice().doubleValue()/100);
            oOrderItem.setSellerDiscount(item.getMerchantDiscountedPrice().doubleValue()/100);
            //优惠后
//            Integer discountAfter = item.getEstimatePrice()!=null? item.getEstimatePrice():0;
//            if(discountAfter==0) oOrderItem.setSellerDiscount(0.0);
//            else{
//               Integer discount = goodsAmount - discountAfter;
//               oOrderItem.setSellerDiscount(discount.doubleValue()/100);
//            }
            oOrderItem.setPlatformDiscount(0.0);
            //改价后
            Integer changeAfter = item.getChangePrice()!=null?item.getChangePrice():0;
            if(changeAfter==0) oOrderItem.setChangeAmount(0.0);
            else{
                Integer change = goodsAmount - changeAfter;
                oOrderItem.setChangeAmount(change.doubleValue()/100);
            }

            oOrderItem.setPayDiscount(0.0);
            oOrderItem.setPayment(item.getRealPrice().doubleValue()/100);

            oOrderItem.setRefundCount(item.getOnAftersaleSkuCnt()+item.getFinishAftersaleSkuCnt());
            if(oOrderItem.getRefundCount().intValue()<oOrderItem.getQuantity().intValue()) {
                oOrderItem.setRefundStatus(1);
            }else{
                oOrderItem.setRefundStatus(4);
            }
            oOrderItem.setShipper(0l);
            oOrderItem.setShipType(order.getShipType());
            oOrderItem.setShipStatus(0);
            oOrderItem.setCreateTime(new Date());
            oOrderItem.setCreateBy("手动确认订单");
            erpOrderItemMapper.insert(oOrderItem);
        }
        // 更新自己
        WeiOrder douOrderUpdate = new WeiOrder();
        douOrderUpdate.setId(weiOrder.getId());
        douOrderUpdate.setAuditStatus(1);
        douOrderUpdate.setAuditTime(new Date());
        mapper.updateById(douOrderUpdate);
        return ResultVo.success();
    }
}




