package cn.qihangerp.erp.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.enums.EnumUserType;
import cn.qihangerp.model.entity.ErpSupplier;
import cn.qihangerp.model.entity.OShop;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.ErpSupplierService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@AllArgsConstructor
@RestController
@RequestMapping("/supplier")
public class SupplierController extends BaseController {
    private final ErpSupplierService supplierService;

    /**
     * 所有供应商list
     * @param bo
     * @param pageQuery
     * @return
     */
    @GetMapping("/list_all")
    public TableDataInfo list_all(ErpSupplier bo, PageQuery pageQuery) {
        var pageList = supplierService.list(new LambdaQueryWrapper<ErpSupplier>()
                .eq(ErpSupplier::getIsDelete,0)
                .eq(bo.getIsShipper()!=null,ErpSupplier::getIsShipper,bo.getIsShipper())
        );
        return getDataTable(pageList);
    }
    @GetMapping("/list")
    public TableDataInfo list(ErpSupplier bo, PageQuery pageQuery)
    {
        var pageList = supplierService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(supplierService.getById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PostMapping
    public AjaxResult add(@RequestBody ErpSupplier scmSupplier)
    {
//        scmSupplier.setCreatetime(new Date());
//        scmSupplier.setIsdelete(0);
        return toAjax(supplierService.save(scmSupplier));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ErpSupplier scmSupplier)
    {
        return toAjax(supplierService.updateById(scmSupplier));
    }

    /**
     * 删除【请填写功能名称】
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(supplierService.removeByIds(Arrays.stream(ids).toList()));
    }
}
