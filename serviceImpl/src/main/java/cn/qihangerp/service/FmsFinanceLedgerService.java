package cn.qihangerp.service;

import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.model.entity.FmsFinanceLedger;
import cn.qihangerp.model.query.FinanceLedgerQuery;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FmsFinanceLedgerService extends IService<FmsFinanceLedger> {
    PageResult<FmsFinanceLedger> queryPageList(FinanceLedgerQuery query, PageQuery pageQuery);

    boolean add(FmsFinanceLedger ledger);

    boolean updateLedger(FmsFinanceLedger ledger);

    boolean deleteLedger(Long id);

    FmsFinanceLedger getById(Long id);

    FmsFinanceLedger getByVoucherNo(String voucherNo);
}