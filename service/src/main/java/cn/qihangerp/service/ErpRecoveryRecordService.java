package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpRecoveryRecord;
import cn.qihangerp.model.request.RecoveryAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_recovery_record(回收记录表)】的数据库操作Service
* @createDate 2026-04-11 15:03:32
*/
public interface ErpRecoveryRecordService extends IService<ErpRecoveryRecord> {
    PageResult<ErpRecoveryRecord> queryPageList(ErpRecoveryRecord bo, PageQuery pageQuery);
    ResultVo<Long> add(String createBy, RecoveryAddRequest bo);
}
