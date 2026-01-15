package cn.qihangerp.erp.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.entity.ErpPurchaseOrderShip;
import cn.qihangerp.model.query.PurchaseOrderSearchBo;
import cn.qihangerp.module.service.ErpPurchaseOrderShipService;
import cn.qihangerp.security.common.BaseController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理Controller
 * 
 * @author qihang
 * @date 2023-12-29
 */
@AllArgsConstructor
@RestController
@RequestMapping("/erp/purchase")
public class PurchaseOrderShipController extends BaseController
{
    private final ErpPurchaseOrderShipService shipService;
    /**
     *
     */
    @GetMapping("/shipList")
    public TableDataInfo shipList(PurchaseOrderSearchBo bo, PageQuery pageQuery)
    {
        return getDataTable(shipService.queryPageList(bo, pageQuery));
    }
    @GetMapping(value = "/shipDetail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ErpPurchaseOrderShip detail = shipService.getById(id);
        return AjaxResult.success(detail);
    }
    @PutMapping("/ship/confirmReceipt")
    public AjaxResult confirmReceipt(@RequestBody ErpPurchaseOrderShip erpPurchaseOrderShip, HttpServletRequest request)
    {
        erpPurchaseOrderShip.setUpdateBy(getUsername());
        return toAjax(shipService.updateScmPurchaseOrderShip(erpPurchaseOrderShip));
    }

    //createStockInEntry
//    @PostMapping("/ship/createStockInEntry")
//    public AjaxResult createStockInEntry(@RequestBody PurchaseOrderStockInBo bo, HttpServletRequest request)
//    {
//        if(bo.getId() == null) return AjaxResult.error("缺少参数id");
//        if(bo.getWarehouseId()==null) return AjaxResult.error("请选择仓库");
//
//        bo.setCreateBy(getUsername());
//        int result =shipService.createStockInEntry(bo);
//        if(result == -1) return new AjaxResult(404,"采购物流不存在");
//        else if (result == -2) return new AjaxResult(501,"未确认收货不允许操作");
//        else if (result == -3) {
//            return new AjaxResult(502,"已处理过了请勿重复操作");
//        } else if (result == -4) {
//            return new AjaxResult(503,"状态不正确不能操作");
//        } else if (result == -5) {
//            return new AjaxResult(504,"仓库不存在");
//        } else if (result == 1) {
//            return toAjax(1);
//        }else return toAjax(result);
//    }

}
