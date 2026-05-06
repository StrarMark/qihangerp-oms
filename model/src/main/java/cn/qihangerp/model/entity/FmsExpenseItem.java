package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("fms_expense_item")
public class FmsExpenseItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 费用单ID
     */
    private Long expenseId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 费用金额
     */
    private BigDecimal amount;
    
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
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
}
