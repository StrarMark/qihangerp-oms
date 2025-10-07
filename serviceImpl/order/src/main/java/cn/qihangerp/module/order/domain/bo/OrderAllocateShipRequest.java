package cn.qihangerp.module.order.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderAllocateShipRequest {
    private String id;//订单id
    private Long supplierId;//供应商ID
    private BigDecimal purAmount;//采购金额
    private String receiverName;//
    private String receiverMobile;//
    private String address;//
    private String remark;
    private String buyerMemo;
    private String sellerMemo;
}
