package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.SysOpenAuth;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【sys_open_auth(开放接口授权)】的数据库操作Service
* @createDate 2025-05-06 18:46:15
*/
public interface SysOpenAuthService extends IService<SysOpenAuth> {
    PageResult<SysOpenAuth> queryPageList(SysOpenAuth bo, PageQuery pageQuery);
    SysOpenAuth queryByAppKey(String appKey);
    List<SysOpenAuth> getAppKeyByType(Integer type);
}
