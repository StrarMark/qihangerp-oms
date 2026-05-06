package cn.qihangerp.model.query;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FinanceLedgerQuery {
    private String voucherNo;
    private String accountType;
    private String sourceType;
    private Long merchantId;
    private Long shopId;
    private Long orderId;
    private String orderNo;
    private Date startDate;
    private Date endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}