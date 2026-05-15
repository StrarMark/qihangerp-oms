package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.dto.ImportResult;
import cn.qihangerp.model.entity.FmsExpense;
import cn.qihangerp.model.entity.FmsExpenseItem;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.qihangerp.model.query.ExpenseApplicationQuery;

import java.util.List;

/**
 * 费用管理服务接口
 * @author qihang
 * @date 2026-04-20
 */
public interface FmsExpenseService extends IService<FmsExpense> {

    /**
     * 分页查询费用
     * @param query 查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<FmsExpense> queryPageList(ExpenseApplicationQuery query, PageQuery pageQuery);

    /**
     * 添加费用
     * @param expense 费用信息
     * @param items 费用明细
     * @return 是否成功
     */
    boolean addExpense(FmsExpense expense, List<FmsExpenseItem> items);

    /**
     * 更新费用
     * @param expense 费用信息
     * @param items 费用明细
     * @return 是否成功
     */
    boolean updateExpense(FmsExpense expense, List<FmsExpenseItem> items);

    /**
     * 删除费用
     * @param id 费用ID
     * @return 是否成功
     */
    boolean deleteExpense(Long id);

    /**
     * 审核费用
     * @param id 费用ID
     * @param approver 审核人
     * @return 是否成功
     */
    boolean approveExpense(Long id, String approver);

    /**
     * 支付费用
     * @param id 费用ID
     * @param payer 支付人
     * @return 是否成功
     */
    boolean payExpense(Long id, String payer);

    /**
     * 关闭费用
     * @param id 费用ID
     * @return 是否成功
     */
    boolean closeExpense(Long id);

    /**
     * 查询费用明细
     * @param expenseId 费用ID
     * @return 费用明细列表
     */
    List<FmsExpenseItem> queryExpenseItems(Long expenseId);

    /**
     * 批量导入费用（从平台对账单）
     * @param expenses 费用列表
     * @param operator 操作人
     * @return 导入结果
     */
    ImportResult batchImportExpenses(List<FmsExpense> expenses, String operator);
}
