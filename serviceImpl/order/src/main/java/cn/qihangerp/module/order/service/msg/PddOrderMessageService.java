package cn.qihangerp.module.order.service.msg;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumOOrderStatus;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.utils.StringUtils;
import cn.qihangerp.model.entity.OOrder;
import cn.qihangerp.model.entity.OOrderItem;
import cn.qihangerp.module.order.mapper.OOrderItemMapper;
import cn.qihangerp.module.order.mapper.OOrderMapper;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PddOrderMessageService {
    private final OOrderMapper orderMapper;
    private final OOrderItemMapper orderItemMapper;

    @Transactional
    public ResultVo<Long> pddOrderMessage(String orderSn, JSONObject orderDetail ) {
        log.info("=====pdd order message===订单号{}==="+orderSn);
//        JSONObject jsonObject = pddApiService.getOrderDetail(orderSn);
//        if(jsonObject.getInteger("code")!=200 || jsonObject.getJSONObject("data") ==null){
//            log.info("=====pdd order message===没有找到订单");
//            return ResultVo.error(404,"没有找到订单");
//        }
//
//        JSONObject orderDetail = jsonObject.getJSONObject("data");
//        log.info("=====pdd order message===订单:"+JSONObject.toJSONString(orderDetail));
        if(orderDetail == null) {
            log.info("=====pdd order message===没有找到订单=======");
            return ResultVo.error("没有找到订单");
        }

        JSONArray itemArray = orderDetail.getJSONArray("items");
        if (itemArray.isEmpty()) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("=====pdd order message===没有items====事务回滚=======");
            return ResultVo.error("没有找到订单items");
        }
//        List<PddOrder> originOrders = pddOrderMapper.selectList(new LambdaQueryWrapper<PddOrder>().eq(PddOrder::getOrderSn, orderSn));
//
//        if(originOrders == null || originOrders.size() == 0) {
//            // 没有找到订单信息
//            return ResultVo.error(ResultVoEnum.NotFound,"没有找到PDD原始订单："+orderSn);
//        }
//        PddOrder originOrder = originOrders.get(0);
//        PddOrder originOrder = new PddOrder();
        Long shopId = orderDetail.getLong("shopId");
        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>()
                .eq(OOrder::getOrderNum, orderSn).eq(OOrder::getShopId,shopId));
        if(oOrders == null || oOrders.isEmpty()) {
            // 新增订单
            OOrder insert = new OOrder();
            insert.setOrderNum(orderSn);
            insert.setShopType(EnumShopType.PDD.getIndex());
            insert.setShopId(shopId);
            insert.setBuyerMemo(orderDetail.getString("buyerMemo"));
            insert.setSellerMemo(orderDetail.getString("remark"));
            Integer originOrderStatus = orderDetail.getInteger("orderStatus");
            Integer originRefundStatus = orderDetail.getInteger("refundStatus");
            Integer afterSalesStatus = orderDetail.getInteger("afterSalesStatus");
            String platformStatusDesc = "未知";
            // 状态 订单状态0：新订单，1：待发货，2：已发货，3：已完成，11已取消；12退款中；21待付款；22锁定，29删除
            int orderStatus = 0;
            int refundStatus = -1;
            if (originRefundStatus == 1) {
                // 没有售后
                orderStatus = originOrderStatus;
                if(originOrderStatus==1) {
                    platformStatusDesc = "待发货";
                }else if(originOrderStatus == 2){
                    platformStatusDesc = "已发货待签收";
                }else if(originOrderStatus == 3){
                    platformStatusDesc = "已签收";
                }
                refundStatus = 1;
            } else {
                if (originRefundStatus == 4) {
                    refundStatus = 4;
                    orderStatus = EnumOOrderStatus.CLOSED.getIndex();
                    if(originOrderStatus==1) {
                        platformStatusDesc = "待发货-已退款";
                    }else if(originOrderStatus == 2){
                        platformStatusDesc = "已发货待签收-已退款";
                    }else if(originOrderStatus == 3){
                        platformStatusDesc = "已签收-已退款";
                    }
                } else {
                    refundStatus = originRefundStatus;
                    orderStatus = EnumOOrderStatus.REFUND.getIndex();
                    if(originOrderStatus==1) {
                        platformStatusDesc = "待发货-售后中";
                    }else if(originOrderStatus == 2){
                        platformStatusDesc = "已发货待签收-售后中";
                    }else if(originOrderStatus == 3){
                        platformStatusDesc = "已签收-售后中";
                    }
                }
            }
            if(orderDetail.getInteger("riskControlStatus")==1){
                orderStatus = 0;
            }
            insert.setRefundStatus(refundStatus);
            insert.setOrderStatus(orderStatus);
//            insert.setPlatformStatusCode(originOrderStatus+"-"+originRefundStatus);
//            insert.setPlatformStatusDesc(platformStatusDesc);
            // 价格
            insert.setGoodsAmount(orderDetail.getDouble("goodsAmount"));//.getGoodsAmount());
            insert.setPostFee(orderDetail.getDouble("postage"));
            insert.setAmount(orderDetail.getDouble("goodsAmount"));
            double payAmount = orderDetail.getDouble("payAmount");
            insert.setPayment(payAmount);
            double platformDiscount = orderDetail.getDouble("platformDiscount")!=null?orderDetail.getDouble("platformDiscount"):0.0;
            insert.setPlatformDiscount(platformDiscount);
            double sellerDiscount = orderDetail.getDouble("sellerDiscount")!=null?orderDetail.getDouble("sellerDiscount"):0.0;
            insert.setSellerDiscount(sellerDiscount);
            double payDiscount =  orderDetail.getDouble("duoDuoPayReduction")!=null?orderDetail.getDouble("duoDuoPayReduction"):0.0;
//            insert.setMerchantAmount(payAmount+platformDiscount+payDiscount);
//            insert.setWaybillCode(orderDetail.getString("trackingNumber"));
            insert.setReceiverName(orderDetail.getString("receiverNameMask"));
            insert.setReceiverMobile(orderDetail.getString("receiverPhoneMask"));
            insert.setAddress(orderDetail.getString("addressMask"));
            insert.setProvince(orderDetail.getString("province"));
            insert.setCity(orderDetail.getString("city"));
            insert.setTown(orderDetail.getString("town"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(orderDetail.getString("createdTime"), formatter);
            insert.setOrderTime(dateTime);
//            insert.setOrderModifiedTime(orderDetail.getString("updatedAt"));
//            if(orderDetail.getDate("receiveTime")!=null){
//                insert.setOrderFinishTime(orderDetail.getDate("receiveTime").getTime());
//            }
//            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");

//            insert.setOpenAddressId(orderDetail.getString("openAddressId"));
            orderMapper.insert(insert);
            // 发送新订单消息
//            sseService.broadcastNewOrderMessage(EnumShopType.PDD,insert.getOrderNum());
            // 插入orderItem
//            addPddOrderItem(insert.getId(),originOrder.getOrderSn(),orderStatus,refundStatus,platformDiscount,sellerDiscount);
//            JSONArray itemArray = orderDetail.getJSONArray("items");
//            if (itemArray.isEmpty()) {
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                log.info("=====pdd order message===没有items====事务回滚=======");
//            }


            for (int i =0;i<itemArray.size();i++) {
                JSONObject itemObject =itemArray.getJSONObject(i);
//                Map<String,Object> itemObject = (Map<String, Object>) itemArray.get(i);
//                JSONObject itemObject = (JSONObject) item;

                OOrderItem orderItem = new OOrderItem();
                orderItem.setShopId(insert.getShopId());
                orderItem.setShopType(EnumShopType.PDD.getIndex());
//                orderItem.setMerchantId(insert.getMerchantId());
//                orderItem.setOrderTime(insert.getOrderTime());
                orderItem.setOrderId(insert.getId());
                orderItem.setOrderNum(orderSn);
                orderItem.setSubOrderNum(orderSn+"-"+itemObject.getString("skuId"));
                // 这里将订单商品skuid转换成erp系统的skuid
//                Long erpGoodsId = 0L;
//                String erpSkuId = "0";
//
//                List<PddGoodsSku> pddGoodsSku = pddGoodsSkuMapper.selectList(new LambdaQueryWrapper<PddGoodsSku>().eq(PddGoodsSku::getSkuId, item.getSkuId()));
//                if (pddGoodsSku != null && !pddGoodsSku.isEmpty()) {
//                    erpGoodsId = pddGoodsSku.get(0).getOGoodsId();
//                    erpSkuId = pddGoodsSku.get(0).getOGoodsSkuId();
////                        orderItem.setGoodsImg(taoGoodsSku.get(0).getLogo());
////                        orderItem.setGoodsSpec(jdGoodsSkus.get(0).getSkuName());
////                    orderItem.setSkuNum(taoGoodsSku.get(0).getOuterId());
//                }
                orderItem.setSkuNum(itemObject.getString("outerId"));
                orderItem.setSkuId(itemObject.getString("skuId"));
//                orderItem.setProductId(itemObject.getString("goodsId"));
                orderItem.setGoodsId(itemObject.getLong("erpGoodsId"));
                orderItem.setGoodsSkuId(itemObject.getLong("erpGoodsSkuId"));
                orderItem.setGoodsImg(itemObject.getString("goodsImg"));
                orderItem.setGoodsSpec(itemObject.getString("goodsSpec"));
                orderItem.setGoodsTitle(itemObject.getString("goodsName"));
                orderItem.setGoodsPrice(itemObject.getDouble("goodsPrice"));
                orderItem.setQuantity(itemObject.getInteger("goodsCount"));
                if (i == 0) {
                    Double itemAmount = orderItem.getGoodsPrice() * orderItem.getQuantity() - insert.getPlatformDiscount() - insert.getSellerDiscount();
                    orderItem.setItemAmount(itemAmount);
                    orderItem.setPayment(itemAmount);
                } else {
                    orderItem.setItemAmount(orderItem.getGoodsPrice()* orderItem.getQuantity());
                    orderItem.setPayment(orderItem.getGoodsPrice()* orderItem.getQuantity());
                }
//                orderItem.setPayment(item.getGoodsPrice());

//                orderItem.setOrderStatus(orderStatus);
                orderItem.setRefundStatus(refundStatus);
                orderItem.setRefundCount(0);
                orderItem.setCreateTime(new Date());
                orderItem.setCreateBy("ORDER_MESSAGE");
                orderItemMapper.insert(orderItem);
            }


        }else {
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            Integer originOrderStatus = orderDetail.getInteger("orderStatus");
            Integer originRefundStatus = orderDetail.getInteger("refundStatus");
            Integer afterSalesStatus = orderDetail.getInteger("afterSalesStatus");
            String platformStatusDesc = "未知";
            // 状态 订单状态0：新订单，1：待发货，2：已发货，3：已完成，11已取消；12退款中；21待付款；22锁定，29删除
            int orderStatus = 0;
            int refundStatus = -1;
            if (originRefundStatus == 1) {
                // 没有售后
                orderStatus = originOrderStatus;
                if(originOrderStatus==1) {
                    platformStatusDesc = "待发货";
                }else if(originOrderStatus == 2){
                    platformStatusDesc = "已发货待签收";
                }else if(originOrderStatus == 3){
                    platformStatusDesc = "已签收";
                }
                refundStatus = 1;
            } else {
                if (originRefundStatus == 4) {
                    refundStatus = 4;
                    orderStatus = EnumOOrderStatus.CLOSED.getIndex();
                    if(originOrderStatus==1) {
                        platformStatusDesc = "待发货-已退款";
                    }else if(originOrderStatus == 2){
                        platformStatusDesc = "已发货待签收-已退款";
                    }else if(originOrderStatus == 3){
                        platformStatusDesc = "已签收-已退款";
                    }
                } else {
                    refundStatus = originRefundStatus;
                    orderStatus = EnumOOrderStatus.REFUND.getIndex();
                    if(originOrderStatus==1) {
                        platformStatusDesc = "待发货-售后中";
                    }else if(originOrderStatus == 2){
                        platformStatusDesc = "已发货待签收-售后中";
                    }else if(originOrderStatus == 3){
                        platformStatusDesc = "已签收-售后中";
                    }
                }
            }
            if(orderDetail.getInteger("riskControlStatus")==1){
                orderStatus = 0;
            }
//            insert.setRefundStatus(refundStatus);
            update.setOrderStatus(orderStatus);
//            update.setPlatformStatusCode(originOrderStatus+"-"+originRefundStatus);
//            update.setPlatformStatusDesc(platformStatusDesc);
            update.setShopType(EnumShopType.PDD.getIndex());
            update.setShopId(orderDetail.getLong("shopId"));
//            update.setMerchantId(orderDetail.getLong("merchantId"));

            // 价格
            update.setGoodsAmount(orderDetail.getDouble("goodsAmount"));//.getGoodsAmount());
            update.setPostFee(orderDetail.getDouble("postage"));
            update.setAmount(orderDetail.getDouble("goodsAmount"));
            double payAmount = orderDetail.getDouble("payAmount");
            update.setPayment(payAmount);
            double platformDiscount = orderDetail.getDouble("platformDiscount")!=null?orderDetail.getDouble("platformDiscount"):0.0;
            update.setPlatformDiscount(platformDiscount);
            double sellerDiscount = orderDetail.getDouble("sellerDiscount")!=null?orderDetail.getDouble("sellerDiscount"):0.0;
            update.setSellerDiscount(sellerDiscount);
            double payDiscount =  orderDetail.getDouble("duoDuoPayReduction")!=null?orderDetail.getDouble("duoDuoPayReduction"):0.0;
//            update.setMerchantAmount(payAmount+platformDiscount+payDiscount);

            if (orderStatus == 1 && refundStatus == 1) {
                if (StringUtils.isNotBlank(orderDetail.getString("receiverNameMask"))) {
                    update.setReceiverName(orderDetail.getString("receiverNameMask"));
                }
                if (StringUtils.isNotBlank(orderDetail.getString("receiverPhoneMask"))) {
                    update.setReceiverMobile(orderDetail.getString("receiverPhoneMask"));
                }
                if (StringUtils.isNotBlank(orderDetail.getString("addressMask"))) {
                    update.setAddress(orderDetail.getString("addressMask"));
                }
            }

            if (StringUtils.isNotBlank(orderDetail.getString("province"))) {
                update.setProvince(orderDetail.getString("province"));
            }
            if (StringUtils.isNotBlank(orderDetail.getString("city"))) {
                update.setCity(orderDetail.getString("city"));
            }
            if (StringUtils.isNotBlank(orderDetail.getString("town"))) {
                update.setTown(orderDetail.getString("town"));
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(orderDetail.getString("createdTime"), formatter);
            update.setOrderTime(dateTime);
//            update.setOrderModifiedTime(orderDetail.getString("updatedAt"));
//            if(orderDetail.getDate("receiveTime")!=null){
//                update.setOrderFinishTime(orderDetail.getDate("receiveTime").getTime());
//            }
//            update.setOpenAddressId(orderDetail.getString("openAddressId"));
//            update.setWaybillCode(orderDetail.getString("trackingNumber"));
            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            orderMapper.updateById(update);
            // 更新发货主表数据状态



            Long shipOrderId = 0L;
            for (int i = 0; i < itemArray.size(); i++) {
                JSONObject itemObject = itemArray.getJSONObject(i);
                List<OOrderItem> oOrderItems = orderItemMapper.selectList(
                        new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, oOrders.get(0).getId())
                                .eq(OOrderItem::getSkuId, itemObject.getString("skuId")));
                if (oOrderItems.isEmpty()) {
                    // 新增item
                    OOrderItem orderItem = new OOrderItem();
                    orderItem.setOrderId(oOrders.get(0).getId());
                    orderItem.setShopId(update.getShopId());
                    orderItem.setShopType(EnumShopType.PDD.getIndex());
//                    orderItem.setMerchantId(update.getMerchantId());
                    orderItem.setOrderNum(orderSn);
                    orderItem.setSubOrderNum(orderSn + "-" + itemObject.getString("skuId"));
                    orderItem.setSkuNum(itemObject.getString("outerId"));
                    orderItem.setSkuId(itemObject.getString("skuId"));
//                    orderItem.setProductId(itemObject.getString("goodsId"));
                    orderItem.setGoodsId(itemObject.getLong("erpGoodsId"));
                    orderItem.setGoodsSkuId(itemObject.getLong("erpGoodsSkuId"));
                    orderItem.setGoodsImg(itemObject.getString("goodsImg"));
                    orderItem.setGoodsSpec(itemObject.getString("goodsSpec"));
                    orderItem.setGoodsTitle(itemObject.getString("goodsName"));
                    orderItem.setGoodsPrice(itemObject.getDouble("goodsPrice"));
                    orderItem.setQuantity(itemObject.getInteger("goodsCount"));
                    if (i == 0) {
                        Double itemAmount = orderItem.getGoodsPrice() * orderItem.getQuantity() - oOrders.get(0).getPlatformDiscount() - oOrders.get(0).getSellerDiscount();
                        orderItem.setItemAmount(itemAmount);
                        orderItem.setPayment(itemAmount);
                    } else {
                        orderItem.setItemAmount(orderItem.getGoodsPrice() * orderItem.getQuantity());
                        orderItem.setPayment(orderItem.getGoodsPrice() * orderItem.getQuantity());
                    }
//                    orderItem.setOrderStatus(orderStatus);
                    orderItem.setRefundStatus(refundStatus);
                    orderItem.setRefundCount(0);
                    orderItem.setCreateTime(new Date());
                    orderItem.setCreateBy("ORDER_MESSAGE");
                    orderItemMapper.insert(orderItem);
                } else {
                    // 修改、
                    OOrderItem orderItem = new OOrderItem();
                    orderItem.setId(oOrderItems.get(0).getId());
                    orderItem.setShopId(update.getShopId());
                    orderItem.setShopType(EnumShopType.PDD.getIndex());
                    orderItem.setSkuNum(itemObject.getString("outerId"));
                    orderItem.setSkuId(itemObject.getString("skuId"));
                    orderItem.setGoodsId(itemObject.getLong("erpGoodsId"));
                    orderItem.setGoodsSkuId(itemObject.getLong("erpGoodsSkuId"));
                    orderItem.setGoodsImg(itemObject.getString("goodsImg"));
                    orderItem.setGoodsSpec(itemObject.getString("goodsSpec"));
                    orderItem.setGoodsTitle(itemObject.getString("goodsName"));
                    orderItem.setGoodsPrice(itemObject.getDouble("goodsPrice"));
                    orderItem.setQuantity(itemObject.getInteger("goodsCount"));
                    if (i == 0) {
                        Double itemAmount = orderItem.getGoodsPrice() * orderItem.getQuantity() - oOrders.get(0).getPlatformDiscount() - oOrders.get(0).getSellerDiscount();
                        orderItem.setItemAmount(itemAmount);
                        orderItem.setPayment(itemAmount);
                    } else {
                        orderItem.setItemAmount(orderItem.getGoodsPrice() * orderItem.getQuantity());
                        orderItem.setPayment(orderItem.getGoodsPrice() * orderItem.getQuantity());
                    }
//                    orderItem.setOrderStatus(orderStatus);
                    orderItem.setRefundStatus(refundStatus);
                    orderItem.setRefundCount(0);
                    orderItem.setUpdateTime(new Date());
                    orderItem.setUpdateBy("ORDER_MESSAGE");
                    orderItemMapper.updateById(orderItem);
                }
            }

            // 更新主订单状态（如果全部退款的话，就更新成订单取消状态）
            List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                    .eq(OOrderItem::getOrderId, update.getId())
                    .eq(OOrderItem::getRefundStatus, 1)
            );
            // 找出没有退款的子订单，如果没有，那么把主订单直接更新成取消状态
            if (oOrderItems == null || oOrderItems.isEmpty()) {
                OOrder orderUpdate = new OOrder();
                orderUpdate.setId(update.getId());
                orderUpdate.setOrderStatus(EnumOOrderStatus.CLOSED.getIndex());
                orderUpdate.setCancelReason("子订单全部退款");
                orderUpdate.setUpdateBy("子订单全部退款");
                orderUpdate.setUpdateTime(new Date());
                orderMapper.updateById(orderUpdate);
            }
            log.info("===========同步pddOrder====成功");
        }
        return ResultVo.success();
    }
}
