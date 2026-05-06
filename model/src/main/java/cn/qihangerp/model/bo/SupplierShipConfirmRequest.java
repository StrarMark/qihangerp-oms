package cn.qihangerp.model.bo;

import lombok.Data;

import java.util.Date;

@Data
public class SupplierShipConfirmRequest {
    private Long orderId;
//    private Long supplierId;
    private String logisticsCompany;
    private String logisticsCode;
    private Date shipTime;
}
