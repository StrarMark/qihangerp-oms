package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回收抵扣记录表
 * @TableName erp_recovery_deduction
 */
@TableName(value ="erp_recovery_deduction")
@Data
public class ErpRecoveryDeduction {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long recoveryId;

    /**
     * 销售订单ID
     */
    private Long orderId;

    /**
     * 
     */
    private BigDecimal deductionAmount;

    /**
     * 
     */
    private Date deductionTime;

    /**
     * 
     */
    private String createdBy;
}