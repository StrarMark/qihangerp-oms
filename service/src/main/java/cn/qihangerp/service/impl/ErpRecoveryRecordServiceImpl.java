package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.request.RecoveryAddRequest;
import com.alibaba.fastjson2.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpRecoveryRecord;
import cn.qihangerp.service.ErpRecoveryRecordService;
import cn.qihangerp.mapper.ErpRecoveryRecordMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
* @author 1
* @description 针对表【erp_recovery_record(回收记录表)】的数据库操作Service实现
* @createDate 2026-04-11 15:03:32
*/
@Service
public class ErpRecoveryRecordServiceImpl extends ServiceImpl<ErpRecoveryRecordMapper, ErpRecoveryRecord>
    implements ErpRecoveryRecordService{

    @Override
    public PageResult<ErpRecoveryRecord> queryPageList(ErpRecoveryRecord bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpRecoveryRecord> queryWrapper = new LambdaQueryWrapper<ErpRecoveryRecord>()
                .eq( bo.getStatus()!=null, ErpRecoveryRecord::getStatus, bo.getStatus())
                .eq( bo.getSettlementType()!=null, ErpRecoveryRecord::getSettlementType, bo.getSettlementType())
                .eq( bo.getSettlementStatus()!=null, ErpRecoveryRecord::getSettlementStatus, bo.getSettlementStatus())
                .eq(StringUtils.isNotBlank(bo.getRecoveryNo()), ErpRecoveryRecord::getRecoveryNo, bo.getRecoveryNo())
                .eq(StringUtils.isNotBlank(bo.getCustomerName()), ErpRecoveryRecord::getCustomerName, bo.getCustomerName())
                .eq(StringUtils.isNotBlank(bo.getCustomerPhone()), ErpRecoveryRecord::getCustomerPhone, bo.getCustomerPhone())
                .eq(bo.getCustomerId() != null, ErpRecoveryRecord::getCustomerId, bo.getCustomerId())
                .eq(bo.getMerchantId() != null, ErpRecoveryRecord::getMerchantId, bo.getMerchantId())
                .gt(bo.getRemainingAmount().doubleValue()>0,ErpRecoveryRecord::getRemainingAmount,0)
                .eq(bo.getShopId() != null, ErpRecoveryRecord::getShopId, bo.getShopId());

        Page<ErpRecoveryRecord> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ResultVo<Long> add(String createBy, RecoveryAddRequest bo) {
        ErpRecoveryRecord recoveryRecord = new ErpRecoveryRecord();
        BeanUtils.copyProperties(bo, recoveryRecord);
        recoveryRecord.setRecoveryNo("REC"+ DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        recoveryRecord.setRecoveryDate(new Date());
        recoveryRecord.setCreatedTime(new Date());
        recoveryRecord.setCreatedBy(createBy);
        recoveryRecord.setSettlementType(0);//未知
        recoveryRecord.setStatus(1);//1-有效，2-已作废
        recoveryRecord.setSettlementStatus(1);//1-未结清，2-已结清
        // 计算总价
        BigDecimal goldPrice = bo.getGoldWeight().multiply(bo.getGoldPrice());
        BigDecimal silverPrice = bo.getSilverWeight().multiply(bo.getSilverPrice());
        recoveryRecord.setTotalAmount(goldPrice.add(silverPrice));
        recoveryRecord.setRemainingAmount(recoveryRecord.getTotalAmount());
        this.save(recoveryRecord);
        return ResultVo.success(recoveryRecord.getId());
    }
}




