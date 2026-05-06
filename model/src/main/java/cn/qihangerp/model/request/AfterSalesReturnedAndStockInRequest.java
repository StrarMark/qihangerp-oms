package cn.qihangerp.model.request;

import lombok.Data;

@Data
public class AfterSalesReturnedAndStockInRequest {
    private Long id;//o_refund_after_sale 主键ID
//    private Long returnWarehouseId;//退回仓库
    private String returnLogisticsCompany;
    private String returnLogisticsCode;
    private String remark;
}
