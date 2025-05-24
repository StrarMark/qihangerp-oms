package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OOrderShipListItem;
import cn.qihangerp.module.order.service.OOrderShipListItemService;
import cn.qihangerp.module.order.mapper.OOrderShipListItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【o_order_ship_list_item(发货-备货表（打单加入备货清单）)】的数据库操作Service实现
* @createDate 2025-05-24 16:03:35
*/
@Service
public class OOrderShipListItemServiceImpl extends ServiceImpl<OOrderShipListItemMapper, OOrderShipListItem>
    implements OOrderShipListItemService{

    @Override
    public PageResult<OOrderShipListItem> queryWarehousePageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrderShipListItem> queryWrapper = new LambdaQueryWrapper<OOrderShipListItem>()
                .eq(OOrderShipListItem::getShipper,0)
                .eq(bo.getShopId()!=null,OOrderShipListItem::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OOrderShipListItem::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OOrderShipListItem::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrderShipListItem> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }

    @Override
    public PageResult<OOrderShipListItem> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrderShipListItem> queryWrapper = new LambdaQueryWrapper<OOrderShipListItem>()
                .eq(OOrderShipListItem::getShipper,1)
                .eq(bo.getShopId()!=null,OOrderShipListItem::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OOrderShipListItem::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OOrderShipListItem::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrderShipListItem> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




