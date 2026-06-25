package cn.qihangerp.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierOrderShipBo {
    private Long id;
    private String logisticsCompany;
    private String logisticsCode;
    private LocalDateTime shipTime;

}
