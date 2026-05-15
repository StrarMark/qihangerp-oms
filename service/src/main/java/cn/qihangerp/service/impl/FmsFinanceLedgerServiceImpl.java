package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.mapper.FmsFinanceLedgerMapper;
import cn.qihangerp.model.entity.FmsFinanceLedger;
import cn.qihangerp.model.query.FinanceLedgerQuery;
import cn.qihangerp.service.FmsFinanceLedgerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class FmsFinanceLedgerServiceImpl extends ServiceImpl<FmsFinanceLedgerMapper, FmsFinanceLedger> implements FmsFinanceLedgerService {

    @Override
    public PageResult<FmsFinanceLedger> queryPageList(FinanceLedgerQuery query, PageQuery pageQuery) {
        Page<FmsFinanceLedger> page = pageQuery.build();
        LambdaQueryWrapper<FmsFinanceLedger> wrapper = new LambdaQueryWrapper<>();
        
        if (query != null) {
            if (StringUtils.hasLength(query.getVoucherNo())) {
                wrapper.like(FmsFinanceLedger::getVoucherNo, query.getVoucherNo());
            }
            if (StringUtils.hasLength(query.getAccountType())) {
                wrapper.eq(FmsFinanceLedger::getAccountType, query.getAccountType());
            }
            if (StringUtils.hasLength(query.getSourceType())) {
                wrapper.eq(FmsFinanceLedger::getSourceType, query.getSourceType());
            }
            if (query.getMerchantId() != null) {
                wrapper.eq(FmsFinanceLedger::getMerchantId, query.getMerchantId());
            }
            if (query.getShopId() != null) {
                wrapper.eq(FmsFinanceLedger::getShopId, query.getShopId());
            }
            if (query.getOrderId() != null) {
                wrapper.eq(FmsFinanceLedger::getOrderId, query.getOrderId());
            }
            if (StringUtils.hasLength(query.getOrderNo())) {
                wrapper.like(FmsFinanceLedger::getOrderNo, query.getOrderNo());
            }
            if (query.getStartDate() != null) {
                wrapper.ge(FmsFinanceLedger::getExpenseDate, query.getStartDate());
            }
            if (query.getEndDate() != null) {
                wrapper.le(FmsFinanceLedger::getExpenseDate, query.getEndDate());
            }
        }
        wrapper.orderByDesc(FmsFinanceLedger::getCreatedTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(FmsFinanceLedger ledger) {
        if (ledger.getVoucherNo() == null || ledger.getVoucherNo().isEmpty()) {
            ledger.setVoucherNo(generateVoucherNo());
        }
        ledger.setCreatedTime(new Date());
        return baseMapper.insert(ledger) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLedger(FmsFinanceLedger ledger) {
        ledger.setUpdatedTime(new Date());
        return baseMapper.updateById(ledger) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLedger(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public FmsFinanceLedger getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public FmsFinanceLedger getByVoucherNo(String voucherNo) {
        return baseMapper.selectOne(new LambdaQueryWrapper<FmsFinanceLedger>()
                .eq(FmsFinanceLedger::getVoucherNo, voucherNo));
    }

    private String generateVoucherNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "FLD" + date;
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<FmsFinanceLedger>()
                .likeRight(FmsFinanceLedger::getVoucherNo, prefix)) + 1;
        return prefix + String.format("%05d", count);
    }
}