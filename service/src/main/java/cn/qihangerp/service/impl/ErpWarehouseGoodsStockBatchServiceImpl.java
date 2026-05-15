package cn.qihangerp.service.impl;

import cn.qihangerp.model.vo.GoodsSkuInventoryVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStockBatch;
import cn.qihangerp.service.ErpWarehouseGoodsStockBatchService;
import cn.qihangerp.mapper.ErpWarehouseGoodsStockBatchMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_vendor_inventory_batch(商品库存批次)】的数据库操作Service实现
* @createDate 2025-06-14 12:00:05
*/
@AllArgsConstructor
@Service
public class ErpWarehouseGoodsStockBatchServiceImpl extends ServiceImpl<ErpWarehouseGoodsStockBatchMapper, ErpWarehouseGoodsStockBatch>
    implements ErpWarehouseGoodsStockBatchService {
    private final ErpWarehouseGoodsStockBatchMapper erpWarehouseGoodsStockBatchMapper;
    @Override
    public List<GoodsSkuInventoryVo> searchSkuInventoryBatch(String keyword) {
        return erpWarehouseGoodsStockBatchMapper.searchSkuInventoryBatch(keyword);
    }
}




