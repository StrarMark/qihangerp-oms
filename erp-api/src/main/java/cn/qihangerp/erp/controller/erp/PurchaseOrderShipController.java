package cn.qihangerp.erp.controller.erp;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpPurchaseOrderShip;
import cn.qihangerp.model.request.ConfirmReceiptRequest;
import cn.qihangerp.model.request.PurchaseOrderStockInBo;
import cn.qihangerp.model.request.SearchRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.security.common.SecurityUtils;
import cn.qihangerp.service.ErpPurchaseOrderShipService;
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
@RequestMapping("/api/erp-api/erp/purchase")
public class PurchaseOrderShipController extends BaseController
{
    private final ErpPurchaseOrderShipService shipService;
    /**
     *
     */
    @GetMapping("/shipList")
    public TableDataInfo shipList(SearchRequest bo, PageQuery pageQuery)
    {
        return getDataTable(shipService.queryPageList(bo, pageQuery));
    }
    @GetMapping(value = "/shipDetail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ErpPurchaseOrderShip detail = shipService.getById(id);
        if (detail == null) return AjaxResult.error("物流记录不存在");
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", detail.getId());
        map.put("orderId", detail.getOrderId());
        map.put("supplierId", detail.getSupplierId());
        map.put("shipCompany", detail.getShipCompany());
        map.put("shipNum", detail.getShipNum());
        map.put("freight", detail.getFreight());
        map.put("shipTime", detail.getShipTime() != null ? detail.getShipTime().toString() : null);
        map.put("receiptTime", detail.getReceiptTime() != null ? detail.getReceiptTime().toString() : null);
        map.put("createTime", detail.getCreateTime() != null ? detail.getCreateTime().toString() : null);
        map.put("status", detail.getStatus());
        map.put("remark", detail.getRemark());
        map.put("backCount", detail.getBackCount());
        map.put("stockInTime", detail.getStockInTime() != null ? detail.getStockInTime().toString() : null);
        map.put("stockInCount", detail.getStockInCount());
        map.put("warehouseId", detail.getWarehouseId());
        map.put("warehouseName", detail.getWarehouseName());
        map.put("warehouseType", detail.getWarehouseType());
        map.put("orderNum", detail.getOrderNum());
        map.put("orderDate", detail.getOrderDate());
        map.put("orderSpecUnit", detail.getOrderSpecUnit());
        map.put("orderGoodsUnit", detail.getOrderGoodsUnit());
        map.put("orderSpecUnitTotal", detail.getOrderSpecUnitTotal());
        return AjaxResult.success(map);
    }
    @PutMapping("/ship/confirmReceipt")
    public AjaxResult confirmReceipt(@RequestBody ConfirmReceiptRequest req, HttpServletRequest request)
    {
        ErpPurchaseOrderShip ship = new ErpPurchaseOrderShip();
        ship.setId(req.getId());
        ship.setOrderId(req.getOrderId());
        ship.setRemark(req.getRemark());
        ship.setUpdateBy(getUsername());
        if (req.getReceiptTime() != null && !req.getReceiptTime().isEmpty()) {
            ship.setReceiptTime(java.time.LocalDateTime.of(
                java.time.LocalDate.parse(req.getReceiptTime(), java.time.format.DateTimeFormatter.ISO_LOCAL_DATE),
                java.time.LocalTime.MIN));
        } else {
            ship.setReceiptTime(java.time.LocalDateTime.now());
        }
        return toAjax(shipService.updateScmPurchaseOrderShip(ship));
    }

    @PostMapping("/ship/createStockInEntry")
    public AjaxResult createStockInEntry(@RequestBody PurchaseOrderStockInBo bo)
    {
        if(bo.getId() == null) return AjaxResult.error("缺少参数id");
        if(bo.getWarehouseId()==null) return AjaxResult.error("请选择仓库");

        bo.setCreateBy(getUsername());
        ResultVo<Long> result = shipService.createStockInEntry(bo, SecurityUtils.getUserId(),SecurityUtils.getUsername());
        if(result.getCode()==0)
            return AjaxResult.success();
        else
            return AjaxResult.error(result.getMsg());
    }

}
