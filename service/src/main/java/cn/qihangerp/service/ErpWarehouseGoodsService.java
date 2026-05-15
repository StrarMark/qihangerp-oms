package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpWarehouseGoods;
import cn.qihangerp.model.request.WarehouseGoodsAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_cloud_warehouse_goods】的数据库操作Service
* @createDate 2025-07-07 17:02:48
*/
public interface ErpWarehouseGoodsService extends IService<ErpWarehouseGoods> {
    PageResult<ErpWarehouseGoods> queryPageList(ErpWarehouseGoods bo, PageQuery pageQuery);

    PageResult<ErpWarehouseGoods> queryGoodsAndStockPageList(ErpWarehouseGoods bo, PageQuery pageQuery);
    
    ResultVo saveAndUpdate(ErpWarehouseGoods bo);
    ResultVo saveUpdate(ErpWarehouseGoods bo);

    ErpWarehouseGoods queryByErpGoodsSkuId(Long erpGoodsSkuId, Long warehouseId);

    List<ErpWarehouseGoods> getVendorGoodsSpecByCode(Long merchantId,Long warehouseId, String skuCode);
    List<ErpWarehouseGoods> getVendorGoodsSpecStockByCode(Long merchantId,Long warehouseId, String skuCode);

    /**
     * 查询仓库商品
     * @param erpGoodsNo 商家编码
     * @param warehouseId 仓库ID
     * @return
     */
    ErpWarehouseGoods queryByErpGoodsNoAndWarehouse(String erpGoodsNo,Long warehouseId);

    /**
     * 批量删除商品管理
     *
     * @param ids 需要删除的商品管理主键集合
     * @return 结果
     */
    int deleteGoodsByIds(Long[] ids);

    ResultVo updateGoods(ErpWarehouseGoods bo);

    ResultVo<Long> addGoods(WarehouseGoodsAddRequest bo);
}
