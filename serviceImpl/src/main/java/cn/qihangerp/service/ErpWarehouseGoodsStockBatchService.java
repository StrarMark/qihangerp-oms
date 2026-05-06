package cn.qihangerp.service;

import cn.qihangerp.model.vo.GoodsSkuInventoryVo;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStockBatch;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_vendor_inventory_batch(商品库存批次)】的数据库操作Service
* @createDate 2025-06-14 12:00:06
*/
public interface ErpWarehouseGoodsStockBatchService extends IService<ErpWarehouseGoodsStockBatch> {
    List<GoodsSkuInventoryVo> searchSkuInventoryBatch(String keyword);
}
