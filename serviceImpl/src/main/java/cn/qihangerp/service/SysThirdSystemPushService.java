package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.SysThirdSystemPush;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_outer_system_push(外部WMS推送记录)】的数据库操作Service
* @createDate 2025-07-10 17:12:00
*/
public interface SysThirdSystemPushService extends IService<SysThirdSystemPush> {
    PageResult<SysThirdSystemPush> queryPageList(SysThirdSystemPush bo, PageQuery pageQuery);
}
