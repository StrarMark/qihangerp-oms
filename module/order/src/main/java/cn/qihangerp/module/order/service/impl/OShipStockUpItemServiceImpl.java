package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OShipStockUpItem;
import cn.qihangerp.module.order.service.OShipStockUpItemService;
import cn.qihangerp.module.order.mapper.OShipStockUpItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【o_ship_stock_up_item(发货-备货表（打单加入备货清单）)】的数据库操作Service实现
* @createDate 2025-05-23 21:43:16
*/
@Service
public class OShipStockUpItemServiceImpl extends ServiceImpl<OShipStockUpItemMapper, OShipStockUpItem>
    implements OShipStockUpItemService{

    @Override
    public PageResult<OShipStockUpItem> queryWarehousePageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OShipStockUpItem> queryWrapper = new LambdaQueryWrapper<OShipStockUpItem>()
                .eq(OShipStockUpItem::getShipper,0)
                .eq(bo.getShopId()!=null,OShipStockUpItem::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OShipStockUpItem::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OShipStockUpItem::getOrderNum,bo.getOrderNum())
                ;
        Page<OShipStockUpItem> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
    @Override
    public PageResult<OShipStockUpItem> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OShipStockUpItem> queryWrapper = new LambdaQueryWrapper<OShipStockUpItem>()
                .eq(OShipStockUpItem::getShipper,1)
                .eq(bo.getShopId()!=null,OShipStockUpItem::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OShipStockUpItem::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OShipStockUpItem::getOrderNum,bo.getOrderNum())
                ;
        Page<OShipStockUpItem> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




