package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.mapper.FmsExpenseItemMapper;
import cn.qihangerp.model.dto.ImportResult;
import cn.qihangerp.model.entity.FmsExpense;
import cn.qihangerp.model.entity.FmsExpenseItem;
import cn.qihangerp.model.query.ExpenseApplicationQuery;
import cn.qihangerp.mapper.FmsExpenseMapper;
import cn.qihangerp.service.FmsExpenseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 费用管理服务实现类
 * @author qihang
 * @date 2026-04-20
 */
@Slf4j
@Service
public class FmsExpenseServiceImpl extends ServiceImpl<FmsExpenseMapper, FmsExpense> implements FmsExpenseService {

    @Autowired
    private FmsExpenseItemMapper expenseItemMapper;

    @Override
    public PageResult<FmsExpense> queryPageList(ExpenseApplicationQuery query, PageQuery pageQuery) {
        Page<FmsExpense> page = pageQuery.build();
        QueryWrapper<FmsExpense> wrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getMerchantId() != null) {
                wrapper.eq("merchant_id", query.getMerchantId());
            }
            if (query.getShopId() != null) {
                wrapper.eq("shop_id", query.getShopId());
            }
            if (query.getExpenseNo() != null) {
                wrapper.like("expense_no", query.getExpenseNo());
            }
            if (query.getExpenseType() != null) {
                wrapper.eq("expense_type", query.getExpenseType());
            }
            if (query.getStatus() != null) {
                wrapper.eq("status", query.getStatus());
            }
            if (query.getApplicant() != null) {
                wrapper.like("applicant", query.getApplicant());
            }
            if (query.getExpenseDateStart() != null) {
                wrapper.ge("expense_date", query.getExpenseDateStart());
            }
            if (query.getExpenseDateEnd() != null) {
                wrapper.le("expense_date", query.getExpenseDateEnd());
            }
        }
        wrapper.orderByDesc("created_time");
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.build(page);
    }

    @Override
    @Transactional
    public boolean addExpense(FmsExpense expense, List<FmsExpenseItem> items) {
        // 生成申请单号
        expense.setExpenseNo("EXP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
        expense.setCreatedTime(new Date());
        expense.setSettlementStatus(0);
        expense.setOrderCount(items != null ? items.size() : 0);

        // 进销存费用默认已审核，支出报销默认待审批
        if (expense.getExpenseType() != null && expense.getExpenseType() >= 10) {
            expense.setStatus(3); // 已审批
        } else {
            expense.setStatus(2); // 待审批
        }

        boolean result = save(expense);

        if (result && items != null && !items.isEmpty()) {
            for (FmsExpenseItem item : items) {
                item.setExpenseId(expense.getId());
                item.setSettlementStatus(0);
                item.setCreateTime(new Date());
                expenseItemMapper.insert(item);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public boolean updateExpense(FmsExpense expense, List<FmsExpenseItem> items) {
        expense.setUpdatedTime(new Date());
        expense.setOrderCount(items != null ? items.size() : 0);
        boolean result = updateById(expense);

        if (result && items != null && !items.isEmpty()) {
            // 删除旧的明细
            QueryWrapper<FmsExpenseItem> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("expense_id", expense.getId());
            expenseItemMapper.delete(deleteWrapper);

            // 插入新的明细
            for (FmsExpenseItem item : items) {
                item.setExpenseId(expense.getId());
                item.setSettlementStatus(0);
                item.setCreateTime(new Date());
                expenseItemMapper.insert(item);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public boolean deleteExpense(Long id) {
        QueryWrapper<FmsExpenseItem> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("expense_id", id);
        expenseItemMapper.delete(deleteWrapper);
        return removeById(id);
    }

    @Override
    public boolean approveExpense(Long id, String approver) {
        FmsExpense expense = getById(id);
        if (expense != null && expense.getStatus() == 2) {
            expense.setStatus(3); // 已审批
            return updateById(expense);
        }
        return false;
    }

    @Override
    public boolean payExpense(Long id, String payer) {
        FmsExpense expense = getById(id);
        if (expense != null && expense.getStatus() == 3) {
            expense.setStatus(5); // 已支付
            expense.setPaidTime(new Date());
            return updateById(expense);
        }
        return false;
    }

    @Override
    public boolean closeExpense(Long id) {
        FmsExpense expense = getById(id);
        if (expense != null && (expense.getStatus() == 1 || expense.getStatus() == 2)) {
            expense.setStatus(4); // 已驳回
            return updateById(expense);
        }
        return false;
    }

    @Override
    public List<FmsExpenseItem> queryExpenseItems(Long expenseId) {
        QueryWrapper<FmsExpenseItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("expense_id", expenseId);
        queryWrapper.orderByDesc("create_time");
        return expenseItemMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public ImportResult batchImportExpenses(List<FmsExpense> expenses, String operator) {
        int successCount = 0;
        int duplicateCount = 0;
        int failCount = 0;
        
        for (FmsExpense expense : expenses) {
            try {
                boolean isDuplicate = false;
                
                if (expense.getItems() != null && !expense.getItems().isEmpty()) {
                    for (FmsExpenseItem item : expense.getItems()) {
                        if (isDuplicateExpense(expense.getExpenseType(), item.getOrderNo(), expense.getAmount())) {
                            isDuplicate = true;
                            break;
                        }
                    }
                }
                
                if (isDuplicate) {
                    duplicateCount++;
                    log.info("跳过重复费用: expenseType={}, orderNo={}, amount={}", 
                        expense.getExpenseType(), 
                        expense.getItems() != null && !expense.getItems().isEmpty() ? expense.getItems().get(0).getOrderNo() : "N/A",
                        expense.getAmount());
                    continue;
                }
                
                expense.setExpenseNo("EXP" + System.currentTimeMillis() + String.format("%04d", successCount));
                expense.setCreatedTime(new Date());
                expense.setCreatedBy(operator);
                expense.setSettlementStatus(0);
                expense.setOrderCount(expense.getItems() != null ? expense.getItems().size() : 0);
                expense.setStatus(3);

                if (save(expense)) {
                    if (expense.getItems() != null && !expense.getItems().isEmpty()) {
                        for (FmsExpenseItem item : expense.getItems()) {
                            item.setExpenseId(expense.getId());
                            item.setSettlementStatus(0);
                            item.setCreateTime(new Date());
                            expenseItemMapper.insert(item);
                        }
                    }
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
                log.error("批量导入费用失败, expenseType={}", expense.getExpenseType(), e);
            }
        }
        
        log.info("批量导入完成: 成功={}, 重复跳过={}, 失败={}", successCount, duplicateCount, failCount);
        
        if (failCount > 0) {
            return ImportResult.fail(successCount, duplicateCount, failCount);
        }
        return ImportResult.success(successCount, duplicateCount);
    }
    
    private boolean isDuplicateExpense(Integer expenseType, String orderNo, BigDecimal amount) {
        if (orderNo == null || orderNo.isEmpty()) {
            return false;
        }
        
        QueryWrapper<FmsExpenseItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq("order_no", orderNo);
        
        List<FmsExpenseItem> items = expenseItemMapper.selectList(itemQuery);
        
        for (FmsExpenseItem item : items) {
            FmsExpense expense = getById(item.getExpenseId());
            if (expense != null && 
                expense.getExpenseType().equals(expenseType) && 
                expense.getAmount().compareTo(amount) == 0) {
                return true;
            }
        }
        
        return false;
    }
}
