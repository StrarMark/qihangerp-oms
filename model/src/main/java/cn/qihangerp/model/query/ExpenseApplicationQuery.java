package cn.qihangerp.model.query;

import lombok.Data;

import java.util.Date;

/**
 * 费用管理查询对象
 * @author qihang
 * @date 2026-04-20
 */
@Data
public class ExpenseApplicationQuery {
    /**
     * 申请单号
     */
    private String expenseNo;
    
    /**
     * 费用类型
     */
    private Integer expenseType;
    
    /**
     * 费用来源
     */
    private Integer source;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 结算状态
     */
    private Integer settlementStatus;
    
    /**
     * 费用发生日期开始
     */
    private Date expenseDateStart;
    
    /**
     * 费用发生日期结束
     */
    private Date expenseDateEnd;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 店铺ID
     */
    private Long shopId;
    
    /**
     * 申请人
     */
    private String applicant;
}
