package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.mapper.ShopMemberMapper;
import cn.qihangerp.service.ShopMemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ShopMember;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shop_member】的数据库操作Service实现
* @createDate 2025-07-18 16:27:49
*/
@Service
public class ShopMemberServiceImpl extends ServiceImpl<ShopMemberMapper, ShopMember>
    implements ShopMemberService {

    @Override
    public PageResult<ShopMember> queryPageList(ShopMember bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopMember> queryWrapper = new LambdaQueryWrapper<ShopMember>()
                .likeRight(StringUtils.hasText(bo.getName()), ShopMember::getName, bo.getName())
                .likeRight(StringUtils.hasText(bo.getPhone()), ShopMember::getPhone, bo.getPhone())
                .eq(bo.getMerchantId() != null, ShopMember::getMerchantId, bo.getMerchantId())
                .eq(bo.getStatus()!=null, ShopMember::getStatus, bo.getStatus())
                .eq(bo.getShopType()!=null, ShopMember::getShopType, bo.getShopType())
                .eq(bo.getShopId() != null, ShopMember::getShopId, bo.getShopId());

        Page<ShopMember> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ShopMember querySingleBy(String mobile, String name) {
        LambdaQueryWrapper<ShopMember> queryWrapper = new LambdaQueryWrapper<ShopMember>()
                .eq(StringUtils.hasText(name), ShopMember::getName, name)
                .or()
                .eq(StringUtils.hasText(mobile), ShopMember::getPhone, mobile);
        var list = this.baseMapper.selectList(queryWrapper);
        if(list.size()>0) return list.get(0);
        else return null;
    }

    @Override
    public ResultVo<Long> addShopMember(ShopMember shopMember) {
        if(StringUtils.isEmpty(shopMember.getPhone())) return ResultVo.error("手机号不能为空");
        if(StringUtils.isEmpty(shopMember.getName())) return ResultVo.error("会员姓号不能为空");
        List<ShopMember> shopMembers = this.baseMapper.selectList(new LambdaQueryWrapper<ShopMember>().eq(ShopMember::getPhone, shopMember.getPhone()));
        if(shopMembers!=null&&shopMembers.size()>0) return ResultVo.error("会员已存在");
        if(shopMember.getShopId()==null) {
            shopMember.setShopId(0L);
            shopMember.setShopType(-1);
        }
        shopMember.setCreateOn(new Date());
        shopMember.setStatus(1);
        this.baseMapper.insert(shopMember);
        return ResultVo.success(shopMember.getId());
    }
}




