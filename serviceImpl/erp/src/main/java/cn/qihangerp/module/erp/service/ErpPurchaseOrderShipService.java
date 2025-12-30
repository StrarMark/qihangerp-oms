package cn.qihangerp.module.erp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.ErpPurchaseOrderShip;
import cn.qihangerp.model.query.PurchaseOrderSearchBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_purchase_order_ship(采购订单物流表)】的数据库操作Service
* @createDate 2025-09-09 10:40:41
*/
public interface ErpPurchaseOrderShipService extends IService<ErpPurchaseOrderShip> {
    PageResult<ErpPurchaseOrderShip> queryPageList(PurchaseOrderSearchBo bo, PageQuery pageQuery);

    int updateScmPurchaseOrderShip(ErpPurchaseOrderShip erpPurchaseOrderShip);

}
