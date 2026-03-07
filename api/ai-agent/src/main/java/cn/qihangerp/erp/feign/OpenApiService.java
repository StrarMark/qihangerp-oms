package cn.qihangerp.erp.feign;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "oms-api")
public interface OpenApiService {
    @GetMapping(value = "/dou/order/get_detail")
    JSONObject getDouOrderDetail(@RequestParam String orderId);

    @GetMapping(value = "/dou/refund/get_detail")
    JSONObject getDouRefundDetail(@RequestParam String id);
    /**
     * 抖店发货
     * @param Token
     * @return
     */
//    @GetMapping(value = "/dou/ship/order_ship")
//    JSONObject shipDouOrder(@RequestHeader(name = "Authorization",required = true) String Token, @RequestBody DouOrderShipBo bo);

//    @GetMapping(value = "/dou/ship/order_ship_multi_pack")
//    JSONObject shipDouOrderMultiPack(@RequestHeader(name = "Authorization",required = true) String Token, @RequestBody DouOrderShipMultiPackBo bo);


    @GetMapping(value = "/jd/order/get_detail")
    JSONObject getJdOrderDetail(@RequestParam Long orderId,@RequestParam Integer vc);

    @GetMapping(value = "/jd/refund/get_detail")
    JSONObject getJdRefundDetail(@RequestParam Long refundId,@RequestParam Integer vc);

    @GetMapping(value = "/pdd/order/get_detail")
    JSONObject getPddOrderDetail(@RequestParam String sn);

    @GetMapping(value = "/pdd/refund/get_detail")
    JSONObject getPddRefundDetail(@RequestParam Long id);

    /**
     * 淘宝发货
     * @param Token
     * @return
     */
//    @GetMapping(value = "/tao/ship/order_ship")
//    JSONObject shipTaoOrder(@RequestHeader(name = "Authorization",required = true) String Token, @RequestBody TaoOrderShipBo bo);

    @GetMapping(value = "/tao/order/get_detail")
    JSONObject getTaoOrderDetail(@RequestParam String tid);

    @GetMapping(value = "/tao/refund/get_detail")
    JSONObject getTaoRefundDetail(@RequestParam Long refundId);

    @GetMapping(value = "/wei/order/get_detail")
    JSONObject getWeiOrderDetail(@RequestParam String orderId);

    @GetMapping(value = "/wei/refund/get_detail")
    JSONObject getWeiRefundDetail(@RequestParam String afterSaleOrderId);

    /**
     * 微信小店发货
     * @param Token
     * @return
     */
//    @GetMapping(value = "/wei/ship/order_ship")
//    JSONObject shipWeiOrder(@RequestHeader(name = "Authorization",required = true) String Token, @RequestBody WeiOrderShipBo bo);
}
