package cn.qihangerp.oms.tao;


import cn.qihangerp.model.entity.TaoOrder;
import cn.qihangerp.model.entity.TaoOrderItem;
import cn.qihangerp.open.tao.response.TaoOrderDetailResponse;
import cn.qihangerp.open.tao.response.TaoOrderListResponse;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderAssembleHelper {
    /**
     * 组织订单
     * @param trade
     * @return
     */
    public static TaoOrder assembleOrder(TaoOrderListResponse trade) {

        TaoOrder order = new TaoOrder();
        order.setTid(trade.getTid());
        order.setTitle(trade.getTitle());
        order.setType(trade.getType());
        order.setSellerFlag(trade.getSeller_flag() + "");
        order.setHasBuyerMessage(trade.isHas_buyer_message() + "");
        order.setCreditCardFee(trade.getCredit_card_fee());
        order.setCreated(trade.getCreated());
        order.setModified(trade.getModified());
        order.setPayTime(trade.getPay_time());
//            order.setPrice(Double.parseDouble(trade.getPrice()));
        try {
            order.setTotalFee(StringUtils.hasText(trade.getTotal_fee()) ? Double.parseDouble(trade.getTotal_fee()) : null);
            order.setAdjustFee(StringUtils.hasText(trade.getAdjust_fee()) ? Double.parseDouble(trade.getAdjust_fee()) : null);
            order.setPostFee(StringUtils.hasText(trade.getPost_fee()) ? BigDecimal.valueOf(Double.parseDouble(trade.getPost_fee())) : null);
            order.setDiscountFee(StringUtils.hasText(trade.getDiscount_fee()) ? BigDecimal.valueOf(Double.parseDouble(trade.getDiscount_fee())) : null);
            order.setPayment(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
            order.setReceivedPayment(StringUtils.hasText(trade.getReceived_payment()) ? BigDecimal.valueOf(Double.parseDouble(trade.getReceived_payment())) : null);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        order.setBuyerNick(trade.getBuyer_nick());
        order.setBuyerOpenUid(trade.getBuyer_open_uid());
        order.setAlipayNo(trade.getAlipay_no());
//            order.setBuyerAlipayNo(trade.getBuyerAlipayNo());
//            order.setBuyerEmail(trade.getBuyerEmail());
//        order.setBuyerMemo(trade.getBuyerMemo());
//        order.setBuyerMessage(trade.getBuyerMessage());
//        order.setMarkDesc(trade.getMarkDesc());
//        order.setSellerMemo(trade.getSellerMemo());

//        order.setReceiverCountry(trade.getReceiverCountry());
        order.setReceiverState(trade.getReceiver_state());
        order.setReceiverCity(trade.getReceiver_city());
        order.setReceiverDistrict(trade.getReceiver_district());
        order.setReceiverTown(trade.getReceiver_town());
        order.setReceiverAddress(trade.getReceiver_address());
        order.setReceiverName(trade.getReceiver_name());
        order.setReceiverMobile(trade.getReceiver_mobile());
//        order.setReceiverPhone(trade.getReceiverPhone());
        order.setSid(trade.getSid());
//        order.setYfxFee(trade.getYfxFee());
        order.setHasYfx(trade.isHas_yfx() + "");
//            order.setLogisticsInvoiceNo(trade.getinvoice);
//            order.setNumIid(trade.getNumIid() + "");
//            order.setNum(trade.getNum().intValue());
        order.setStatus(trade.getStatus());
//        order.setConsignTime(trade.getConsignTime());
//        order.setEndTime(trade.getEndTime());
        order.setOaid(trade.getOaid());
        List<TaoOrderItem> items = new ArrayList<>();
        for (var item : trade.getOrders()) {
//            TaoOrderItem orderItem = new TaoOrderItem();
//            orderItem.setTitle(item.getTitle());
//                orderItem.setTid(Long.parseLong(trade.getTid()));
//                orderItem.setOid(Long.parseLong(item.getOid()));
//                orderItem.setTotalFee(StringUtils.hasText(item.getTotal_fee()) ? BigDecimal.valueOf(Double.parseDouble(item.getTotal_fee())): BigDecimal.valueOf(0));
//                orderItem.setDiscountFee(StringUtils.hasText(item.getDiscount_fee()) ? BigDecimal.valueOf(Double.parseDouble(item.getDiscount_fee())): BigDecimal.valueOf(0));
//                orderItem.setAdjustFee(StringUtils.hasText(item.getAdjust_fee()) ?BigDecimal.valueOf(Double.parseDouble(item.getAdjust_fee())): BigDecimal.valueOf(0));
//                orderItem.setDivideOrderFee( StringUtils.hasText(item.getDivide_order_fee()) ? Double.parseDouble(item.getDivide_order_fee()):null);
//                orderItem.setPartMjzDiscount(StringUtils.hasText(item.getPart_mjz_discount()) ? Double.parseDouble(item.getPart_mjz_discount()):0.0);
//                orderItem.setPayment(StringUtils.hasText(item.getPayment()) ? Double.parseDouble(item.getPayment()):0.0);
//                orderItem.setPrice(StringUtils.hasText(item.getPrice()) ? BigDecimal.valueOf(Double.parseDouble(item.getPrice())): BigDecimal.valueOf(0));
//                orderItem.setPicPath(item.getPic_path());
//                orderItem.setNumIid(item.getNum_iid()+"");
//                orderItem.setSkuId(item.getSku_id());
////                orderItem.setOuterIid(item.getOuterIid());
//                orderItem.setOuterSkuId(item.getOuter_sku_id());
//                orderItem.setSkuPropertiesName(item.getSku_properties_name());
////                orderItem.setItemMealId(item.getItemMealId() == null ? "" : item.getItemMealId() + "");
////                orderItem.setItemMealName(item.getItemMealName());
//                orderItem.setNum(item.getNum());
//                orderItem.setRefundStatus(item.getRefund_status());
//                orderItem.setStatus(item.getStatus());
////                orderItem.setBuyerRate(item.getBuyerRate() + "");
////                orderItem.setSellerRate(item.getSellerRate() + "");
////                orderItem.setRefundId(item.getRefundId());
//                orderItem.setSellerType(item.getSeller_type());
//                orderItem.setCid(item.getCid());
//                orderItem.setEndTime(item.getEnd_time());
//                orderItem.setConsignTime(item.getConsign_time());
//                orderItem.setShippingType(item.getShipping_type());
//                orderItem.setLogisticsCompany(item.getLogistics_company());
//                orderItem.setInvoiceNo(item.getInvoice_no());
            TaoOrderItem orderItem = new TaoOrderItem();
            orderItem.setTitle(item.getTitle());
            orderItem.setTid(Long.parseLong(trade.getTid()));
            orderItem.setOid(Long.parseLong(item.getOid()));
            orderItem.setTotalFee(StringUtils.hasText(item.getTotalFee()) ? BigDecimal.valueOf(Double.parseDouble(item.getTotalFee())) : BigDecimal.valueOf(0));
            orderItem.setDiscountFee(StringUtils.hasText(item.getDiscountFee()) ? BigDecimal.valueOf(Double.parseDouble(item.getDiscountFee())) : BigDecimal.valueOf(0));
            orderItem.setAdjustFee(StringUtils.hasText(item.getAdjustFee()) ? BigDecimal.valueOf(Double.parseDouble(item.getAdjustFee())) : BigDecimal.valueOf(0));
            orderItem.setDivideOrderFee(StringUtils.hasText(item.getDivideOrderFee()) ? Double.parseDouble(item.getDivideOrderFee()) : null);
            orderItem.setPartMjzDiscount(StringUtils.hasText(item.getPartMjzDiscount()) ? Double.parseDouble(item.getPartMjzDiscount()) : 0.0);
            orderItem.setPayment(StringUtils.hasText(item.getPayment()) ? Double.parseDouble(item.getPayment()) : 0.0);
            orderItem.setPrice(StringUtils.hasText(item.getPrice()) ? Double.parseDouble(item.getPrice()) : 0.0);
            orderItem.setPicPath(item.getPicPath());
            orderItem.setNumIid(item.getNumIid());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setOuterIid(item.getOuterIid());
            orderItem.setOuterSkuId(item.getOuterSkuId());
            orderItem.setSkuPropertiesName(item.getSkuPropertiesName());
            orderItem.setItemMealId(item.getItemMealId());
            orderItem.setItemMealName(item.getItemMealName());
            orderItem.setNum(item.getNum());

            orderItem.setIsShShip(item.getIsShShip()!=null?item.getIsShShip().toString():"");
            orderItem.setRefundStatus(item.getRefundStatus());
            orderItem.setStatus(item.getStatus());
            orderItem.setBuyerRate(item.getBuyerRate() + "");
            orderItem.setSellerRate(item.getSellerRate() + "");
            orderItem.setRefundId(item.getRefundId());
//            orderItem.setSellerType(item.getSeller_type());
            orderItem.setCid(item.getCid());
            orderItem.setEndTime(item.getEndTime());
            orderItem.setConsignTime(item.getConsignTime());
//            orderItem.setShippingType(item.getShippingType());
            orderItem.setLogisticsCompany(item.getLogisticsCompany());
            orderItem.setInvoiceNo(item.getLogisticsCode());
            items.add(orderItem);
        }
        order.setItems(items);
        return order;

    }

    public static TaoOrder assembleOrder(TaoOrderDetailResponse trade) {

        TaoOrder order = new TaoOrder();
        order.setTid(trade.getTid());
        order.setTitle(trade.getTitle());
        order.setType(trade.getType());
        order.setSellerFlag(trade.getSellerFlag() + "");
//        order.setHasBuyerMessage(trade.isHas_buyer_message() + "");
//        order.setCreditCardFee(trade.getCredit_card_fee());
        order.setCreated(trade.getCreated());
        order.setModified(trade.getModified());
        order.setPayTime(trade.getPayTime());
//            order.setPrice(Double.parseDouble(trade.getPrice()));
        try {
            order.setTotalFee(StringUtils.hasText(trade.getTotalFee()) ? Double.parseDouble(trade.getTotalFee()) : null);
            order.setAdjustFee(StringUtils.hasText(trade.getAdjustFee()) ? Double.parseDouble(trade.getAdjustFee()) : null);
            order.setPostFee(StringUtils.hasText(trade.getPostFee()) ? BigDecimal.valueOf(Double.parseDouble(trade.getPostFee())) : null);
            order.setDiscountFee(StringUtils.hasText(trade.getDiscountFee()) ? BigDecimal.valueOf(Double.parseDouble(trade.getDiscountFee())) : null);
            order.setPayment(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
            order.setReceivedPayment(StringUtils.hasText(trade.getReceivedPayment()) ? BigDecimal.valueOf(Double.parseDouble(trade.getReceivedPayment())) : null);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        order.setBuyerNick(trade.getBuyerNick());
        order.setBuyerOpenUid(trade.getBuyerOpenUid());
        order.setAlipayNo(trade.getAlipayNo());
//            order.setBuyerAlipayNo(trade.getBuyerAlipayNo());
//            order.setBuyerEmail(trade.getBuyerEmail());
//        order.setBuyerMemo(trade.getBuyerMemo());
//        order.setBuyerMessage(trade.getBuyerMessage());
//        order.setMarkDesc(trade.getMarkDesc());
//        order.setSellerMemo(trade.getSellerMemo());

//        order.setReceiverCountry(trade.getReceiverCountry());
        order.setReceiverState(trade.getReceiverState());
        order.setReceiverCity(trade.getReceiverCity());
        order.setReceiverDistrict(trade.getReceiverDistrict());
        order.setReceiverTown(trade.getReceiverTown());
        order.setReceiverAddress(trade.getReceiverAddress());
        order.setReceiverName(trade.getReceiverName());
        order.setReceiverMobile(trade.getReceiverMobile());
//        order.setReceiverPhone(trade.getReceiverPhone());
        order.setSid(trade.getSid());
//        order.setYfxFee(trade.getYfxFee());
        order.setHasYfx(trade.getHasYfx() + "");
//            order.setLogisticsInvoiceNo(trade.getinvoice);
//            order.setNumIid(trade.getNumIid() + "");
//            order.setNum(trade.getNum().intValue());
        order.setStatus(trade.getStatus());
//        order.setConsignTime(trade.getConsignTime());
//        order.setEndTime(trade.getEndTime());
        order.setOaid(trade.getOaid());
        List<TaoOrderItem> items = new ArrayList<>();
        for (var item : trade.getOrders().getOrder()) {
            TaoOrderItem orderItem = new TaoOrderItem();
            orderItem.setTitle(item.getTitle());
            orderItem.setTid(Long.parseLong(trade.getTid()));
            orderItem.setOid(Long.parseLong(item.getOid()));
            orderItem.setTotalFee(StringUtils.hasText(item.getTotalFee()) ? BigDecimal.valueOf(Double.parseDouble(item.getTotalFee())): BigDecimal.valueOf(0));
            orderItem.setDiscountFee(StringUtils.hasText(item.getDiscountFee()) ? BigDecimal.valueOf(Double.parseDouble(item.getDiscountFee())): BigDecimal.valueOf(0));
            orderItem.setAdjustFee(StringUtils.hasText(item.getAdjustFee()) ?BigDecimal.valueOf(Double.parseDouble(item.getAdjustFee())): BigDecimal.valueOf(0));
            orderItem.setDivideOrderFee( StringUtils.hasText(item.getDivideOrderFee()) ? Double.parseDouble(item.getDivideOrderFee()):null);
            orderItem.setPartMjzDiscount(StringUtils.hasText(item.getPartMjzDiscount()) ? Double.parseDouble(item.getPartMjzDiscount()):0.0);
            orderItem.setPayment(StringUtils.hasText(item.getPayment()) ? Double.parseDouble(item.getPayment()):0.0);
            orderItem.setPrice(StringUtils.hasText(item.getPrice()) ? Double.parseDouble(item.getPrice()): 0.0);
            orderItem.setPicPath(item.getPicPath());
            orderItem.setNumIid(item.getNumIid());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setOuterIid(item.getOuterIid());
            orderItem.setOuterSkuId(item.getOuterSkuId());
            orderItem.setSkuPropertiesName(item.getSkuPropertiesName());
            orderItem.setItemMealId(item.getItemMealId());
            orderItem.setItemMealName(item.getItemMealName());
            orderItem.setNum(item.getNum());
            orderItem.setIsShShip(item.getIsShShip().toString());
            orderItem.setRefundStatus(item.getRefundStatus());
            orderItem.setStatus(item.getStatus());
            orderItem.setBuyerRate(item.getBuyerRate() + "");
            orderItem.setSellerRate(item.getSellerRate() + "");
            orderItem.setRefundId(item.getRefundId());
//            orderItem.setSellerType(item.getSeller_type());
            orderItem.setCid(item.getCid());
            orderItem.setEndTime(item.getEndTime());
            orderItem.setConsignTime(item.getConsignTime());
//            orderItem.setShippingType(item.getShippingType());
            orderItem.setLogisticsCompany(item.getLogisticsCompany());
            orderItem.setInvoiceNo(item.getLogisticsCode());

            items.add(orderItem);
        }
        order.setItems(items);
        return order;

    }
}
