package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpWarehouseShop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_cloud_warehouse_shop】的数据库操作Service
* @createDate 2025-07-07 21:22:47
*/
public interface ErpWarehouseShopService extends IService<ErpWarehouseShop> {

    PageResult<ErpWarehouseShop> queryPageList(ErpWarehouseShop query,  PageQuery pageQuery);
    ResultVo saveErpCloudWarehouseShop(List<ErpWarehouseShop> shops);
    ResultVo saveErpCloudWarehouseShop(ErpWarehouseShop shop);

    List<ErpWarehouseShop> getWarehouseShopList(Long warehouseId,Integer shopType,Long merchantId,String ownerNo);
}
