package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;

import cn.qihangerp.mapper.SysOpenAuthMapper;
import cn.qihangerp.model.entity.SysOpenAuth;
import cn.qihangerp.service.SysOpenAuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author qilip
* @description 针对表【sys_open_auth(开放接口授权)】的数据库操作Service实现
* @createDate 2025-05-06 18:46:15
*/
@Service
public class SysOpenAuthServiceImpl extends ServiceImpl<SysOpenAuthMapper, SysOpenAuth>
    implements SysOpenAuthService {

    @Override
    public PageResult<SysOpenAuth> queryPageList(SysOpenAuth bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOpenAuth> queryWrapper = new LambdaQueryWrapper<SysOpenAuth>()
                .likeRight(StringUtils.hasText(bo.getAppKey()), SysOpenAuth::getAppKey, bo.getAppKey())
                ;

        Page<SysOpenAuth> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public SysOpenAuth queryByAppKey(String appKey) {
        LambdaQueryWrapper<SysOpenAuth> queryWrapper = new LambdaQueryWrapper<SysOpenAuth>()
                .eq(SysOpenAuth::getAppKey, appKey);
        SysOpenAuth sysOpenAuth = this.baseMapper.selectOne(queryWrapper);
        return sysOpenAuth;
    }

    @Override
    public List<SysOpenAuth> getAppKeyByType(Integer type) {
        LambdaQueryWrapper<SysOpenAuth> queryWrapper = new LambdaQueryWrapper<SysOpenAuth>()
                .eq(SysOpenAuth::getType, type);
        return this.baseMapper.selectList(queryWrapper);
    }
}




