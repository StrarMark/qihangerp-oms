package cn.qihangerp.model.request;

import lombok.Data;

/**
* 第三方云仓发货请求
*/
@Data
public class ThirdPartyCloudWarehouseShipRequest {
    /**
     * 发货单ID
     */
    private Long shipOrderId;
    
    /**
     * 物流单号
     */
    private String logisticsNo;
    
    /**
     * 物流公司
     */
    private String logisticsCompany;
}
