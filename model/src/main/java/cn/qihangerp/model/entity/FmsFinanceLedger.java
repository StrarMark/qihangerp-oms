package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("fms_finance_ledger")
public class FmsFinanceLedger {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String voucherNo;

    private String accountType;

    private BigDecimal incomeAmount;

    private BigDecimal expenseAmount;

    private String sourceType;

    private Long sourceId;

    private String sourceNo;

    private Long merchantId;

    private Long shopId;

    private Long orderId;

    private String orderNo;

    private Date expenseDate;

    private String remark;

    private String createdBy;

    private Date createdTime;

    private Date updatedTime;
}