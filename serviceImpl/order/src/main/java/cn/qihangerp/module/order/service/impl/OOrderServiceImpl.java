package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.model.entity.OLogisticsCompany;
import cn.qihangerp.model.request.OrderSearchRequest;
import cn.qihangerp.module.goods.domain.OGoods;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.domain.OGoodsSupplier;
import cn.qihangerp.module.goods.mapper.OGoodsMapper;
import cn.qihangerp.module.goods.mapper.OGoodsSkuMapper;
import cn.qihangerp.module.goods.mapper.OGoodsSupplierMapper;
import cn.qihangerp.module.mapper.OLogisticsCompanyMapper;
import cn.qihangerp.module.order.domain.*;
import cn.qihangerp.module.order.domain.bo.OrderAllocateShipRequest;
import cn.qihangerp.module.order.domain.bo.OrderShipRequest;
import cn.qihangerp.module.order.domain.vo.OrderDiscountVo;
import cn.qihangerp.module.order.domain.vo.SalesDailyVo;
import cn.qihangerp.module.order.mapper.*;
import cn.qihangerp.module.order.service.OOrderService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.JdOrderStateEnum;
import cn.qihangerp.common.enums.TaoOrderStateEnum;
import cn.qihangerp.common.utils.DateUtils;
import cn.qihangerp.common.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author qilip
* @description 针对表【o_order(订单表)】的数据库操作Service实现
* @createDate 2024-03-09 13:15:57
*/
@Slf4j
@AllArgsConstructor
@Service
public class OOrderServiceImpl extends ServiceImpl<OOrderMapper, OOrder>
    implements OOrderService {

    private final OOrderMapper orderMapper;
    private final OOrderItemMapper orderItemMapper;
    private final OLogisticsCompanyMapper logisticsCompanyMapper;
    private final OGoodsSkuMapper oGoodsSkuMapper;
    private final OGoodsMapper oGoodsMapper;
    private final OGoodsSupplierMapper supplierMapper;

    private final OOrderShipListMapper orderShipListMapper;
    private final OOrderShipListItemMapper orderShipListItemMapper;

    private final ErpShipmentMapper shipmentMapper;
    private final ErpShipmentItemMapper shipmentItemMapper;

    private final OfflineOrderMapper offlineOrderMapper;
    private final OfflineOrderItemMapper offlineOrderItemMapper;

//    private final PddApiService pddApiService;
//    private final TaoApiService taoApiService;
//    private final JdApiService jdApiService;
//    private final DouApiService douApiService;
//    private final WeiApiService weiApiService;


    private final String DATE_PATTERN =
            "^(?:(?:(?:\\d{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|(?:(?:(?:\\d{2}(?:0[48]|[2468][048]|[13579][26])|(?:(?:0[48]|[2468][048]|[13579][26])00))-0?2-29))$)|(?:(?:(?:\\d{4}-(?:0?[13578]|1[02]))-(?:0?[1-9]|[12]\\d|30))$)|(?:(?:(?:\\d{4}-0?[13-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|30))$)|(?:(?:(?:\\d{2}(?:0[48]|[13579][26]|[2468][048])|(?:(?:0[48]|[13579][26]|[2468][048])00))-0?2-29))$)$";
    private final Pattern DATE_FORMAT = Pattern.compile(DATE_PATTERN);
    @Transactional
    @Override
    public ResultVo<Integer> jdOrderMessage(String orderId,JSONObject orderDetail) {
        log.info("京东订单消息处理"+orderId);
//        JSONObject jsonObject = jdApiService.getOrderDetail(Long.parseLong(orderId),0);
//        if(jsonObject.getInteger("code")!=200 || jsonObject.getJSONObject("data") ==null){
//            log.info("=====jdpop order message===没有找到订单");
//            return ResultVo.error(404,"没有找到订单");
//        }

//        JSONObject orderDetail = jsonObject.getJSONObject("data");
        log.info("=====jdpop order message===订单:"+JSONObject.toJSONString(orderDetail));

        JSONArray itemArray = orderDetail.getJSONArray("items");
        if (itemArray.isEmpty()) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("=====jdpop order message===没有items====事务回滚=======");
        }

//        List<JdOrder> jdOrders = jdOrderMapper.selectList(new LambdaQueryWrapper<JdOrder>().eq(JdOrder::getOrderId, orderId));
//        if(jdOrders == null || jdOrders.size() == 0) {
//            // 没有找到订单信息
//            return ResultVo.error(ResultVoEnum.NotFound,"没有找到JD订单："+orderId);
//        }
//        JdOrder jdOrder = jdOrders.get(0);
        // 状态
        int orderStatus = JdOrderStateEnum.getIndex(orderDetail.getString("orderState"));
        Integer refundStatus=-1;
        if (orderStatus == 11) {
            //已取消的订单
            refundStatus=4;
        } else if (orderStatus == -1) {
            refundStatus=-1;
        } else {
            refundStatus=1;
        }
        // 价格
        double orderSellerPrice = 0.0;
        try{
            orderSellerPrice = StringUtils.isEmpty(orderDetail.getString("orderSellerPrice")) ? 0.0 : Double.parseDouble(orderDetail.getString("orderSellerPrice"));
        }catch (Exception e){}
        double freightPrice =0.0;
        try{
            freightPrice = StringUtils.isEmpty(orderDetail.getString("freightPrice")) ? 0.0 : Double.parseDouble(orderDetail.getString("freightPrice"));
        }catch (Exception e){}
        double orderPayment = 0.0;
        try {
            orderPayment = StringUtils.isEmpty(orderDetail.getString("orderPayment")) ? 0.0 : Double.parseDouble(orderDetail.getString("orderPayment"));
        }catch (Exception e){
        }
        double sellerDiscount= 0.0;
        try {
            sellerDiscount = StringUtils.isEmpty(orderDetail.getString("sellerDiscount")) ? 0.0 : Double.parseDouble(orderDetail.getString("sellerDiscount"));
        }catch (Exception e){
        }
        if(orderSellerPrice == 0.0) orderSellerPrice = orderPayment;
        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum, orderId));
        if(oOrders == null || oOrders.isEmpty()) {
            // 新增订单 12
            OOrder insert = new OOrder();
            insert.setOrderNum(orderId);
            insert.setShopType(EnumShopType.JD.getIndex());
            insert.setShopId(orderDetail.getLong("shopId"));
            insert.setBuyerMemo(orderDetail.getString("orderRemark"));
            insert.setSellerMemo(orderDetail.getString("venderRemark"));
            insert.setRefundStatus(refundStatus);
            insert.setOrderStatus(orderStatus);
            // 价格
            insert.setGoodsAmount(orderSellerPrice);
            insert.setPayment(orderPayment);
            insert.setAmount(orderSellerPrice);
            insert.setPostFee(freightPrice);
            insert.setPlatformDiscount(0.0);
            insert.setSellerDiscount(sellerDiscount);

            insert.setReceiverName(orderDetail.getString("fullname"));
            insert.setReceiverMobile(orderDetail.getString("mobile"));
            insert.setAddress(orderDetail.getString("fullAddress"));
            insert.setProvince(orderDetail.getString("province"));
            insert.setCity(orderDetail.getString("city"));
            insert.setTown(orderDetail.getString("county"));
            insert.setOrderTime(DateUtils.parseDate(orderDetail.getString("orderStartTime")));
            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");

            orderMapper.insert(insert);

            // 添加orderItem
            addJdOrderItem(insert,orderId,insert.getId(),orderStatus,orderSellerPrice,orderPayment,itemArray);
        }else{
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            update.setShopType(EnumShopType.JD.getIndex());
            update.setShopId(orderDetail.getLong("shopId"));
            // 价格
            update.setGoodsAmount(orderSellerPrice);
            update.setPayment(orderPayment);
            update.setAmount(orderSellerPrice);
            update.setPostFee(freightPrice);
            update.setPlatformDiscount(0.0);
            update.setSellerDiscount(sellerDiscount);
            update.setRefundStatus(refundStatus);
            update.setOrderStatus(orderStatus);
            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            orderMapper.updateById(update);

            // 删除orderItem
//            orderItemMapper.delete(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId,update.getId()));
            // 插入orderItem
            addJdOrderItem(update,orderId,update.getId(),orderStatus,orderSellerPrice,orderPayment,itemArray);

        }
        return ResultVo.success();
    }

    private void addJdOrderItem(OOrder oOrder,String orderId,String oOrderId,Integer orderStatus,Double orderSellerPrice,Double orderPayment,JSONArray itemArray ) {
        Double payedItemAmount = 0.0;//已付金额
        for (int i =0;i<itemArray.size();i++) {

            JSONObject itemObject =itemArray.getJSONObject(i);

            // 查询商品库商品
            Long oGoodsId = itemObject.getLong("oGoodsId");
            Long oGoodsSkuId =itemObject.getLong("oGoodsSkuId");
            String skuNum = itemObject.getString("outerSkuId");

            if(oGoodsSkuId<=0){
                // 没有关联商品库商品skuid，查找关联====使用skucode查找
                if(org.springframework.util.StringUtils.hasText(skuNum)) {
                    List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                    if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
                        log.error("同步JD订单没有找到商品库商品SKU");
                    }else{
                        oGoodsId = oGoodsSkus.get(0).getGoodsId();
                        oGoodsSkuId = oGoodsSkus.get(0).getId();
                    }
                }else {
                    log.error("同步JD订单{},原始订单没有填写sku编码信息",oOrder.getOrderNum());
                }
            }else{
                OGoodsSku oGoodsSku = oGoodsSkuMapper.selectById(oGoodsSkuId);
                if(oGoodsSku==null){
                    // 没有关联商品库商品skuid，查找关联====使用skucode查找
                    if(org.springframework.util.StringUtils.hasText(skuNum)) {
                        List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                        if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
                            log.error("同步JD订单没有找到商品库商品SKU");
                        }else{
                            oGoodsId = oGoodsSkus.get(0).getGoodsId();
                            oGoodsSkuId = oGoodsSkus.get(0).getId();
                        }
                    }else {
                        log.error("同步JD订单{},原始订单没有填写sku编码信息",oOrder.getOrderNum());
                    }
                }else{
                    oGoodsId = oGoodsSku.getGoodsId();
                    oGoodsSkuId = oGoodsSku.getId();
                }
            }

            OOrderItem orderItem = new OOrderItem();
            orderItem.setOrderId(oOrder.getId());
            orderItem.setShopType(oOrder.getShopType());
            orderItem.setShopId(oOrder.getShopId());
            orderItem.setOrderNum(oOrder.getOrderNum());
            orderItem.setSubOrderNum(oOrder.getOrderNum()+"-"+itemObject.getString("skuId"));
            // TODO：这里将订单商品skuid转换成erp系统的skuid
//            Long erpGoodsId = 0L;
//            String erpSkuId = "0";
//
//            List<JdGoodsSku> jdGoodsSkus = jdGoodsSkuMapper.selectList(new LambdaQueryWrapper<JdGoodsSku>().eq(JdGoodsSku::getSkuId, item.getSkuId()));
//            if (jdGoodsSkus != null && !jdGoodsSkus.isEmpty()) {
//                erpGoodsId = jdGoodsSkus.get(0).getOGoodsId();
//                erpSkuId = jdGoodsSkus.get(0).getOGoodsSkuId();
//                orderItem.setGoodsImg(jdGoodsSkus.get(0).getLogo());
//                orderItem.setGoodsSpec(jdGoodsSkus.get(0).getSkuName());
//                orderItem.setSkuNum(jdGoodsSkus.get(0).getOuterId());
//            }
            orderItem.setSkuNum(skuNum);
            orderItem.setGoodsId(oGoodsId);
            orderItem.setGoodsSkuId(oGoodsSkuId);
//            orderItem.setSkuNum(itemObject.getString("outerSkuId"));
            orderItem.setSkuId(itemObject.getString("skuId"));
//            orderItem.setGoodsId(itemObject.getLong("oGoodsId"));
//            orderItem.setGoodsSkuId(itemObject.getLong("oGoodsSkuId"));
            orderItem.setGoodsTitle(itemObject.getString("skuName"));
            orderItem.setGoodsPrice(StringUtils.isEmpty(itemObject.getString("jdPrice")) ? 0.0 : Double.parseDouble(itemObject.getString("jdPrice")));
            Integer quantity = itemObject.getInteger("itemTotal");

            // TODO:计算子订单价格 计算公式不对
//            Double orderTotalPrice = Double.parseDouble(jdOrder.getOrderTotalPrice()) * 100;
//            Double orderPayment = Double.parseDouble(jdOrder.getOrderPayment()) * 100;
//            // 折扣比例
//            Double rate = orderPayment / orderTotalPrice;
//
//            // 折扣计算后的价格
//            if ((i+1) != itemArray.size()) {
//                Double itemAmountTmp = orderItem.getGoodsPrice() * rate * quantity;
//                double itemAmount = BigDecimal.valueOf(itemAmountTmp).setScale(2, RoundingMode.HALF_UP).doubleValue();
//                orderItem.setItemAmount(itemAmount);
//                orderItem.setPayment(itemAmount);
//                payedItemAmount += itemAmount;
////                    availableItemAmount = Double.parseDouble(jdOrder.getOrderPayment()) - availableItemAmount;
//            } else {
//                payedItemAmount = Double.parseDouble(jdOrder.getOrderPayment()) - payedItemAmount;
//                orderItem.setItemAmount(payedItemAmount);
//                orderItem.setPayment(payedItemAmount);
//            }

//                    orderItem.setItemAmount(orderItem.getGoodsPrice() *quantity);

            orderItem.setQuantity(quantity);
            if (orderStatus == 11) {
                orderItem.setRefundStatus(4);
                orderItem.setRefundCount(quantity);
            } else if (orderStatus == -1) {
                orderItem.setRefundStatus(-1);
            } else {
                orderItem.setRefundStatus(1);
                orderItem.setRefundCount(0);
            }

            List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                    .eq(OOrderItem::getOrderId, oOrderId)
                    .eq(OOrderItem::getSkuId, itemObject.getString("skuId")));
            if(oOrderItems==null||oOrderItems.isEmpty()){
                //不存在，新增
                orderItem.setCreateTime(new Date());
                orderItem.setCreateBy("ORDER_MESSAGE");
                orderItemMapper.insert(orderItem);
            }else {
                orderItem.setId(oOrderItems.get(0).getId());
                orderItem.setUpdateBy("ORDER_MESSAGE");
                orderItem.setUpdateTime(new Date());
                orderItemMapper.updateById(orderItem);
            }

            orderItem.setCreateTime(new Date());
            orderItem.setCreateBy("ORDER_MESSAGE");
            orderItemMapper.insert(orderItem);
        }

    }


    @Transactional
    @Override
    public ResultVo<Integer> taoOrderMessage(String tid,JSONObject orderDetail ) {
        log.info("Tao订单消息处理"+tid);
//        JSONObject jsonObject = taoApiService.getOrderDetail(tid);
//        if(jsonObject.getInteger("code")!=200 || jsonObject.getJSONObject("data") ==null){
//            log.info("=====tao order message===没有找到订单");
//            return ResultVo.error(404,"没有找到订单");
//        }
//
//        JSONObject orderDetail = jsonObject.getJSONObject("data");
        log.info("=====tao order message===订单:"+JSONObject.toJSONString(orderDetail));

        JSONArray itemArray = orderDetail.getJSONArray("items");
        if (itemArray.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("=====tao order message===没有items====事务回滚=======");
        }
//        List<TaoOrder> taoOrders = taoOrderMapper.selectList(new LambdaQueryWrapper<TaoOrder>().eq(TaoOrder::getTid, tid));
//
//        if(taoOrders == null || taoOrders.size() == 0) {
//            // 没有找到订单信息
//            return ResultVo.error(ResultVoEnum.NotFound,"没有找到TAO订单："+tid);
//        }
//        TaoOrder taoOrder = taoOrders.get(0);
        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum, tid));
        if(oOrders == null || oOrders.isEmpty()) {
            // 新增订单
            OOrder insert = new OOrder();
            insert.setOrderNum(tid);
            insert.setShopType(EnumShopType.TAO.getIndex());
            insert.setShopId(orderDetail.getLong("shopId"));
            String buyerMemo = "";
            if(org.springframework.util.StringUtils.hasText(orderDetail.getString("buyerMessage"))){
                buyerMemo += orderDetail.getString("buyerMessage");
            }
            if(org.springframework.util.StringUtils.hasText(orderDetail.getString("buyerMemo"))){
                buyerMemo += orderDetail.getString("buyerMemo");
            }
            insert.setBuyerMemo(buyerMemo);
            insert.setSellerMemo(orderDetail.getString("sellerMemo"));
            // 状态
            int orderStatus = TaoOrderStateEnum.getIndex(orderDetail.getString("status"));
            if (orderStatus == 11) {
                insert.setRefundStatus(4);
            } else if (orderStatus == -1) {
                insert.setRefundStatus(-1);
            } else {
                insert.setRefundStatus(1);
            }
            insert.setOrderStatus(orderStatus);
            Double goodsAmount  = orderDetail.getDouble("totalFee");
            Double postFee = orderDetail.getDouble("postFee");
            Double payment = orderDetail.getDouble("payment");
            insert.setGoodsAmount(goodsAmount);
            insert.setPostFee(postFee);
            insert.setAmount(payment);
            insert.setPayment(payment);
            insert.setPlatformDiscount(0.0);
            insert.setSellerDiscount(goodsAmount+postFee-payment);
            insert.setReceiverName(orderDetail.getString("receiverName"));
            insert.setReceiverMobile(orderDetail.getString("receiverMobile"));
            insert.setAddress(orderDetail.getString("receiverAddress"));
            insert.setProvince(orderDetail.getString("receiverState"));
            insert.setCity(orderDetail.getString("receiverCity"));
            insert.setTown(orderDetail.getString("receiverDistrict"));
            insert.setOrderTime(orderDetail.getDate("created"));
            // 计算商家优惠
//            Double sellerDiscount = 0.0;
//            List<TaoOrderPromotion> taoOrderPromotions = taoOrderPromotionMapper.selectList(new LambdaQueryWrapper<TaoOrderPromotion>().eq(TaoOrderPromotion::getId, taoOrder.getTid()));
//            if(taoOrderPromotions!=null){
//                for (var it:taoOrderPromotions) {
//                    if(org.springframework.util.StringUtils.hasText(it.getKdDiscountFee())){
//                        try {
//                            sellerDiscount += Double.parseDouble(it.getKdDiscountFee());
//                        }catch (Exception e){}
//                    } else if (org.springframework.util.StringUtils.hasText(it.getDiscountFee())) {
//                        try {
//                            sellerDiscount += Double.parseDouble(it.getDiscountFee());
//                        }catch (Exception e){}
//                    }
//                }
//            }
//            insert.setSellerDiscount(sellerDiscount);


            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");

            orderMapper.insert(insert);

            // 插入orderItem
            addTaoOrderItem(insert,tid,itemArray);

        }else{
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            update.setShopType(EnumShopType.TAO.getIndex());
            update.setShopId(orderDetail.getLong("shopId"));
            String buyerMemo = "";
            if(org.springframework.util.StringUtils.hasText(orderDetail.getString("buyerMessage"))){
                buyerMemo += orderDetail.getString("buyerMessage");
            }
            if(org.springframework.util.StringUtils.hasText(orderDetail.getString("buyerMemo"))){
                buyerMemo += orderDetail.getString("buyerMemo");
            }
            update.setBuyerMemo(buyerMemo);
            update.setSellerMemo(orderDetail.getString("sellerMemo"));
            // 状态
            int orderStatus = TaoOrderStateEnum.getIndex(orderDetail.getString("status"));
            if (orderStatus == 11) {
                update.setRefundStatus(4);
            } else if (orderStatus == -1) {
                update.setRefundStatus(-1);
            } else {
                update.setRefundStatus(1);
            }
            update.setOrderStatus(orderStatus);

            Double goodsAmount  = orderDetail.getDouble("totalFee");
            Double postFee = orderDetail.getDouble("postFee");
            Double payment = orderDetail.getDouble("payment");
            update.setGoodsAmount(goodsAmount);
            update.setPostFee(postFee);
            update.setAmount(payment);
            update.setPayment(payment);
            update.setPlatformDiscount(0.0);
            update.setSellerDiscount(goodsAmount+postFee-payment);
            update.setReceiverName(orderDetail.getString("receiverName"));
            update.setReceiverMobile(orderDetail.getString("receiverMobile"));
            update.setAddress(orderDetail.getString("receiverAddress"));
            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            orderMapper.updateById(update);

            // 插入orderItem
            addTaoOrderItem(update,tid,itemArray);
        }
        return ResultVo.success();
    }

    private void addTaoOrderItem(OOrder oOrder,String tid, JSONArray itemArray) {
        for (int i = 0; i < itemArray.size(); i++) {
            JSONObject itemObject = itemArray.getJSONObject(i);

            // 查询商品库商品
            Long oGoodsId = itemObject.getLong("oGoodsId");
            Long oGoodsSkuId =itemObject.getLong("oGoodsSkuId");
            String skuNum = itemObject.getString("outerSkuId");

            if(oGoodsSkuId<=0){
                // 没有关联商品库商品skuid，查找关联====使用skucode查找
                if(org.springframework.util.StringUtils.hasText(skuNum)) {
                    List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                    if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
                        log.error("同步TAO订单没有找到商品库商品SKU");
                    }else{
                        oGoodsId = oGoodsSkus.get(0).getGoodsId();
                        oGoodsSkuId = oGoodsSkus.get(0).getId();
                    }
                }else {
                    log.error("同步TAO订单{},原始订单没有填写sku编码信息",tid);
                }
            }else{
                OGoodsSku oGoodsSku = oGoodsSkuMapper.selectById(oGoodsSkuId);
                if(oGoodsSku==null){
                    // 没有关联商品库商品skuid，查找关联====使用skucode查找
                    if(org.springframework.util.StringUtils.hasText(skuNum)) {
                        List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                        if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
                            log.error("同步TAO订单没有找到商品库商品SKU");
                        }else{
                            oGoodsId = oGoodsSkus.get(0).getGoodsId();
                            oGoodsSkuId = oGoodsSkus.get(0).getId();
                        }
                    }else {
                        log.error("同步TAO订单{},原始订单没有填写sku编码信息",tid);
                    }
                }else{
                    oGoodsId = oGoodsSku.getGoodsId();
                    oGoodsSkuId = oGoodsSku.getId();
                }
            }

            OOrderItem orderItem = new OOrderItem();
            orderItem.setOrderId(oOrder.getId());
            orderItem.setShopType(oOrder.getShopType());
            orderItem.setShopId(oOrder.getShopId());
            orderItem.setOrderNum(tid);
            orderItem.setSubOrderNum(itemObject.getString("oid"));
            orderItem.setSkuNum(skuNum);
            orderItem.setGoodsId(oGoodsId);
            orderItem.setGoodsSkuId(oGoodsSkuId);
            orderItem.setSkuId(itemObject.getString("skuId"));

            orderItem.setGoodsImg(itemObject.getString("picPath"));
            orderItem.setGoodsSpec(itemObject.getString("skuPropertiesName"));
            orderItem.setGoodsTitle(itemObject.getString("title"));
            orderItem.setGoodsPrice(itemObject.getDouble("price"));
            orderItem.setItemAmount(itemObject.getDouble("totalFee"));
            orderItem.setDiscountAmount(itemObject.getDouble("partMjzDiscount"));
            orderItem.setPayment(itemObject.getDouble("payment"));
            orderItem.setQuantity(itemObject.getInteger("num"));
            // 退款状态。退款状态。可选值 WAIT_SELLER_AGREE(买家已经申请退款，等待卖家同意)
            // WAIT_BUYER_RETURN_GOODS(卖家已经同意退款，等待买家退货)
            // WAIT_SELLER_CONFIRM_GOODS(买家已经退货，等待卖家确认收货)
            // SELLER_REFUSE_BUYER(卖家拒绝退款)
            // CLOSED(退款关闭)
            // SUCCESS(退款成功)
            if (itemObject.getString("refundStatus").equals("WAIT_SELLER_AGREE")
                    || itemObject.getString("refundStatus").equals("WAIT_BUYER_RETURN_GOODS")
                    || itemObject.getString("refundStatus").equals("WAIT_SELLER_CONFIRM_GOODS")
                    || itemObject.getString("refundStatus").equals("SELLER_REFUSE_BUYER")) {
                orderItem.setRefundStatus(2);
                orderItem.setRefundCount(itemObject.getInteger("num"));
            } else if (itemObject.getString("refundStatus").equals("SUCCESS")) {
                orderItem.setRefundCount(itemObject.getInteger("num"));
                orderItem.setRefundStatus(4);
            } else if (itemObject.getString("refundStatus").equals("CLOSED") || itemObject.getString("refundStatus").equals("NO_REFUND")) {
                orderItem.setRefundStatus(1);
                orderItem.setRefundCount(0);
            }
            // 状态
            int orderStatus = TaoOrderStateEnum.getIndex(itemObject.getString("status"));
            orderItem.setOrderStatus(orderStatus);

            List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                    .eq(OOrderItem::getOrderId, oOrder.getId())
                    .eq(OOrderItem::getSkuId, itemObject.getString("skuId")));
            if(oOrderItems==null||oOrderItems.isEmpty()){
                //不存在，新增
                orderItem.setCreateTime(new Date());
                orderItem.setCreateBy("ORDER_MESSAGE");
                orderItemMapper.insert(orderItem);
            }else {
                orderItem.setId(oOrderItems.get(0).getId());
                orderItem.setUpdateBy("ORDER_MESSAGE");
                orderItem.setUpdateTime(new Date());
                orderItemMapper.updateById(orderItem);
            }


        }

    }

    @Transactional
    @Override
    public ResultVo<Integer> pddOrderMessage(String orderSn,JSONObject orderDetail ) {
        log.info("=====pdd order message===订单号{}===" + orderSn);
//        JSONObject jsonObject = pddApiService.getOrderDetail(orderSn);
//        if(jsonObject.getInteger("code")!=200 || jsonObject.getJSONObject("data") ==null){
//            log.info("=====pdd order message===没有找到订单");
//            return ResultVo.error(404,"没有找到订单");
//        }
//
//        JSONObject orderDetail = jsonObject.getJSONObject("data");
        log.info("=====pdd order message===订单:" + JSONObject.toJSONString(orderDetail));
        if (orderDetail == null) return ResultVo.error(404, "没有找到订单");
        JSONArray itemArray = orderDetail.getJSONArray("items");
        if (itemArray.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("=====pdd order message===没有items====事务回滚=======");
        }
//        List<PddOrder> originOrders = pddOrderMapper.selectList(new LambdaQueryWrapper<PddOrder>().eq(PddOrder::getOrderSn, orderSn));
//
//        if(originOrders == null || originOrders.size() == 0) {
//            // 没有找到订单信息
//            return ResultVo.error(ResultVoEnum.NotFound,"没有找到PDD原始订单："+orderSn);
//        }
//        PddOrder originOrder = originOrders.get(0);
//        PddOrder originOrder = new PddOrder();
        OOrder newOrder = new OOrder();
        Integer originOrderStatus = orderDetail.getInteger("orderStatus");
        Integer originRefundStatus = orderDetail.getInteger("refundStatus");
        // 状态 订单状态0：新订单，1：待发货，2：已发货，3：已完成，11已取消；12退款中；21待付款；22锁定，29删除
        int orderStatus = -1;
        int refundStatus = -1;
        if (originRefundStatus == 1) {
            // 没有售后
            orderStatus = originOrderStatus;
            refundStatus = 1;
        } else {
            if (originRefundStatus == 4) {
                refundStatus = 4;
                orderStatus = 11;
            } else {
                refundStatus = originRefundStatus;
                orderStatus = 12;
            }
        }
        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum, orderSn));
        if (oOrders == null || oOrders.isEmpty()) {
            // 新增订单
            OOrder insert = new OOrder();
            insert.setOrderNum(orderSn);
            insert.setShopType(EnumShopType.PDD.getIndex());
            insert.setShopId(orderDetail.getLong("shopId"));
            insert.setBuyerMemo(orderDetail.getString("buyerMemo"));
            insert.setSellerMemo(orderDetail.getString("remark"));

            insert.setRefundStatus(refundStatus);
            insert.setOrderStatus(orderStatus);
            // 价格
            insert.setGoodsAmount(orderDetail.getDouble("goodsAmount"));//.getGoodsAmount());
            insert.setPostFee(orderDetail.getDouble("postage"));
            insert.setAmount(orderDetail.getDouble("payAmount"));
            insert.setPayment(orderDetail.getDouble("payAmount"));
//            double platformDiscount = originOrder.getPlatformDiscount()!=null?originOrder.getPlatformDiscount():0.0;
            insert.setPlatformDiscount(orderDetail.getDouble("platformDiscount"));
//            double sellerDiscount = originOrder.getSellerDiscount()!=null?originOrder.getSellerDiscount():0.0;
            insert.setSellerDiscount(orderDetail.getDouble("sellerDiscount"));

            insert.setReceiverName(orderDetail.getString("receiverNameMask"));
            insert.setReceiverMobile(orderDetail.getString("receiverPhoneMask"));
            insert.setAddress(orderDetail.getString("addressMask"));
            insert.setProvince(orderDetail.getString("province"));
            insert.setCity(orderDetail.getString("city"));
            insert.setTown(orderDetail.getString("town"));
            insert.setOrderTime(DateUtils.parseDate(orderDetail.getString("createdTime")));
            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");

            orderMapper.insert(insert);

            newOrder = insert;
            // 插入orderItem
//            addPddOrderItem(insert.getId(),originOrder.getOrderSn(),orderStatus,refundStatus,platformDiscount,sellerDiscount);


//            for (int i = 0; i < itemArray.size(); i++) {
//                JSONObject itemObject = itemArray.getJSONObject(i);
////                Map<String,Object> itemObject = (Map<String, Object>) itemArray.get(i);
////                JSONObject itemObject = (JSONObject) item;
//
//                // 查询商品库商品
//                Long oGoodsId = itemObject.getLong("oGoodsId");
//                Long oGoodsSkuId =itemObject.getLong("oGoodsSkuId");
//                String skuNum = itemObject.getString("outerSkuId");
//
//                if(oGoodsSkuId<=0){
//                    // 没有关联商品库商品skuid，查找关联====使用skucode查找
//                    if(org.springframework.util.StringUtils.hasText(skuNum)) {
//                        List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
//                        if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
//                            log.error("同步TAO订单没有找到商品库商品SKU");
//                        }else{
//                            oGoodsId = oGoodsSkus.get(0).getGoodsId();
//                            oGoodsSkuId = oGoodsSkus.get(0).getId();
//                        }
//                    }else {
//                        log.error("同步TAO订单{},原始订单没有填写sku编码信息",tid);
//                    }
//                }else{
//                    OGoodsSku oGoodsSku = oGoodsSkuMapper.selectById(oGoodsSkuId);
//                    if(oGoodsSku==null){
//                        // 没有关联商品库商品skuid，查找关联====使用skucode查找
//                        if(org.springframework.util.StringUtils.hasText(skuNum)) {
//                            List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
//                            if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
//                                log.error("同步TAO订单没有找到商品库商品SKU");
//                            }else{
//                                oGoodsId = oGoodsSkus.get(0).getGoodsId();
//                                oGoodsSkuId = oGoodsSkus.get(0).getId();
//                            }
//                        }else {
//                            log.error("同步TAO订单{},原始订单没有填写sku编码信息",tid);
//                        }
//                    }else{
//                        oGoodsId = oGoodsSku.getGoodsId();
//                        oGoodsSkuId = oGoodsSku.getId();
//                    }
//                }
//
//                OOrderItem orderItem = new OOrderItem();
//                orderItem.setOrderId(insert.getId());
//                orderItem.setOrderNum(orderSn);
//                orderItem.setSubOrderNum(orderSn + "-" + itemObject.getString("skuId"));
//                orderItem.setShopType(EnumShopType.PDD.getIndex());
//                orderItem.setShopId(orderDetail.getLong("shopId"));
//                // 这里将订单商品skuid转换成erp系统的skuid
////                Long erpGoodsId = 0L;
////                String erpSkuId = "0";
////
////                List<PddGoodsSku> pddGoodsSku = pddGoodsSkuMapper.selectList(new LambdaQueryWrapper<PddGoodsSku>().eq(PddGoodsSku::getSkuId, item.getSkuId()));
////                if (pddGoodsSku != null && !pddGoodsSku.isEmpty()) {
////                    erpGoodsId = pddGoodsSku.get(0).getOGoodsId();
////                    erpSkuId = pddGoodsSku.get(0).getOGoodsSkuId();
//////                        orderItem.setGoodsImg(taoGoodsSku.get(0).getLogo());
//////                        orderItem.setGoodsSpec(jdGoodsSkus.get(0).getSkuName());
//////                    orderItem.setSkuNum(taoGoodsSku.get(0).getOuterId());
////                }
//                orderItem.setSkuNum(skuNum);
//                orderItem.setGoodsId(oGoodsId);
//                orderItem.setGoodsSkuId(oGoodsSkuId);
////                orderItem.setSkuNum(itemObject.getString("outerId"));
//                orderItem.setSkuId(itemObject.getString("skuId"));
////                orderItem.setGoodsId(itemObject.getLong("ogoodsId"));
////                orderItem.setGoodsSkuId(itemObject.getLong("ogoodsSkuId"));
//                orderItem.setGoodsImg(itemObject.getString("goodsImg"));
//                orderItem.setGoodsSpec(itemObject.getString("goodsSpec"));
//                orderItem.setGoodsTitle(itemObject.getString("goodsName"));
//                orderItem.setGoodsPrice(itemObject.getDouble("goodsPrice"));
//                orderItem.setQuantity(itemObject.getInteger("goodsCount"));
//                if (i == 0) {
//                    Double itemAmount = orderItem.getGoodsPrice() * orderItem.getQuantity() - insert.getPlatformDiscount() - insert.getSellerDiscount();
//                    orderItem.setItemAmount(itemAmount);
//                    orderItem.setPayment(itemAmount);
//                } else {
//                    orderItem.setItemAmount(orderItem.getGoodsPrice() * orderItem.getQuantity());
//                    orderItem.setPayment(orderItem.getGoodsPrice() * orderItem.getQuantity());
//                }
////                orderItem.setPayment(item.getGoodsPrice());
//
//                orderItem.setOrderStatus(orderStatus);
//                orderItem.setRefundStatus(refundStatus);
//                orderItem.setRefundCount(0);
//                orderItem.setCreateTime(new Date());
//                orderItem.setCreateBy("ORDER_MESSAGE");
//                orderItemMapper.insert(orderItem);
//            }


        } else {
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            update.setShopType(EnumShopType.PDD.getIndex());
            update.setShopId(orderDetail.getLong("shopId"));
//            Integer originOrderStatus = orderDetail.getInteger("orderStatus");
//            Integer originRefundStatus = orderDetail.getInteger("refundStatus");
//            // 状态 订单状态0：新订单，1：待发货，2：已发货，3：已完成，11已取消；12退款中；21待付款；22锁定，29删除
//            int orderStatus = -1;
//            int refundStatus = -1;
//            if (originRefundStatus == 1) {
//                // 没有售后
//                orderStatus = originOrderStatus;
//                refundStatus = 1;
//            } else {
//                if (originRefundStatus == 4) {
//                    refundStatus = 4;
//                    orderStatus = 11;
//                } else {
//                    refundStatus = originRefundStatus;
//                    orderStatus = 12;
//                }
//            }
            update.setRefundStatus(refundStatus);
            update.setOrderStatus(orderStatus);

            // 价格
            update.setGoodsAmount(orderDetail.getDouble("goodsAmount"));//.getGoodsAmount());
            update.setPostFee(orderDetail.getDouble("postage"));
            update.setAmount(orderDetail.getDouble("payAmount"));
            update.setPayment(orderDetail.getDouble("payAmount"));
//            double platformDiscount = originOrder.getPlatformDiscount()!=null?originOrder.getPlatformDiscount():0.0;
            update.setPlatformDiscount(orderDetail.getDouble("platformDiscount"));
//            double sellerDiscount = originOrder.getSellerDiscount()!=null?originOrder.getSellerDiscount():0.0;
            update.setSellerDiscount(orderDetail.getDouble("sellerDiscount"));

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
            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            orderMapper.updateById(update);
            newOrder = update;
            // 删除orderItem
//            orderItemMapper.delete(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId,update.getId()));
//            // 插入orderItem
//            addPddOrderItem(update.getId(),originOrder.getOrderSn(),orderStatus,refundStatus,platformDiscount,sellerDiscount);

//            JSONArray itemArray = orderDetail.getJSONArray("items");
//            if (itemArray.isEmpty()) {
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                log.info("=====pdd order message===没有items====事务回滚=======");
//            }


        }

        for (int i = 0; i < itemArray.size(); i++) {
            JSONObject itemObject = itemArray.getJSONObject(i);
            // 查询商品库商品
            Long oGoodsId = itemObject.getLong("ogoodsId");
            Long oGoodsSkuId = itemObject.getLong("ogoodsSkuId");
            String skuNum = itemObject.getString("outerId");

            if (oGoodsSkuId <= 0) {
                // 没有关联商品库商品skuid，查找关联====使用skucode查找
                if (org.springframework.util.StringUtils.hasText(skuNum)) {
                    List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                    if (oGoodsSkus == null || oGoodsSkus.isEmpty()) {
                        log.error("同步PDD订单没有找到商品库商品SKU");
                    } else {
                        oGoodsId = oGoodsSkus.get(0).getGoodsId();
                        oGoodsSkuId = oGoodsSkus.get(0).getId();
                    }
                } else {
                    log.error("同步PDD订单{},原始订单没有填写sku编码信息", newOrder.getOrderNum());
                }
            } else {
                OGoodsSku oGoodsSku = oGoodsSkuMapper.selectById(oGoodsSkuId);
                if (oGoodsSku == null) {
                    // 没有关联商品库商品skuid，查找关联====使用skucode查找
                    if (org.springframework.util.StringUtils.hasText(skuNum)) {
                        List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                        if (oGoodsSkus == null || oGoodsSkus.isEmpty()) {
                            log.error("同步PDD订单没有找到商品库商品SKU");
                        } else {
                            oGoodsId = oGoodsSkus.get(0).getGoodsId();
                            oGoodsSkuId = oGoodsSkus.get(0).getId();
                        }
                    } else {
                        log.error("同步PDD订单{},原始订单没有填写sku编码信息", newOrder.getOrderNum());
                    }
                } else {
                    oGoodsId = oGoodsSku.getGoodsId();
                    oGoodsSkuId = oGoodsSku.getId();
                }
            }
            OOrderItem orderItem = new OOrderItem();
            orderItem.setOrderId(oOrders.get(0).getId());
            orderItem.setOrderNum(orderSn);
            orderItem.setSubOrderNum(orderSn + "-" + itemObject.getString("skuId"));
            orderItem.setShopType(EnumShopType.PDD.getIndex());
            orderItem.setShopId(orderDetail.getLong("shopId"));
//                orderItem.setSkuNum(itemObject.getString("outerId"));
            orderItem.setSkuId(itemObject.getString("skuId"));
            orderItem.setSkuNum(skuNum);
            orderItem.setGoodsId(oGoodsId);
            orderItem.setGoodsSkuId(oGoodsSkuId);
//                orderItem.setGoodsId(itemObject.getLong("ogoodsId"));
//                orderItem.setGoodsSkuId(itemObject.getLong("ogoodsSkuId"));
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
            orderItem.setOrderStatus(orderStatus);
            orderItem.setRefundStatus(refundStatus);
            orderItem.setRefundCount(0);


            List<OOrderItem> oOrderItems = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OOrderItem>()
                            .eq(OOrderItem::getOrderId, oOrders.get(0).getId())
                            .eq(OOrderItem::getSkuId, itemObject.getString("skuId")));
            if (oOrderItems.isEmpty()) {
                // 新增item
                orderItem.setCreateTime(new Date());
                orderItem.setCreateBy("ORDER_MESSAGE");
                orderItemMapper.insert(orderItem);
            } else {
                // 修改、
                orderItem.setUpdateTime(new Date());
                orderItem.setUpdateBy("ORDER_MESSAGE");
                orderItemMapper.updateById(orderItem);
            }
        }
        return ResultVo.success();
    }

//    private void addPddOrderItem(String oOrderId,String orderSn,Integer orderStatus,Integer refundStatus,double platformDiscount,double sellerDiscount){
//        List<PddOrderItem> originOrderItems = pddOrderItemMapper.selectList(new LambdaQueryWrapper<PddOrderItem>().eq(PddOrderItem::getOrderSn, orderSn));
//        if(originOrderItems!=null && originOrderItems.size()>0) {
//            int i = 0;
//            for (var item : originOrderItems) {
//                OOrderItem orderItem = new OOrderItem();
//                orderItem.setOrderId(oOrderId);
//                orderItem.setOrderNum(item.getOrderSn());
//                orderItem.setSubOrderNum(item.getId().toString());
//                // 这里将订单商品skuid转换成erp系统的skuid
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
//                orderItem.setSkuNum(item.getOuterId());
//                orderItem.setSkuId(item.getSkuId());
//                orderItem.setGoodsId(erpGoodsId);
//                orderItem.setGoodsSkuId(erpSkuId);
//                orderItem.setGoodsImg(item.getGoodsImg());
//                orderItem.setGoodsSpec(item.getGoodsSpec());
//                orderItem.setGoodsTitle(item.getGoodsName());
//                orderItem.setGoodsPrice(item.getGoodsPrice());
//                if(i==0) {
//                    Double itemAmount = item.getGoodsPrice() * item.getGoodsCount() - platformDiscount - sellerDiscount;
//                    orderItem.setItemAmount(itemAmount);
//                    orderItem.setPayment(itemAmount);
//                }else {
//                    orderItem.setItemAmount(item.getGoodsPrice());
//                    orderItem.setPayment(item.getGoodsPrice());
//                }
////                orderItem.setPayment(item.getGoodsPrice());
//                orderItem.setQuantity(item.getGoodsCount());
//                orderItem.setOrderStatus(orderStatus);
//                orderItem.setRefundStatus(refundStatus);
//                orderItem.setRefundCount(0);
//                orderItem.setCreateTime(new Date());
//                orderItem.setCreateBy("ORDER_MESSAGE");
//                orderItemMapper.insert(orderItem);
//                i++;
//            }
//        }
//    }

    @Transactional
    @Override
    public ResultVo<Integer> douOrderMessage(String orderId,JSONObject orderDetail ) {
        log.info("Dou订单消息处理" + orderId);
//        JSONObject jsonObject = douApiService.getOrderDetail(orderId);
//        if(jsonObject.getInteger("code")!=200 || jsonObject.getJSONObject("data") ==null){
//            log.info("=====dou order message===没有找到订单");
//            return ResultVo.error(404,"没有找到订单");
//        }
//
//        JSONObject orderDetail = jsonObject.getJSONObject("data");
        log.info("=====dou order message===订单:"+JSONObject.toJSONString(orderDetail));
        if(orderDetail == null) return ResultVo.error(404,"没有找到订单");
        // 状态 订单状态0：新订单，1：待发货，2：已发货，3：已完成，11已取消；12退款中；21待付款；22锁定，29删除，101部分发货
        // 抖店订单状态 1 待确认/待支付（订单创建完毕）105 已支付 2 备货中 101 部分发货 3 已发货（全部发货）4 已取消 5 已完成（已收货）
        int orderStatus = -1;
        int refundStatus = -1;
        if (orderDetail.getInteger("orderStatus") == 1) {
            // 1待确认/待支付（订单创建完毕）
            orderStatus = 21;
            refundStatus = 1;
        } else if (orderDetail.getInteger("orderStatus") == 105) {
            // 105 已支付
            orderStatus = 0;
            refundStatus = 1;
        } else if (orderDetail.getInteger("orderStatus") == 2) {
            // 105 已支付
            orderStatus = 1;
            refundStatus = 1;
        } else if (orderDetail.getInteger("orderStatus") == 101) {
            // 101 部分发货
            orderStatus = 101;
            refundStatus = 1;
        } else if (orderDetail.getInteger("orderStatus") == 3) {
            //  3 已发货（全部发货）
            orderStatus = 2;
            refundStatus = 1;
        } else if (orderDetail.getInteger("orderStatus") == 4) {
            //  4 已取消
            orderStatus = 11;
            refundStatus = 1;
        } else if (orderDetail.getInteger("orderStatus") == 5) {
            //  5 已完成（已收货）
            orderStatus = 3;
            refundStatus = 1;
        }

//        List<DouOrder> originOrders = douOrderMapper.selectList(new LambdaQueryWrapper<DouOrder>().eq(DouOrder::getOrderId, orderId));
//
//        if (originOrders == null || originOrders.size() == 0) {
//            // 没有找到订单信息
//            return ResultVo.error(ResultVoEnum.NotFound, "没有找到DOU原始订单：" + orderId);
//        }
//        DouOrder originOrder = originOrders.get(0);

        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum, orderId));
        if (oOrders == null || oOrders.isEmpty()) {
            // 新增订单
            OOrder insert = new OOrder();
            insert.setOrderNum(orderId);
            insert.setShopType(EnumShopType.DOU.getIndex());
            insert.setShopId(orderDetail.getLong("shopId"));
            insert.setBuyerMemo(orderDetail.getString("buyerWords"));
            insert.setSellerMemo(orderDetail.getString("sellerWords"));
            insert.setRefundStatus(refundStatus);
            insert.setOrderStatus(orderStatus);
            insert.setGoodsAmount(orderDetail.getDouble("orderAmount")  / 100 );
            insert.setPostFee(orderDetail.getDouble("postAmount") / 100);
            insert.setAmount(orderDetail.getDouble("orderAmount")  / 100);
            insert.setPayment(orderDetail.getDouble("payAmount")  / 100);
            insert.setPlatformDiscount(orderDetail.getDouble("promotionPlatformAmount") / 100);
            insert.setSellerDiscount(orderDetail.getDouble("promotionShopAmount") / 100);

            insert.setReceiverName(orderDetail.getString("maskPostReceiver"));
            insert.setReceiverMobile(orderDetail.getString("maskPostTel"));
            insert.setAddress(orderDetail.getString("maskPostAddress"));
            insert.setProvince(orderDetail.getString("provinceName"));
            insert.setCity(orderDetail.getString("cityName"));
            insert.setTown(orderDetail.getString("townName"));
            long time = orderDetail.getLong("createTime") * 1000;
            insert.setOrderTime(new Date(time));
            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");

            orderMapper.insert(insert);
            // 插入orderItem
            addDouOrderItem(insert,insert.getId(), orderId, orderStatus, refundStatus,orderDetail.getJSONArray("items"));

        } else {
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            update.setShopType(EnumShopType.DOU.getIndex());
            update.setShopId(orderDetail.getLong("shopId"));
            update.setRefundStatus(refundStatus);
            update.setOrderStatus(orderStatus);
            update.setGoodsAmount(orderDetail.getDouble("orderAmount")  / 100 );
            update.setPostFee(orderDetail.getDouble("postAmount") / 100);
            update.setAmount(orderDetail.getDouble("orderAmount")  / 100);
            update.setPayment(orderDetail.getDouble("payAmount")  / 100);
            update.setPlatformDiscount(orderDetail.getDouble("promotionPlatformAmount") / 100);
            update.setSellerDiscount(orderDetail.getDouble("promotionShopAmount") / 100);

            update.setReceiverName(orderDetail.getString("maskPostReceiver"));
            update.setReceiverMobile(orderDetail.getString("maskPostTel"));
            update.setAddress(orderDetail.getString("maskPostAddress"));


            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            orderMapper.updateById(update);

            // 删除orderItem
//            orderItemMapper.delete(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, update.getId()));
            // 插入orderItem
            addDouOrderItem(update,update.getId(), orderId, orderStatus, refundStatus,orderDetail.getJSONArray("items"));
        }
        return ResultVo.success();
    }
    private void addDouOrderItem(OOrder oOrder,String oOrderId,String originOrderId,Integer orderStatus,Integer refundStatus,JSONArray itemArray){

        if(itemArray!=null && itemArray.size()>0) {
            for (int i = 0; i < itemArray.size(); i++) {
                JSONObject itemObject = itemArray.getJSONObject(i);
                // 查询商品库商品
                Long oGoodsId = itemObject.getLong("ogoodsId");
                Long oGoodsSkuId =itemObject.getLong("ogoodsSkuId");
                String skuNum = itemObject.getString("outSkuId");

                if(oGoodsSkuId<=0){
                    // 没有关联商品库商品skuid，查找关联====使用skucode查找
                    if(org.springframework.util.StringUtils.hasText(skuNum)) {
                        List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                        if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
                            log.error("同步DOU订单没有找到商品库商品SKU");
                        }else{
                            oGoodsId = oGoodsSkus.get(0).getGoodsId();
                            oGoodsSkuId = oGoodsSkus.get(0).getId();
                        }
                    }else {
                        log.error("同步DOU订单{},原始订单没有填写sku编码信息",oOrder.getOrderNum());
                    }
                }else{
                    OGoodsSku oGoodsSku = oGoodsSkuMapper.selectById(oGoodsSkuId);
                    if(oGoodsSku==null){
                        // 没有关联商品库商品skuid，查找关联====使用skucode查找
                        if(org.springframework.util.StringUtils.hasText(skuNum)) {
                            List<OGoodsSku> oGoodsSkus = oGoodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, skuNum));
                            if(oGoodsSkus==null||oGoodsSkus.isEmpty()){
                                log.error("同步DOU订单没有找到商品库商品SKU");
                            }else{
                                oGoodsId = oGoodsSkus.get(0).getGoodsId();
                                oGoodsSkuId = oGoodsSkus.get(0).getId();
                            }
                        }else {
                            log.error("同步DOU订单{},原始订单没有填写sku编码信息",oOrder.getOrderNum());
                        }
                    }else{
                        oGoodsId = oGoodsSku.getGoodsId();
                        oGoodsSkuId = oGoodsSku.getId();
                    }
                }
                OOrderItem orderItem = new OOrderItem();
                orderItem.setOrderId(oOrder.getId());
                orderItem.setShopId(oOrder.getShopId());
                orderItem.setShopType(oOrder.getShopType());
                orderItem.setOrderNum(itemObject.getString("parentOrderId"));
                orderItem.setSubOrderNum(itemObject.getString("orderId"));
//                orderItem.setSkuNum(itemObject.getString("outSkuId"));
                orderItem.setSkuId(itemObject.getString("skuId"));
//                orderItem.setGoodsId(itemObject.getLong("ogoodsId"));
//                orderItem.setGoodsSkuId(itemObject.getLong("ogoodsSkuId"));
                orderItem.setSkuNum(skuNum);
                orderItem.setGoodsId(oGoodsId);
                orderItem.setGoodsSkuId(oGoodsSkuId);
                orderItem.setGoodsImg(itemObject.getString("productPic"));

//                if(org.springframework.util.StringUtils.hasText(item.getSpec())) {
//                    orderItem.setGoodsSpec(item.getSpec().length()>500?item.getSpec().substring(0,499):item.getSpec());
//                }
                orderItem.setGoodsTitle(itemObject.getString("productName"));
                orderItem.setGoodsSpec(itemObject.getString("spec"));

                orderItem.setGoodsPrice(itemObject.getDouble("goodsPrice")/100);
                orderItem.setItemAmount(itemObject.getDouble("orderAmount")/100);
                orderItem.setPayment(itemObject.getDouble("payAmount")/100);
                orderItem.setQuantity(itemObject.getInteger("itemNum"));
                orderItem.setOrderStatus(orderStatus);
                orderItem.setRefundStatus(refundStatus);
                orderItem.setRefundCount(0);

                List<OOrderItem> oOrderItems = orderItemMapper.selectList(
                        new LambdaQueryWrapper<OOrderItem>()
                                .eq(OOrderItem::getOrderId, oOrder.getId())
                                .eq(OOrderItem::getSkuId, orderItem.getSkuId()));
                if (oOrderItems.isEmpty()) {
                    // 新增item
                    orderItem.setCreateTime(new Date());
                    orderItem.setCreateBy("ORDER_MESSAGE");
                    orderItemMapper.insert(orderItem);
                } else {
                    // 修改、
                    orderItem.setUpdateTime(new Date());
                    orderItem.setUpdateBy("ORDER_MESSAGE");
                    orderItemMapper.updateById(orderItem);
                }
            }
        }
    }

    @Transactional
    @Override
    public ResultVo<Integer> weiOrderMessage(String orderId,JSONObject orderDetail ) {
        log.info("WEI订单消息处理" + orderId);
//        JSONObject jsonObject = weiApiService.getOrderDetail(orderId);
//        if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//            log.info("=====wei order message===没有找到订单");
//            return ResultVo.error(404, "没有找到订单");
//        }

//        JSONObject orderDetail = jsonObject.getJSONObject("data");
        log.info("=====wei order message===订单:" + JSONObject.toJSONString(orderDetail));
        if (orderDetail == null) return ResultVo.error(404, "没有找到订单");


        return ResultVo.success();
    }
    /**
     * 线下订单通知
     * @param
     * @return
     */
    @Transactional
    @Override
    public ResultVo<Integer> offlineOrderMessage(String orderNum) {
        log.info("Offline订单消息处理" + orderNum);
        List<OfflineOrder> originOrders = offlineOrderMapper.selectList(new LambdaQueryWrapper<OfflineOrder>().eq(OfflineOrder::getOrderNum, orderNum));

        if (originOrders == null || originOrders.size() == 0) {
            // 没有找到订单信息
            return ResultVo.error(ResultVoEnum.NotFound, "没有找到OFFLINE原始订单：" + orderNum);
        }
        OfflineOrder originOrder = originOrders.get(0);

        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum, orderNum));
        if (oOrders == null || oOrders.isEmpty()) {
            // 新增订单
            OOrder insert = new OOrder();
            insert.setOrderNum(originOrder.getOrderNum());
            insert.setShopType(EnumShopType.OFFLINE.getIndex());
            insert.setShopId(originOrder.getShopId());
            insert.setBuyerMemo(originOrder.getBuyerMemo());
            insert.setSellerMemo(originOrder.getSellerMemo());
            insert.setRefundStatus(originOrder.getRefundStatus());
            insert.setOrderStatus(originOrder.getOrderStatus());

            insert.setGoodsAmount(originOrder.getGoodsAmount());
            insert.setPostFee(originOrder.getPostFee());
            insert.setAmount(originOrder.getAmount());
            insert.setPayment(originOrder.getPayment());
            insert.setPlatformDiscount(originOrder.getPlatformDiscount());
            insert.setSellerDiscount(originOrder.getSellerDiscount());

            insert.setReceiverName(originOrder.getReceiverName());
            insert.setReceiverMobile(originOrder.getReceiverMobile());
            insert.setAddress(originOrder.getAddress());
            insert.setProvince(originOrder.getProvince());
            insert.setCity(originOrder.getCity());
            insert.setTown(originOrder.getTown());
            insert.setOrderTime(originOrder.getOrderTime());
            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");

            orderMapper.insert(insert);
            // 插入orderItem
            addOfflineOrderItem(insert.getId(), originOrder.getOrderNum(), originOrder.getOrderStatus(), originOrder.getRefundStatus(),insert.getShopId());

            //更新推送状态
            OfflineOrder offlineUpdate = new OfflineOrder();
            offlineUpdate.setId(originOrder.getId());
            offlineUpdate.setOmsPushStatus(1);
            offlineUpdate.setUpdateTime(new Date());
            offlineUpdate.setUpdateBy("推送状态更新");
            offlineOrderMapper.updateById(offlineUpdate);
        } else {
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            update.setShopType(EnumShopType.OFFLINE.getIndex());
            update.setShopId(originOrder.getShopId());
            update.setRefundStatus(originOrder.getRefundStatus());
            update.setOrderStatus(originOrder.getOrderStatus());
            update.setReceiverName(originOrder.getReceiverName());
            update.setReceiverMobile(originOrder.getReceiverMobile());
            update.setAddress(originOrder.getAddress());
            update.setProvince(originOrder.getProvince());
            update.setCity(originOrder.getCity());
            update.setTown(originOrder.getTown());
            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            orderMapper.updateById(update);
        }
        return ResultVo.success();
    }
    private void addOfflineOrderItem(String oOrderId,String originOrderNum,Integer orderStatus,Integer refundStatus,Long shopId){
        List<OfflineOrderItem> originOrderItems = offlineOrderItemMapper.selectList(new LambdaQueryWrapper<OfflineOrderItem>().eq(OfflineOrderItem::getOrderNum, originOrderNum));
        if(originOrderItems!=null && originOrderItems.size()>0) {
            for (var item : originOrderItems) {
                OOrderItem orderItem = new OOrderItem();
                orderItem.setOrderId(oOrderId);
                orderItem.setShopType(EnumShopType.OFFLINE.getIndex());
                orderItem.setShopId(shopId);
                orderItem.setOrderNum(originOrderNum);
                orderItem.setSubOrderNum(item.getSubOrderNum());
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
                orderItem.setGoodsId(item.getGoodsId());
                orderItem.setGoodsSkuId(item.getGoodsSkuId());
                orderItem.setGoodsImg(item.getGoodsImg());
                orderItem.setGoodsSpec(item.getGoodsSpec());
                orderItem.setGoodsTitle(item.getGoodsTitle());
                orderItem.setGoodsPrice(item.getGoodsPrice());
                orderItem.setItemAmount(item.getItemAmount());
                orderItem.setPayment(item.getPayment());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setOrderStatus(orderStatus);
                orderItem.setRefundStatus(refundStatus);
                orderItem.setRefundCount(0);
                orderItem.setCreateTime(new Date());
                orderItem.setCreateBy("ORDER_MESSAGE");
                orderItemMapper.insert(orderItem);
            }
        }
    }
    /**
     * 获取待发货list（去除处理过的）
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OOrder> queryWaitShipmentPageList(OrderSearchRequest bo, PageQuery pageQuery) {

        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()
                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .eq(OOrder::getOrderStatus,1)
                .eq(OOrder::getRefundStatus,1)
                .eq(OOrder::getShipStatus,0)//发货状态 0 待发货 1 已分配供应商发货 2全部发货
//                .lt(ErpOrder::getShipType,2)//ship_type发货方式 0 自己发货1联合发货2供应商发货
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (OOrder order:pages.getRecords()) {
                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                                .eq(OOrderItem::getOrderId, order.getId())
                                .eq(OOrderItem::getShipStatus,0)
//                        .eq(ErpOrderItem::getShipType,0)
                ));
            }
        }

        return PageResult.build(pages);
    }

    /**
     * 查询分配给供应商发货的订单list
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OOrder> queryAssignedShipmentList(OrderSearchRequest bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()
                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .ne(OOrder::getShipper,0)//ship_type发货方 0 自己发货1联合发货2供应商发货
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (OOrder order:pages.getRecords()) {
                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                        .eq(OOrderItem::getOrderId, order.getId())
                        .ne(OOrderItem::getShipper,0)
                ));
//                order.setShipmentList(shipmentMapper.selectList(new LambdaQueryWrapper<ErpShipment>().eq(ErpShipment::getOrderId,order.getId())));
            }
        }

        return PageResult.build(pages);

    }

    /**
     * 已经发货的list（去除分配给供应商发货的）
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OOrder> queryShippedPageList(OrderSearchRequest bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()

                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .eq(bo.getShipType()!=null,OOrder::getShipType,bo.getShipType())
                .eq(OOrder::getShipStatus,2)//发货状态 0 待发货 1 已分配供应商发货 2全部发货

                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (OOrder order:pages.getRecords()) {
                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                        .eq(OOrderItem::getOrderId, order.getId())
                        .eq(OOrderItem::getShipStatus,2)
                ));
//                order.setShipmentList(shipmentMapper.selectList(new LambdaQueryWrapper<ErpShipment>().eq(ErpShipment::getOrderId,order.getId())));
            }
        }

        return PageResult.build(pages);
    }

    @Override
    public List<OOrder> getList(OOrder order) {
        return orderMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public PageResult<OOrder> queryPageList(OrderSearchRequest bo, PageQuery pageQuery) {
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

        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()
                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                .eq(bo.getOrderStatus()!=null,OOrder::getOrderStatus,bo.getOrderStatus())
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime()+" 00:00:00")
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime()+" 23:59:59")

                .eq(org.springframework.util.StringUtils.hasText(bo.getReceiverName()),OOrder::getReceiverName,bo.getReceiverName())
                .like(org.springframework.util.StringUtils.hasText(bo.getReceiverMobile()),OOrder::getReceiverMobile,bo.getReceiverMobile())
                ;

        pageQuery.setOrderByColumn("order_time");
        pageQuery.setIsAsc("desc");
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (var order:pages.getRecords()) {
//                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, order.getId())));
                order.setItemVoList(orderItemMapper.selectOrderItemListByOrderId(Long.parseLong(order.getId())));
            }
        }

        return PageResult.build(pages);
    }

    @Override
    public OOrder queryDetailById(Long id) {
        OOrder oOrder = orderMapper.selectById(id);
        if(oOrder!=null) {
//           oOrder.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, oOrder.getId())));
            oOrder.setItemVoList(orderItemMapper.selectOrderItemListByOrderId(id));
            // 获取优惠信息
            if(oOrder.getShopType()==EnumShopType.TAO.getIndex()){
                oOrder.setDiscounts(orderMapper.getTaoOrderDiscount(oOrder.getOrderNum()));
            } else if (oOrder.getShopType()==EnumShopType.JD.getIndex()) {
                oOrder.setDiscounts(orderMapper.getJdOrderDiscount(oOrder.getOrderNum()));
            }else if (oOrder.getShopType()==EnumShopType.PDD.getIndex()) {
                List<OrderDiscountVo> discountVoList = new ArrayList<>();
                if(oOrder.getPlatformDiscount()!=null&& oOrder.getPlatformDiscount()>0){
                    OrderDiscountVo vo = new OrderDiscountVo();
                    vo.setName("平台优惠");
                    vo.setDiscountAmount(oOrder.getPlatformDiscount().toString());
                    vo.setDescription("平台优惠");
                    discountVoList.add(vo);
                    oOrder.setDiscounts(discountVoList);
                }
            }else if (oOrder.getShopType()==EnumShopType.DOU.getIndex()) {
                List<OrderDiscountVo> discountVoList = new ArrayList<>();
                if(oOrder.getPlatformDiscount()!=null&& oOrder.getPlatformDiscount()>0){
                    OrderDiscountVo vo = new OrderDiscountVo();
                    vo.setName("平台优惠");
                    vo.setDiscountAmount(oOrder.getPlatformDiscount().toString());
                    vo.setDescription("平台优惠");
                    discountVoList.add(vo);
                    oOrder.setDiscounts(discountVoList);
                }
            }
        }

        return oOrder;
    }

    @Override
    public List<OOrder> searchOrderConsignee(String consignee) {
        LambdaQueryWrapper<OOrder> qw = new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderStatus,1).likeRight(OOrder::getReceiverName,consignee);
        return orderMapper.selectList(qw);
    }

    @Override
    public List<OOrderItem> searchOrderItemByReceiverMobile(String receiverMobile) {
        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderStatus, 1).eq(OOrder::getReceiverMobile, receiverMobile));
        List<OOrderItem> orderItemList = new ArrayList<>();
        if(oOrders!=null){
            for (var order:oOrders) {
                orderItemList.addAll(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId,order.getId())));
            }
        }
        return orderItemList;
    }

    @Override
    public List<SalesDailyVo> salesDaily() {
        return orderMapper.salesDaily();
    }
    @Override
    public SalesDailyVo getTodaySalesDaily() {
        return orderMapper.getTodaySalesDaily();
    }

    @Override
    public Integer getWaitShipOrderAllCount() {
        return orderMapper.getWaitShipOrderAllCount();
    }

    /**
     * 手动发货
     * @param shipBo
     * @param createBy
     * @return
     */
    @Transactional
    @Override
    public ResultVo<Integer> manualShipmentOrder(OrderShipRequest shipBo, String createBy) {
        if (org.springframework.util.StringUtils.isEmpty(shipBo.getId()) || shipBo.getId().equals("0"))
            return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数：id");

        OOrder erpOrder = orderMapper.selectById(shipBo.getId());
        if (erpOrder == null) {
            return ResultVo.error("找不到订单数据");
        } else if (erpOrder.getOrderStatus().intValue() != 1 && erpOrder.getRefundStatus().intValue() != 1) {
            return ResultVo.error("订单状态不对，不允许发货");
        }
        if(erpOrder.getShipStatus()!=0){
            return ResultVo.error("订单已分配供应商发货，不允许手动发货");
        }

        OLogisticsCompany erpLogisticsCompany = logisticsCompanyMapper.selectById(shipBo.getShippingCompany());
        if(erpLogisticsCompany==null) return ResultVo.error("快递公司选择错误");

        // 自己发货的list
        List<OOrderItem> oOrderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OOrderItem>()
                        .eq(OOrderItem::getOrderId, erpOrder.getId())
                        .eq(OOrderItem::getShipStatus,0)
                        .eq(OOrderItem::getShipper,0)
        );
        if(oOrderItems==null) return ResultVo.error("订单 item 数据错误，无法发货！");

        // 添加到备货单
        OOrderShipList shipList = new OOrderShipList();
        shipList.setShopId(erpOrder.getShopId());
        shipList.setShopType(erpOrder.getShopType());
        shipList.setShipper(0);
        shipList.setShipSupplierId(0L);
        shipList.setShipSupplier("自由仓库发货");
        shipList.setOrderId(Long.parseLong(erpOrder.getId()));
        shipList.setOrderNum(erpOrder.getOrderNum());
        shipList.setStatus(0);
        shipList.setRemark(erpOrder.getRemark());
        shipList.setBuyerMemo(erpOrder.getBuyerMemo());
        shipList.setSellerMemo(erpOrder.getSellerMemo());
        shipList.setShipLogisticsCompany(erpLogisticsCompany.getName());
        shipList.setShipLogisticsCompanyCode(erpLogisticsCompany.getCode());
        shipList.setShipLogisticsCode(shipBo.getShippingNumber());
        shipList.setShipStatus(2);
        shipList.setCreateTime(new Date());
        shipList.setCreateBy("手动发货");
        orderShipListMapper.insert(shipList);

        // 添加发货记录
        ErpShipment erpShipment = new ErpShipment();
        erpShipment.setShipper(0);//发货方 0 仓库发货 1 供应商发货】
        erpShipment.setShopId(erpOrder.getShopId());
        erpShipment.setShopType(erpOrder.getShopType());
        erpShipment.setOrderId(Long.parseLong(erpOrder.getId()));
        erpShipment.setOrderNum(erpOrder.getOrderNum());
        erpShipment.setOrderTime(erpOrder.getOrderTime());
        erpShipment.setShipType(1);//发货类型（1订单发货2商品补发3商品换货）
        erpShipment.setShipCompany(erpLogisticsCompany.getName());
        erpShipment.setShipCompanyCode(erpLogisticsCompany.getCode());
        erpShipment.setShipCode(shipBo.getShippingNumber());
        erpShipment.setShipFee(shipBo.getShippingCost());
        erpShipment.setShipTime(new Date());
        erpShipment.setShipOperator(shipBo.getShippingMan());
        erpShipment.setShipStatus(1);//物流状态（0 待发货1已发货2已完成）

        erpShipment.setPackageHeight(shipBo.getHeight());
        erpShipment.setPackageWeight(shipBo.getWeight());
        erpShipment.setPackageLength(shipBo.getLength());
        erpShipment.setPackageWidth(shipBo.getWidth());
        erpShipment.setPacksgeOperator(shipBo.getShippingMan());
//        erpShipment.setPackages(JSONObject.toJSONString(oOrderItems));
        erpShipment.setRemark(shipBo.getRemark());
        erpShipment.setCreateBy(createBy);
        erpShipment.setCreateTime(new Date());

        shipmentMapper.insert(erpShipment);

        for(OOrderItem orderItem:oOrderItems){
            // 添加备货清单item
            OOrderShipListItem listItem=new OOrderShipListItem();
            listItem.setShopId(erpOrder.getShopId());
            listItem.setShopType(erpOrder.getShopType());
            listItem.setListId(shipList.getId());
            listItem.setShipper(shipList.getShipper());
            listItem.setShipSupplier(shipList.getShipSupplier());
            listItem.setShipSupplierId(shipList.getShipSupplierId());
            listItem.setOrderId(Long.parseLong(orderItem.getOrderId()));
            listItem.setOrderItemId(Long.parseLong(orderItem.getId()));
            listItem.setOrderNum(orderItem.getOrderNum());
            listItem.setOriginalSkuId(orderItem.getSkuId());
            listItem.setGoodsId(orderItem.getGoodsId());
            listItem.setSkuId(orderItem.getGoodsSkuId());
            listItem.setGoodsTitle(orderItem.getGoodsTitle());
            listItem.setGoodsImg(orderItem.getGoodsImg());
            listItem.setGoodsNum(orderItem.getGoodsNum());
            listItem.setSkuName(orderItem.getGoodsSpec());
            listItem.setSkuNum(orderItem.getSkuNum());
            listItem.setQuantity(orderItem.getQuantity());
            listItem.setStatus(0);//状态0待备货1备货中2备货完成3已发货
            listItem.setCreateBy("手动发货");
            listItem.setCreateTime(new Date());
            orderShipListItemMapper.insert(listItem);
            // 添加发货明细
            ErpShipmentItem erpShipmentItem = new ErpShipmentItem();
            erpShipmentItem.setShipper(erpShipment.getShipper());
            erpShipmentItem.setShopId(erpShipment.getShopId());
            erpShipmentItem.setShopType(erpShipment.getShopType());
            erpShipmentItem.setShipmentId(erpShipment.getId());
            erpShipmentItem.setOrderId(erpShipment.getOrderId());
            erpShipmentItem.setOrderNum(erpShipment.getOrderNum());
            erpShipmentItem.setOrderTime(erpShipment.getOrderTime());
            erpShipmentItem.setOrderItemId(Long.parseLong(orderItem.getId()));
            erpShipmentItem.setErpGoodsId(orderItem.getGoodsId());
            erpShipmentItem.setErpSkuId(orderItem.getGoodsSkuId());
            erpShipmentItem.setGoodsTitle(orderItem.getGoodsTitle());
            erpShipmentItem.setGoodsNum(orderItem.getGoodsNum());
            erpShipmentItem.setGoodsImg(orderItem.getGoodsImg());
            erpShipmentItem.setGoodsSpec(orderItem.getGoodsSpec());
            erpShipmentItem.setSkuNum(orderItem.getSkuNum());
            erpShipmentItem.setQuantity(orderItem.getQuantity());
            erpShipmentItem.setRemark(orderItem.getRemark());
            erpShipmentItem.setStockStatus(0);
            erpShipmentItem.setCreateBy(createBy);
            erpShipmentItem.setCreateTime(new Date());
            shipmentItemMapper.insert(erpShipmentItem);

            // 更新订单item发货状态
            OOrderItem orderItemUpdate = new OOrderItem();
            orderItemUpdate.setId( orderItem.getId());
            orderItemUpdate.setUpdateBy("手动发货");
            orderItemUpdate.setUpdateTime(new Date());
            orderItemUpdate.setShipper(0);
            orderItemUpdate.setShipStatus(2);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
            orderItemUpdate.setShipType(2);//发货方式1电子面单发货2手动发货
            orderItemMapper.updateById(orderItemUpdate);
        }


        // 更新状态、发货方式
        OOrder update = new OOrder();
        update.setId(erpOrder.getId());
        update.setShipper(0);
        update.setShipStatus(2);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
        update.setOrderStatus(2);
        update.setShipType(2);//发货方式1电子面单发货2手动发货

        update.setUpdateTime(new Date());
        update.setUpdateBy("手动发货");
        orderMapper.updateById(update);

        return ResultVo.success();
    }

    /**
     * 分配供应商发货
     * @param shipBo
     * @param createBy
     * @return
     */
    @Override
    public ResultVo<Integer> allocateShipmentOrder(OrderAllocateShipRequest shipBo, String createBy) {
        if (org.springframework.util.StringUtils.isEmpty(shipBo.getId()) || shipBo.getId().equals("0"))
            return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数：id");

        OOrder erpOrder = orderMapper.selectById(shipBo.getId());
        if (erpOrder == null) {
            return ResultVo.error("找不到订单数据");
        } else if (erpOrder.getOrderStatus().intValue() != 1 && erpOrder.getRefundStatus().intValue() != 1) {
            return ResultVo.error("订单状态不对，不允许分配发货");
        }
        if(erpOrder.getShipStatus()!=0){
            return ResultVo.error("订单发货已处理，不允许分配发货");
        }

        List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                .eq(OOrderItem::getOrderId, erpOrder.getId()));
        if(oOrderItems==null) return ResultVo.error("订单 item 数据错误，无法发货！");

        long skuIdZeroCount = oOrderItems.stream().filter(x -> x.getGoodsSkuId() == 0).count();
        if(skuIdZeroCount>0) return ResultVo.error("订单 item 数据中有skuId错误的数据，请补充！");

        // 按 订单明细找出同供应商 分组
        Map<Long,List<OOrderItem>> supplierOrderItemList = new TreeMap<>();
        Map<Long, OGoodsSupplier> supplierList = new TreeMap<>();
        for(OOrderItem orderItem:oOrderItems){
            OGoodsSku erpGoodsSku = oGoodsSkuMapper.selectById(orderItem.getGoodsSkuId());
            if(erpGoodsSku==null) {
                return ResultVo.error("订单明细找不到商品sku信息");
            }
            OGoods erpGoods = oGoodsMapper.selectById(erpGoodsSku.getGoodsId());
            if(erpGoods==null){
                return ResultVo.error("订单明细找不到商品信息");
            }
            OGoodsSupplier erpSupplier = supplierMapper.selectById(erpGoods.getSupplierId());
            if(erpSupplier==null){
                return ResultVo.error("订单明细商品找不到供应商信息");
            }
            // 组合供应商
            boolean isExist = supplierOrderItemList.containsKey(erpSupplier.getId());
            if(isExist){
                supplierOrderItemList.get(erpSupplier.getId()).add(orderItem);
            }else{
                List<OOrderItem> orderItemList = new ArrayList<>();
                orderItemList.add(orderItem);
                supplierOrderItemList.put(Long.parseLong(erpSupplier.getId()),orderItemList);
            }
            supplierList.put(Long.parseLong(erpSupplier.getId()),erpSupplier);
        }

        // 开始组装分配数据
        // 遍历 Map
        for (Map.Entry<Long, List<OOrderItem>> entry : supplierOrderItemList.entrySet()) {
            Long supplierId = entry.getKey();  // 获取键（Long）
            List<OOrderItem> orderItemList = entry.getValue();  // 获取值（List<ErpOrderItem>）

            // 添加分配发货
//            ErpShipment erpShipment = new ErpShipment();
//            erpShipment.setShipper(1);//发货方 0 仓库发货 1 供应商发货】
//            erpShipment.setSupplierId(supplierId);
//            erpShipment.setSupplier(supplierList.get(supplierId)!=null?supplierList.get(supplierId).getName():"");
//            erpShipment.setTenantId(erpOrder.getTenantId());
//            erpShipment.setShopId(erpOrder.getShopId());
//            erpShipment.setShopType(erpOrder.getShopType());
//            erpShipment.setOrderId(erpOrder.getId());
//            erpShipment.setOrderNum(erpOrder.getOrderNum());
//            erpShipment.setOrderTime(erpOrder.getOrderTime());
//            erpShipment.setShipType(1);//发货类型（1订单发货2商品补发3商品换货）
//            erpShipment.setShipCompany("");
//            erpShipment.setShipCompanyCode("");
//            erpShipment.setShipCode("");
//            erpShipment.setShipFee(BigDecimal.ZERO);
//            erpShipment.setShipStatus(0);//物流状态（0 待发货1已发货2已完成）
//
//            erpShipment.setPackageHeight(0.0);
//            erpShipment.setPackageWeight(0.0);
//            erpShipment.setPackageLength(0.0);
//            erpShipment.setPackageWidth(0.0);
//            erpShipment.setPacksgeOperator("");
////        erpShipment.setPackages(JSONObject.toJSONString(oOrderItems));
//            erpShipment.setRemark("");
//            erpShipment.setCreateBy(createBy);
//            erpShipment.setCreateTime(new Date());
//
//            shipmentMapper.insert(erpShipment);

            // 添加分配发货
            OOrderShipList shipList = new OOrderShipList();
            shipList.setShopId(erpOrder.getShopId());
            shipList.setShopType(erpOrder.getShopType());
            shipList.setShipper(1);
            shipList.setShipSupplierId(supplierId);
            shipList.setShipSupplier(supplierList.get(supplierId)!=null?supplierList.get(supplierId).getName():"");
            shipList.setOrderId(Long.parseLong(erpOrder.getId()));
            shipList.setOrderNum(erpOrder.getOrderNum());
            shipList.setStatus(0);
            shipList.setShipLogisticsCompany("");
            shipList.setShipLogisticsCompanyCode("");
            shipList.setShipLogisticsCode("");
            shipList.setShipStatus(1);
            shipList.setReceiverName(shipBo.getReceiverName());
            shipList.setReceiverMobile(shipBo.getReceiverMobile());
            shipList.setProvince(erpOrder.getProvince());
            shipList.setCity(erpOrder.getCity());
            shipList.setTown(erpOrder.getTown());
            shipList.setAddress(shipBo.getAddress());

            shipList.setRemark(erpOrder.getRemark());
            shipList.setSellerMemo(shipBo.getSellerMemo());
            shipList.setBuyerMemo(shipBo.getBuyerMemo());
            shipList.setCreateTime(new Date());
            shipList.setCreateBy("分配供应商发货");
            orderShipListMapper.insert(shipList);

            // 遍历 List<ErpOrderItem>
            for (OOrderItem item : orderItemList) {
                // 打印 List 中的每个 ErpOrderItem 对象
//                ErpShipmentItem erpShipmentItem = new ErpShipmentItem();
//                erpShipmentItem.setSupplierId(erpShipment.getSupplierId());
//                erpShipmentItem.setSupplier(erpShipment.getSupplier());
//                erpShipmentItem.setShipper(erpShipment.getShipper());
//                erpShipmentItem.setTenantId(erpShipment.getTenantId());
//                erpShipmentItem.setShopId(erpShipment.getShopId());
//                erpShipmentItem.setShopType(erpShipment.getShopType());
//                erpShipmentItem.setShipmentId(erpShipment.getId());
//                erpShipmentItem.setOrderId(erpShipment.getOrderId());
//                erpShipmentItem.setOrderNum(erpShipment.getOrderNum());
//                erpShipmentItem.setOrderTime(erpShipment.getOrderTime());
//                erpShipmentItem.setOrderItemId(item.getId());
//                erpShipmentItem.setErpGoodsId(item.getErpGoodsId());
//                erpShipmentItem.setErpSkuId(item.getErpSkuId());
//                erpShipmentItem.setGoodsTitle(item.getGoodsTitle());
//                erpShipmentItem.setGoodsNum(item.getGoodsNum());
//                erpShipmentItem.setGoodsImg(item.getGoodsImg());
//                erpShipmentItem.setGoodsSpec(item.getGoodsSpec());
//                erpShipmentItem.setSkuNum(item.getSkuNum());
//                erpShipmentItem.setQuantity(item.getQuantity());
//                erpShipmentItem.setRemark(item.getRemark());
//                erpShipmentItem.setStockStatus(0);
//                erpShipmentItem.setCreateBy(createBy);
//                erpShipmentItem.setCreateTime(new Date());
//                shipmentItemMapper.insert(erpShipmentItem);
                // 添加备货清单item
                OOrderShipListItem listItem=new OOrderShipListItem();
                listItem.setShopId(erpOrder.getShopId());
                listItem.setShopType(erpOrder.getShopType());
                listItem.setListId(shipList.getId());
                listItem.setShipper(shipList.getShipper());
                listItem.setShipSupplier(shipList.getShipSupplier());
                listItem.setShipSupplierId(shipList.getShipSupplierId());
                listItem.setOrderId(Long.parseLong(item.getOrderId()));
                listItem.setOrderItemId(Long.parseLong(item.getId()));
                listItem.setOrderNum(item.getOrderNum());
                listItem.setOriginalSkuId(item.getSkuId());
                listItem.setGoodsId(item.getGoodsId());
                listItem.setSkuId(item.getGoodsSkuId());
                listItem.setGoodsTitle(item.getGoodsTitle());
                listItem.setGoodsImg(item.getGoodsImg());
                listItem.setGoodsNum(item.getGoodsNum());
                listItem.setSkuName(item.getGoodsSpec());
                listItem.setSkuNum(item.getSkuNum());
                listItem.setQuantity(item.getQuantity());
                listItem.setStatus(0);//状态0待备货1备货中2备货完成3已发货
                listItem.setCreateBy("分配供应商发货");
                listItem.setCreateTime(new Date());
                orderShipListItemMapper.insert(listItem);

                // 更新订单item发货状态
                OOrderItem orderItemUpdate = new OOrderItem();
                orderItemUpdate.setId( item.getId());
                orderItemUpdate.setUpdateBy("分配供应商发货");
                orderItemUpdate.setUpdateTime(new Date());
                orderItemUpdate.setShipStatus(1);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
                orderItemUpdate.setShipper(2);//发货方式 0 自己发货1联合发货2供应商发货
                orderItemMapper.updateById(orderItemUpdate);
            }
        }

//        return ResultVo.error("还没有想好怎么实现！");

//
//        for(ErpOrderItem orderItem:oOrderItems){
//            ErpShipmentItem erpShipmentItem = new ErpShipmentItem();
//            erpShipmentItem.setShipper(erpShipment.getShipper());
//            erpShipmentItem.setTenantId(erpShipment.getTenantId());
//            erpShipmentItem.setShopId(erpShipment.getShopId());
//            erpShipmentItem.setShopType(erpShipment.getShopType());
//            erpShipmentItem.setShipmentId(erpShipment.getId());
//            erpShipmentItem.setOrderId(erpShipment.getOrderId());
//            erpShipmentItem.setOrderNum(erpShipment.getOrderNum());
//            erpShipmentItem.setOrderTime(erpShipment.getOrderTime());
//            erpShipmentItem.setOrderItemId(orderItem.getId());
//            erpShipmentItem.setErpGoodsId(orderItem.getErpGoodsId());
//            erpShipmentItem.setErpSkuId(orderItem.getErpSkuId());
//            erpShipmentItem.setGoodsTitle(orderItem.getGoodsTitle());
//            erpShipmentItem.setGoodsNum(orderItem.getGoodsNum());
//            erpShipmentItem.setGoodsImg(orderItem.getGoodsImg());
//            erpShipmentItem.setGoodsSpec(orderItem.getGoodsSpec());
//            erpShipmentItem.setSkuNum(orderItem.getSkuNum());
//            erpShipmentItem.setQuantity(orderItem.getQuantity());
//            erpShipmentItem.setRemark(orderItem.getRemark());
//            erpShipmentItem.setStockStatus(0);
//            erpShipmentItem.setCreateBy(createBy);
//            erpShipmentItem.setCreateTime(new Date());
//            shipmentItemMapper.insert(erpShipmentItem);
//        }
//
//
        // 更新状态、发货方式
        OOrder update = new OOrder();
        update.setId(erpOrder.getId());
        update.setShipStatus(1);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
        update.setShipper(2);//发发货方式 0 自己发货1联合发货2供应商发货
        update.setUpdateTime(new Date());
        update.setUpdateBy("分配供应商发货");
        orderMapper.updateById(update);

        return ResultVo.success();
    }
}




