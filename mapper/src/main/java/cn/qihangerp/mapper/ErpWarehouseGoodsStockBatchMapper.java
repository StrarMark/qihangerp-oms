package cn.qihangerp.mapper;

import cn.qihangerp.model.vo.GoodsSkuInventoryVo;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStockBatch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_vendor_inventory_batch(商品库存批次)】的数据库操作Mapper
* @createDate 2025-06-14 12:00:05
* @Entity cn.qihangerp.module.stock.domain.ErpVendorInventoryBatch
*/
public interface ErpWarehouseGoodsStockBatchMapper extends BaseMapper<ErpWarehouseGoodsStockBatch> {
    List<GoodsSkuInventoryVo> searchSkuInventoryBatch(String keyword);
}




