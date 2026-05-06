package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回收记录表
 * @TableName erp_recovery_record
 */
@TableName(value ="erp_recovery_record")
@Data
public class ErpRecoveryRecord {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 回收单号
     */
    private String recoveryNo;

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
    private Long merchantId;
    private String merchantName;

    /**
     * 
     */
    private Long shopId;
    private String shopName;

    /**
     * 
     */
    private Date recoveryDate;

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
     * 金重*金价+银重*银价
     */
    private BigDecimal totalAmount;

    /**
     * 1-仅抵扣，2-仅现金，3-混合
     */
    private Integer settlementType;

    /**
     * 
     */
    private BigDecimal cashAmount;

    /**
     * 
     */
    private BigDecimal deductedAmount;

    /**
     * 
     */
    private BigDecimal remainingAmount;

    /**
     * 1-有效，2-已作废
     */
    private Integer status;

    /**
     * 1-未结清，2-已结清
     */
    private Integer settlementStatus;

    /**
     * 原始销售订单ID
     */
    private Long originalOrderId;
    private String originalOrderNo;

    /**
     * 
     */
    private String remark;

    /**
     * 
     */
    private String createdBy;

    /**
     * 
     */
    private Date createdTime;

    /**
     * 
     */
    private Date updatedTime;
}