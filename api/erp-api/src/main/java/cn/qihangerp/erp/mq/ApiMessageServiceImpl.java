//package cn.qihangerp.erp.mq;
//
//import cn.qihangerp.common.ResultVo;
//import cn.qihangerp.common.enums.EnumShopType;
//import cn.qihangerp.common.mq.MqMessage;
//import cn.qihangerp.common.mq.MqType;
//import cn.qihangerp.common.utils.SpringUtils;
//import cn.qihangerp.module.service.OOrderService;
//import cn.qihangerp.module.service.ORefundService;
//import cn.qihangerp.module.service.ErpShipmentService;
//import cn.qihangerp.module.service.ApiMessageService;
//import cn.qihangerp.erp.feign.OpenApiService;
//import com.alibaba.fastjson2.JSONObject;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@AllArgsConstructor
//@Slf4j
//@Service
//public class ApiMessageServiceImpl implements ApiMessageService {
//    private final OpenApiService openApiService;
////    private final PddApiService pddApiService;
////    private final TaoApiService taoApiService;
////    private final JdApiService jdApiService;
////    private final DouApiService douApiService;
////    private final WeiApiService weiApiService;
//
//    @Override
//    public ResultVo<Integer> messageHandle(MqMessage mqMessage) {
//        if (mqMessage.getMqType() == MqType.ORDER_MESSAGE) {
//            // 有新订单，插入新订单到shop_order
//            OOrderService orderService = SpringUtils.getBean(OOrderService.class);
//            if (mqMessage.getShopType().getIndex() == EnumShopType.JD.getIndex()) {
//                log.info("订单消息JD");
//                JSONObject jsonObject = openApiService.getJdOrderDetail(Long.parseLong(mqMessage.getKeyId()), 0);
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====jdpop order message===没有找到订单");
//                    return ResultVo.error(404, "没有找到订单");
//                }
//                JSONObject orderDetail = jsonObject.getJSONObject("data");
//                orderService.jdOrderMessage(mqMessage.getKeyId(),orderDetail);
//
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.TAO.getIndex()) {
//                log.info("订单消息TAO");
//                JSONObject jsonObject = openApiService.getTaoOrderDetail(mqMessage.getKeyId());
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.error("===查询原始订单错误==={}",jsonObject.getString("msg"));
//                    log.info("=====tao order message===没有找到订单");
//                    return ResultVo.error(404, "没有找到订单");
//                }
//
//                JSONObject orderDetail = jsonObject.getJSONObject("data");
//                orderService.taoOrderMessage(mqMessage.getKeyId(), orderDetail);
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.PDD.getIndex()) {
//                log.info("订单消息PDD");
//                JSONObject jsonObject = openApiService.getPddOrderDetail(mqMessage.getKeyId());
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====pdd order message===没有找到订单:{}",jsonObject.getString("msg"));
//                    return ResultVo.error(404, "没有找到订单");
//                }
//
//                JSONObject orderDetail = jsonObject.getJSONObject("data");
//                ResultVo<Long> longResultVo = orderService.pddOrderMessage(mqMessage.getKeyId(), orderDetail);
//                log.info("===========pdd order message result={}",JSONObject.toJSONString(longResultVo));
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.DOU.getIndex()) {
//                log.info("订单消息DOU");
//                JSONObject jsonObject = openApiService.getDouOrderDetail(mqMessage.getKeyId());
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====dou order message===没有找到订单");
//                    return ResultVo.error(404, "没有找到订单");
//                }
//
//                JSONObject orderDetail = jsonObject.getJSONObject("data");
//                orderService.douOrderMessage(mqMessage.getKeyId(), orderDetail);
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.OFFLINE.getIndex()) {
//                log.info("订单消息OFFLINE");
//                orderService.offlineOrderMessage(mqMessage.getKeyId());
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.WEI.getIndex()) {
//                log.info("订单消息WEI");
//                JSONObject jsonObject = openApiService.getWeiOrderDetail(mqMessage.getKeyId());
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====wei order message===没有找到订单");
//                    return ResultVo.error(404, "没有找到订单");
//                }
//
//                JSONObject orderDetail = jsonObject.getJSONObject("data");
//                orderService.weiOrderMessage(mqMessage.getKeyId(), orderDetail);
//            }
//
//        } else if (mqMessage.getMqType() == MqType.REFUND_MESSAGE) {
//            // 售后消息
//            ORefundService refundService = SpringUtils.getBean(ORefundService.class);
//            if (mqMessage.getShopType().getIndex() == EnumShopType.JD.getIndex()) {
//                log.info("退款消息JD");
////                        log.info("=====jd refund message===消息处理" + serviceId);
//                JSONObject jsonObject = openApiService.getJdRefundDetail(Long.parseLong(mqMessage.getKeyId()), 0);
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====jd refund message===没有找到退款单");
//                    return ResultVo.error(404, "没有找到退款单");
//                }
//
//                JSONObject refundDetail = jsonObject.getJSONObject("data");
//                refundService.jdRefundMessage(mqMessage.getKeyId(), refundDetail);
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.TAO.getIndex()) {
//                log.info("退款消息TAO");
//                JSONObject jsonObject = openApiService.getTaoRefundDetail(Long.parseLong(mqMessage.getKeyId()));
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====tao refund message===没有找到退款单");
//                    return ResultVo.error(404, "没有找到退款单");
//                }
//
//                JSONObject refundDetail = jsonObject.getJSONObject("data");
//                refundService.taoRefundMessage(mqMessage.getKeyId(), refundDetail);
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.PDD.getIndex()) {
//                log.info("退款消息PDD");
//                JSONObject jsonObject = openApiService.getPddRefundDetail(Long.parseLong(mqMessage.getKeyId()));
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====pdd refund message===没有找到退款单");
//                    return ResultVo.error(404, "没有找到退款单");
//                }
//
//                JSONObject refundDetail = jsonObject.getJSONObject("data");
//                refundService.pddRefundMessage(mqMessage.getKeyId(), refundDetail);
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.DOU.getIndex()) {
//                log.info("退款消息DOU");
//                JSONObject jsonObject = openApiService.getDouRefundDetail(mqMessage.getKeyId());
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====dou refund message===没有找到退款单");
//                    return ResultVo.error(404, "没有找到退款单");
//                }
//
//                JSONObject refundDetail = jsonObject.getJSONObject("data");
//                refundService.douRefundMessage(mqMessage.getKeyId(), refundDetail);
//            } else if (mqMessage.getShopType().getIndex() == EnumShopType.WEI.getIndex()) {
//                log.info("退款消息WEI");
//                JSONObject jsonObject = openApiService.getWeiRefundDetail(mqMessage.getKeyId());
//                if (jsonObject.getInteger("code") != 200 || jsonObject.getJSONObject("data") == null) {
//                    log.info("=====dou refund message===没有找到退款单");
//                    return ResultVo.error(404, "没有找到退款单");
//                }
//
//                JSONObject refundDetail = jsonObject.getJSONObject("data");
//                refundService.weiRefundMessage(mqMessage.getKeyId(), refundDetail);
//            }
//        } else if (mqMessage.getMqType() == MqType.SHIP_STOCKUP_MESSAGE) {
//            // 备货消息
//            log.info("=================收到备货消息==============");
//            if (mqMessage.getShopType().getIndex() == EnumShopType.OFFLINE.getIndex()) {
//                ErpShipmentService shipmentService = SpringUtils.getBean(ErpShipmentService.class);
////                shipmentService.shipStockup(mqMessage.getKeyId(), EnumShopType.OFFLINE);
//            }
//        } else if (mqMessage.getMqType() == MqType.SHIP_SEND_MESSAGE) {
//            // 发货消息
//            log.info("=================收到发货消息==============");
//            if (mqMessage.getShopType().getIndex() == EnumShopType.OFFLINE.getIndex()) {
//                ErpShipmentService shipmentService = SpringUtils.getBean(ErpShipmentService.class);
////                shipmentService.shipSendMessage(mqMessage.getKeyId(), EnumShopType.OFFLINE, mqMessage.getData1(), mqMessage.getData2());
////                orderService.offlineOrderMessage(mqMessage.getKeyId());
//            }
//        }
//        return null;
//    }
//}
