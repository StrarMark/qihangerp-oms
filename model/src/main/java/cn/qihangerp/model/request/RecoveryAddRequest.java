package cn.qihangerp.model.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 回收记录
 *
 */
@Data
public class RecoveryAddRequest {

    /**
     * 
     */
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;

    /**
     * 
     */
    private BigDecimal goldWeight;

    /**
     * 
     */
    private BigDecimal goldPrice;

    /**
     * 
     */
    private BigDecimal silverWeight;

    /**
     * 
     */
    private BigDecimal silverPrice;


    /**
     * 1-仅抵扣，2-仅现金，3-混合
     */
//    private Integer settlementType;

    /**
     * 
     */
    private BigDecimal cashAmount;

    /**
     * 
     */
    private BigDecimal deductedAmount;


    /**
     * 原始销售订单ID
     */
    private Long originalOrderId;
    private String originalOrderNo;

    /**
     * 
     */
    private String remark;
    private Long merchantId;
    private String merchantName;
    private Long shopId;
    private String shopName;

}