package cn.qihangerp.model.query;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanceLedgerQuery {
    private String voucherNo;
    private String accountType;
    private String sourceType;
    private Long merchantId;
    private Long shopId;
    private Long orderId;
    private String orderNo;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}