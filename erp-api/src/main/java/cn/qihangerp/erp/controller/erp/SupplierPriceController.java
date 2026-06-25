package cn.qihangerp.erp.controller.erp;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.entity.ErpSupplierGoodsPrice;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.ErpSupplierGoodsPriceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商报价管理
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/erp-api/supplier/price")
public class SupplierPriceController extends BaseController {

    private final ErpSupplierGoodsPriceService supplierGoodsPriceService;

    /**
     * 分页查询供应商报价列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ErpSupplierGoodsPrice query, PageQuery pageQuery) {
        var pageList = supplierGoodsPriceService.queryPageList(query, pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 获取供应商报价详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(supplierGoodsPriceService.getById(id));
    }

    /**
     * 新增供应商报价
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ErpSupplierGoodsPrice bo) {
        bo.setCreateBy(getUsername());
        bo.setMerchantId(0L);
        bo.setStatus(1);
        return toAjax(supplierGoodsPriceService.save(bo));
    }

    /**
     * 修改供应商报价
     */
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody ErpSupplierGoodsPrice bo) {
        bo.setUpdateBy(getUsername());
        return toAjax(supplierGoodsPriceService.updateById(bo));
    }

    /**
     * 删除供应商报价
     */
    @DeleteMapping("/del/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(supplierGoodsPriceService.removeById(id));
    }
}
