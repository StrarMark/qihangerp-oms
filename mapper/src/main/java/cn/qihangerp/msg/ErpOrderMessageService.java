package cn.qihangerp.msg;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.enums.EnumOOrderStatus;
import cn.qihangerp.enums.EnumShopType;
import cn.qihangerp.mapper.*;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.sse.SseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ErpOrderMessageService {
    private final ErpSalesOrderMapper erpSalesOrderMapper;
    private final ErpSalesOrderItemMapper erpSalesOrderItemMapper;
    private final OOrderMapper orderMapper;
    private final OOrderItemMapper orderItemMapper;
    private final OOrderStockingMapper shipOrderMapper;
    private final OOrderStockingItemMapper shipOrderItemMapper;
    private final SseService sseService;
    @Transactional(rollbackFor = Exception.class)
    public ResultVo<Long> erpOrderMessage(String orderNum) {
        log.info("erp订单消息处理" + orderNum);
        List<ErpSalesOrder> originOrders = erpSalesOrderMapper.selectList(new LambdaQueryWrapper<ErpSalesOrder>().eq(ErpSalesOrder::getOrderNum, orderNum));

        if (originOrders == null || originOrders.size() == 0) {
            log.error("没有找到ERP原始订单:{}",orderNum);
            // 没有找到订单信息
            return ResultVo.error(ResultVoEnum.NotFound, "没有找到ERP原始订单：" + orderNum);
        }

        ErpSalesOrder originOrder = originOrders.get(0);

        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>()
                .eq(OOrder::getOrderNum, orderNum).eq(OOrder::getShopId,originOrder.getShopId()));
        if (oOrders == null || oOrders.isEmpty()) {
            // 新增订单
            OOrder insert = new OOrder();
            insert.setOrderMode(1);//订单模式0店铺订单1手工订单
            insert.setOrderNum(originOrder.getOrderNum());
            insert.setShopType(EnumShopType.ERP_ORDER.getIndex());
            insert.setShopId(0L);
            if(originOrder.getOwnerMerchantId()!=null){
                insert.setMerchantId(originOrder.getOwnerMerchantId());
            }else {
                insert.setMerchantId(0L);
            }
//            insert.setShopType(originOrder.getShopType());
//            insert.setShopId(originOrder.getShopId());
//            insert.setMerchantId(originOrder.getMerchantId());

            insert.setBuyerMemo(originOrder.getBuyerMemo());
            insert.setSellerMemo(originOrder.getSellerMemo());
//            insert.setRefundStatus(originOrder.getRefundStatus());
            insert.setOrderStatus(originOrder.getOrderStatus());
            insert.setPlatformStatusCode(originOrder.getOrderStatus().toString());
            insert.setGoodsAmount(originOrder.getGoodsAmount());
            insert.setPostFee(originOrder.getPostFee());
            insert.setAmount(originOrder.getAmount());
            insert.setPayment(originOrder.getPayment());
            insert.setPlatformDiscount(originOrder.getPlatformDiscount());
            insert.setSellerDiscount(originOrder.getSellerDiscount());
            insert.setMerchantAmount(insert.getAmount());
            insert.setReceiverName(originOrder.getReceiverName());
            insert.setReceiverMobile(originOrder.getReceiverMobile());
            insert.setAddress(originOrder.getAddress());
            insert.setProvince(originOrder.getProvince());
            insert.setCity(originOrder.getCity());
            insert.setTown(originOrder.getTown());

            // 转换为 LocalDateTime
            LocalDateTime localDateTime = originOrder.getOrderTime().toInstant()
                    .atZone(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();

            insert.setOrderTime(localDateTime);
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime updateTime = originOrder.getUpdateTime().toInstant()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toLocalDateTime();
                insert.setOrderModifiedTime(updateTime.format(formatter));
                if (originOrder.getOrderStatus()==3||originOrder.getOrderStatus()==11||originOrder.getOrderStatus()==29){
                    insert.setOrderFinishTime(updateTime.atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli());
                }
            }catch (Exception e){}
//            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");
            insert.setHasGift(originOrder.getHasGift());
            insert.setSalesmanId(originOrder.getSalesmanId());
            insert.setSalesmanName(originOrder.getSalesmanName());

            orderMapper.insert(insert);
            // 发送新订单消息
            sseService.broadcastNewOrderMessage(EnumShopType.ERP_ORDER,insert.getOrderNum());
            // 插入orderItem
            addOfflineOrderItem(insert.getId(), originOrder.getOrderNum(),insert.getOrderTime(), originOrder.getOrderStatus(), originOrder.getRefundStatus());

            //更新推送状态
            ErpSalesOrder offlineUpdate = new ErpSalesOrder();
            offlineUpdate.setId(originOrder.getId());
            offlineUpdate.setOmsPushStatus(1);
            offlineUpdate.setUpdateTime(new Date());
            offlineUpdate.setUpdateBy("推送状态更新");
            erpSalesOrderMapper.updateById(offlineUpdate);
        } else {
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            update.setOrderMode(1);//订单模式0店铺订单1手工订单
//            update.setRefundStatus(originOrder.getRefundStatus());
//            update.setShopType(originOrder.getShopType());
//            update.setShopId(originOrder.getShopId());
//            update.setMerchantId(originOrder.getMerchantId());
            update.setGoodsAmount(originOrder.getGoodsAmount());
            update.setPostFee(originOrder.getPostFee());
            update.setAmount(originOrder.getAmount());
            update.setPayment(originOrder.getPayment());
            update.setPlatformDiscount(originOrder.getPlatformDiscount());
            update.setSellerDiscount(originOrder.getSellerDiscount());
            update.setMerchantAmount(update.getAmount());
            update.setOrderStatus(originOrder.getOrderStatus());
            update.setPlatformStatusCode(originOrder.getOrderStatus().toString());
            update.setReceiverName(originOrder.getReceiverName());
            update.setReceiverMobile(originOrder.getReceiverMobile());
            update.setAddress(originOrder.getAddress());
            update.setProvince(originOrder.getProvince());
            update.setCity(originOrder.getCity());
            update.setTown(originOrder.getTown());
            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            update.setHasGift(originOrder.getHasGift());
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime updateTime = originOrder.getUpdateTime().toInstant()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toLocalDateTime();
                update.setOrderModifiedTime(updateTime.format(formatter));
                if (originOrder.getOrderStatus()==3||originOrder.getOrderStatus()==11||originOrder.getOrderStatus()==29){
                    update.setOrderFinishTime(updateTime.atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli());
                }
            }catch (Exception e){}
            orderMapper.updateById(update);
            // 更新发货主表数据状态
//            OOrderStocking shipOrder = new OOrderStocking();
//            shipOrder.setOrderStatus(update.getOrderStatus());
//            shipOrderMapper.update(shipOrder,new LambdaQueryWrapper<OOrderStocking>().eq(OOrderStocking::getOOrderId,update.getId()));
            // 更新发货订单状态
            List<OOrderStocking> oOrderStockings = shipOrderMapper.selectList(new LambdaQueryWrapper<OOrderStocking>().eq(OOrderStocking::getOOrderId, update.getId()));
            if(oOrderStockings!=null && oOrderStockings.size()>0) {
                for (OOrderStocking oOrderStocking : oOrderStockings) {
                    OOrderStocking stockingUpdate = new OOrderStocking();
                    stockingUpdate.setId(oOrderStocking.getId());
                    stockingUpdate.setOrderStatus(update.getOrderStatus());
                    stockingUpdate.setUpdateBy("通知更新订单状态");
                    stockingUpdate.setUpdateTime(new Date());
                    shipOrderMapper.updateById(stockingUpdate);
                }
            }

            // 插入orderItem
            addOfflineOrderItem(update.getId(), originOrder.getOrderNum(),oOrders.get(0).getOrderTime(), originOrder.getOrderStatus(), originOrder.getRefundStatus());
        }
        return ResultVo.success();
    }

    private void addOfflineOrderItem(String oOrderId, String originOrderNum,LocalDateTime orderTime, Integer orderStatus, Integer refundStatus) {
        List<ErpSalesOrderItem> originOrderItems = erpSalesOrderItemMapper.selectList(new LambdaQueryWrapper<ErpSalesOrderItem>().eq(ErpSalesOrderItem::getOrderNum, originOrderNum));
        if (originOrderItems != null && originOrderItems.size() > 0) {
            Long shipOrderId=0L;
            for (ErpSalesOrderItem item : originOrderItems) {

                OOrderItem orderItem = new OOrderItem();
                orderItem.setOrderId(oOrderId);
                orderItem.setOrderTime(orderTime);
                orderItem.setOrderNum(originOrderNum);
                orderItem.setSubOrderNum(item.getSubOrderNum());
//                orderItem.setShopId(shopId);
                orderItem.setShopId(0L);
                orderItem.setShopType(EnumShopType.ERP_ORDER.getIndex());
//                orderItem.setShopType(shopType);
                orderItem.setMerchantId(0L);
                // 这里将订单商品skuid转换成erp系统的skuid
                Long erpGoodsId = 0L;
                String erpSkuId = "0";

//                DouGoodsSku douGoodsSku = douGoodsSkuMapper.selectById(item.getSkuId());
//                if (douGoodsSku != null ) {
//                    erpGoodsId = douGoodsSku.getOGoodsId();
//                    erpSkuId = douGoodsSku.getOGoodsSkuId();
////                        orderItem.setGoodsImg(taoGoodsSku.get(0).getLogo());
////                        orderItem.setGoodsSpec(jdGoodsSkus.get(0).getSkuName());
////                    orderItem.setSkuNum(taoGoodsSku.get(0).getOuterId());
//                }
//                List<DouGoodsSku> douGoodsSku = douGoodsSkuMapper.selectList(new LambdaQueryWrapper<DouGoodsSku>().eq(DouGoodsSku::getId, item.getSkuId()));
//                if (douGoodsSku != null && !douGoodsSku.isEmpty()) {
//                    erpGoodsId = douGoodsSku.get(0).getOGoodsId();
//                    erpSkuId = douGoodsSku.get(0).getOGoodsSkuId();
////                        orderItem.setGoodsImg(taoGoodsSku.get(0).getLogo());
////                        orderItem.setGoodsSpec(jdGoodsSkus.get(0).getSkuName());
////                    orderItem.setSkuNum(taoGoodsSku.get(0).getOuterId());
//                }
                orderItem.setSkuNum(item.getSkuNum());
                orderItem.setSkuId(item.getSkuId());
                orderItem.setProductId(item.getGoodsId().toString());
                orderItem.setGoodsId(item.getGoodsId());
                orderItem.setGoodsSkuId(item.getGoodsSkuId());
                orderItem.setGoodsImg(item.getGoodsImg());
                orderItem.setGoodsSpec(item.getGoodsSpec());
                orderItem.setGoodsTitle(item.getGoodsTitle());
                orderItem.setGoodsPrice(item.getGoodsPrice());
                orderItem.setItemAmount(item.getItemAmount());
                orderItem.setPayment(item.getPayment());
                orderItem.setQuantity(item.getQuantity());
//                orderItem.setOrderStatus(orderStatus);
                orderItem.setRefundStatus(refundStatus);
                orderItem.setRefundCount(0);
                orderItem.setIsGift(item.getIsGift());
                List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                        .eq(OOrderItem::getOrderId, oOrderId)
                        .eq(OOrderItem::getGoodsSkuId, orderItem.getGoodsSkuId())
                );
                if(oOrderItems.isEmpty()) {
                    // 添加
                    orderItem.setCreateTime(new Date());
                    orderItem.setCreateBy("ORDER_MESSAGE");
                    orderItemMapper.insert(orderItem);
                }else{
                    // 修改
                    //修改
                    orderItem.setId(oOrderItems.get(0).getId());
                    orderItem.setUpdateTime(new Date());
                    orderItem.setUpdateBy("ORDER_MESSAGE");
                    orderItemMapper.updateById(orderItem);
                    // 更新发货子表状态
                    List<OOrderStockingItem> oOrderStockingItems = shipOrderItemMapper.selectList(new LambdaQueryWrapper<OOrderStockingItem>().eq(OOrderStockingItem::getOOrderItemId, orderItem.getId()));
                    if (oOrderStockingItems != null && oOrderStockingItems.size() > 0) {
                        shipOrderId = oOrderStockingItems.get(0).getShipOrderId();
                        // 存在数据，就是有发货订单
                        for (OOrderStockingItem oOrderStockingItem : oOrderStockingItems) {
                            OOrderStockingItem updateShip = new OOrderStockingItem();
                            updateShip.setId(oOrderStockingItem.getId());
                            updateShip.setRefundStatus(orderItem.getRefundStatus());
                            updateShip.setUpdateBy("通知修改订单状态");
                            updateShip.setUpdateTime(new Date());
                            shipOrderItemMapper.updateById(updateShip);
                        }
                    }
                }
            }
            // 更新发货订单状态
            List<OOrderStockingItem> shipOrderItemList = shipOrderItemMapper.selectList(
                    new LambdaQueryWrapper<OOrderStockingItem>()
                            .eq(OOrderStockingItem::getShipOrderId, shipOrderId)
                            .eq(OOrderStockingItem::getRefundStatus, 1)
            );
            // 找出没有退款的子订单，如果没有，那么把主订单直接更新成取消状态
            if (shipOrderItemList == null || shipOrderItemList.isEmpty()) {
                OOrderStocking updateShip = new OOrderStocking();
                updateShip.setId(shipOrderId);
                updateShip.setOrderStatus(EnumOOrderStatus.CLOSED.getIndex());
                updateShip.setUpdateBy("子订单全部退款");
                updateShip.setUpdateTime(new Date());
                shipOrderMapper.updateById(updateShip);
            }

            // 更新主订单状态（如果全部退款的话，就更新成订单取消状态）
            List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                    .eq(OOrderItem::getOrderId, oOrderId)
                    .eq(OOrderItem::getRefundStatus, 1)
            );
            // 找出没有退款的子订单，如果没有，那么把主订单直接更新成取消状态
            if (oOrderItems == null || oOrderItems.isEmpty()) {
                OOrder orderUpdate = new OOrder();
                orderUpdate.setId(oOrderId);
                orderUpdate.setOrderStatus(EnumOOrderStatus.CLOSED.getIndex());
                orderUpdate.setCancelReason("子订单全部退款");
                orderUpdate.setUpdateBy("子订单全部退款");
                orderUpdate.setUpdateTime(new Date());
                orderMapper.updateById(orderUpdate);
            }
            log.info("===========同步erpOrder====成功");
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("=====没有找到offlineOrderItems");
        }
    }


}
