package cn.qihangerp.erp.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStock;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStockBatch;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.ErpWarehouseGoodsStockBatchService;
import cn.qihangerp.service.ErpWarehouseGoodsStockService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/goodsInventory")
public class GoodsInventoryController extends BaseController {
    private final ErpWarehouseGoodsStockService goodsInventoryService;
    private final ErpWarehouseGoodsStockBatchService inventoryBatchService;

    @GetMapping("/list")
    public TableDataInfo list(ErpWarehouseGoodsStock bo, PageQuery pageQuery)
    {
        PageResult<ErpWarehouseGoodsStock> pageResult = goodsInventoryService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ErpWarehouseGoodsStock goodsInventory = goodsInventoryService.getById(id);
        if(goodsInventory!=null) {
            List<ErpWarehouseGoodsStockBatch> list = inventoryBatchService.list(
                    new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                            .eq(ErpWarehouseGoodsStockBatch::getGoodsId, goodsInventory.getGoodsId()));
            return AjaxResult.success(list);
        }
        return success();
    }
}
