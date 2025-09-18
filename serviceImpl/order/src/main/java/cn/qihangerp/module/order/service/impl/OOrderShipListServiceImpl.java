package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OOrderShipList;
import cn.qihangerp.module.order.service.OOrderShipListService;
import cn.qihangerp.module.order.mapper.OOrderShipListMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【o_order_ship_list(发货-备货表（取号发货加入备货清单、分配供应商发货加入备货清单）)】的数据库操作Service实现
* @createDate 2025-05-24 16:03:35
*/
@Service
public class OOrderShipListServiceImpl extends ServiceImpl<OOrderShipListMapper, OOrderShipList>
    implements OOrderShipListService{
    @Override
    public PageResult<OOrderShipList> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrderShipList> queryWrapper = new LambdaQueryWrapper<OOrderShipList>()
                .eq(OOrderShipList::getShipper,1)
                .eq(bo.getShopId()!=null,OOrderShipList::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OOrderShipList::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OOrderShipList::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrderShipList> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




