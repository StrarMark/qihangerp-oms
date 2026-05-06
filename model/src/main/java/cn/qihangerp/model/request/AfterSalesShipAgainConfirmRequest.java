package cn.qihangerp.model.request;

import lombok.Data;

@Data
public class AfterSalesShipAgainConfirmRequest {
    private Long id;//o_refund_after_sale 主键ID
//    private Long reissueWarehouseId;//补发仓库
    private String receiverName;//收件人
    private String receiverTel;//收件人电话
    private String receiverAddress;//收件人地址
    private String remark;
}
