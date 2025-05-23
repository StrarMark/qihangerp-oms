package cn.qihangerp.module.order.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.OShipStockUpItem;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_ship_stock_up_item(发货-备货表（打单加入备货清单）)】的数据库操作Service
* @createDate 2025-05-23 21:43:16
*/
public interface OShipStockUpItemService extends IService<OShipStockUpItem> {
    PageResult<OShipStockUpItem> queryWarehousePageList(ShipStockUpBo bo, PageQuery pageQuery);
    PageResult<OShipStockUpItem> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery);
}
