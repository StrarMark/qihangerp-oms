package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("fms_expense")
public class FmsExpense {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String expenseNo;

    /**
     * 费用类型：
     * 1-日常支出, 2-差旅报销,
     * 10-平台扣点, 11-营销费用, 12-包装费用, 13-快递费用, 14-平台服务费, 15-退款费用, 16-税费, 99-其他费用
     */
    private Integer expenseType;

    private Long merchantId;

    private Long shopId;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 申请金额
     */
    private BigDecimal amount;

    /**
     * 费用发生日期
     */
    private Date expenseDate;

    /**
     * 收款方
     */
    private String payee;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：
     * 1-草稿, 2-待审批, 3-已审批, 4-已驳回, 5-已支付
     */
    private Integer status;

    /**
     * 费用来源：
     * 1-手动录入, 2-Excel导入, 3-平台对账单导入
     */
    private Integer source;

    /**
     * 关联订单数
     */
    private Integer orderCount;

    /**
     * 结算状态：0-未结算, 1-已结算
     */
    private Integer settlementStatus;

    /**
     * 结算单ID
     */
    private Long settlementId;

    /**
     * 结算时间
     */
    private Date settlementTime;

    private Long approvalId;

    private Date paidTime;

    private String createdBy;

    private Date createdTime;

    private Date updatedTime;

    @TableField(exist = false)
    private List<FmsExpenseItem> items;
}
