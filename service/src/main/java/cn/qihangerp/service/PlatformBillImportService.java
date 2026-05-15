package cn.qihangerp.service;

import cn.qihangerp.model.dto.ImportResult;
import cn.qihangerp.model.entity.FmsExpense;

import java.util.List;

public interface PlatformBillImportService {

    List<String> queryPlatforms();

    ImportResult saveExpensesFromBillImport(List<FmsExpense> expenses, String operator);
}
