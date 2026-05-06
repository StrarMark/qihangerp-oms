package cn.qihangerp.request;

import lombok.Data;

@Data
public class CloudWarehouseShipOrderQueryRequest {

    private String orderNum;
    private String shippingOrderCode;
    private String shippingErpOrderCode;
    private Integer sendStatus;
    private Integer orderStatus;
    private String startTime;
    private String endTime;
//    private Long warehouseId;//云仓id
    private Long shipperId;//云仓id
    private Integer platformId;
    private Integer erpPushStatus;//推送状态0待推送1已推送
    private Integer waybillStatus;//状态0推送失败1推送成功
    private Integer stockingStatus;//状态0待备货1备货中2备货完成
    private Long shopId;
    private Long merchantId;
}
