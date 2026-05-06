package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.SysThirdSystemPush;
import cn.qihangerp.service.SysThirdSystemPushService;
import cn.qihangerp.mapper.SysThirdSystemPushMapper;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【erp_outer_system_push(外部WMS推送记录)】的数据库操作Service实现
* @createDate 2025-07-10 17:12:00
*/
@Service
public class SysThirdSystemPushServiceImpl extends ServiceImpl<SysThirdSystemPushMapper, SysThirdSystemPush>
    implements SysThirdSystemPushService {

    @Override
    public PageResult<SysThirdSystemPush> queryPageList(SysThirdSystemPush bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysThirdSystemPush> queryWrapper = new LambdaQueryWrapper<SysThirdSystemPush>()
                .eq(SysThirdSystemPush::getMerchantId,bo.getMerchantId())
                .eq(bo.getPushType()!=null, SysThirdSystemPush::getPushType,bo.getPushType())
                .eq(bo.getTargetType()!=null, SysThirdSystemPush::getTargetType,bo.getTargetType());

        Page<SysThirdSystemPush> goodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(goodsPage);
    }
}




