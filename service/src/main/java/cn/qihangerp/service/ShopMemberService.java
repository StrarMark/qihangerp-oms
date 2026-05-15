package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ShopMember;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shop_member】的数据库操作Service
* @createDate 2025-07-18 16:27:49
*/
public interface ShopMemberService extends IService<ShopMember> {
    PageResult<ShopMember> queryPageList(ShopMember bo, PageQuery pageQuery);
    ShopMember querySingleBy(String mobile,String name);
    ResultVo<Long> addShopMember(ShopMember shopMember);
}
