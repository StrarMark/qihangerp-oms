package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.response.ShopGoodsSkuStock;
import cn.qihangerp.model.vo.CloudWarehouseGoodsStockVo;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStock;
import cn.qihangerp.model.request.CloudWarehouseGoodsStockRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 1
* @description 针对表【erp_cloud_warehouse_goods_stock(云仓商品库存)】的数据库操作Service
* @createDate 2025-08-09 11:34:34
*/
public interface ErpWarehouseGoodsStockService extends IService<ErpWarehouseGoodsStock> {
    PageResult<ErpWarehouseGoodsStock> queryCloudWarehousePageList(CloudWarehouseGoodsStockRequest bo, PageQuery pageQuery);
    List<CloudWarehouseGoodsStockVo> queryCloudWarehouseList(CloudWarehouseGoodsStockRequest bo);
    PageResult<ErpWarehouseGoodsStock> queryPageList(ErpWarehouseGoodsStock bo, PageQuery pageQuery);

    /**
     * 京东商品库存更新
     * @param stock
     * @param warehouseType
     * @return
     */
    ResultVo saveAndUpdateJD(ErpWarehouseGoodsStock stock, String warehouseType);

    /**
     * 吉客云商品库存
     * @param stock
     * @return
     */
    ResultVo saveAndUpdateJky(ErpWarehouseGoodsStock stock);
    /**
     * API更新库存
     * @param stock
     * @return
     */
    ResultVo<Long> saveAndUpdateAPI(ErpWarehouseGoodsStock stock);

    Integer getGoodsStockQty(Long goodsId);
    /**
     * 搜索店铺库存商品
     * @param code 条码、sku编码
     * @param shopId 店铺id
     * @return
     */
    List<ShopGoodsSkuStock> searchShopGoodsSkuAndStockAndPrice(String code, Long shopId);

}
