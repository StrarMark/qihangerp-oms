package cn.qihangerp.erp.controller.erp;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.entity.ErpPurchaseOrder;
import cn.qihangerp.model.entity.ErpPurchaseOrderItem;
import cn.qihangerp.model.request.PurchaseOrderAddRequest;
import cn.qihangerp.model.request.PurchaseOrderOptionRequest;
import cn.qihangerp.model.request.SearchRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.ErpPurchaseOrderItemService;
import cn.qihangerp.service.ErpPurchaseOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
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
public class PurchaseOrderController extends BaseController
{
    private final ErpPurchaseOrderService erpPurchaseOrderService;
    private final ErpPurchaseOrderItemService erpPurchaseOrderItemService;

    /**
     * 采购商品明细分页查询
     */
    @GetMapping("/item_list")
    public TableDataInfo itemList(ErpPurchaseOrderItem query, PageQuery pageQuery)
    {
        LambdaQueryWrapper<ErpPurchaseOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getOrderNum()), ErpPurchaseOrderItem::getOrderNum, query.getOrderNum())
               .like(StringUtils.hasText(query.getGoodsName()), ErpPurchaseOrderItem::getGoodsName, query.getGoodsName())
               .like(StringUtils.hasText(query.getSpecNum()), ErpPurchaseOrderItem::getSpecNum, query.getSpecNum())
               .eq(query.getSpecId() != null, ErpPurchaseOrderItem::getSpecId, query.getSpecId())
               .orderByDesc(ErpPurchaseOrderItem::getId);
        Page<ErpPurchaseOrderItem> page = erpPurchaseOrderItemService.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);
        return getDataTable(PageResult.build(page));
    }
    /**
     *
     */
    @GetMapping("/list")
    public TableDataInfo list(SearchRequest bo, PageQuery pageQuery)
    {
        PageResult<ErpPurchaseOrder> pageResult = erpPurchaseOrderService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @GetMapping(value = "/detail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ErpPurchaseOrder detail = erpPurchaseOrderService.getDetailById(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/create")
    public AjaxResult add(@RequestBody PurchaseOrderAddRequest addBo, HttpServletRequest request)
    {
        addBo.setCreateBy(getUsername());
        var result = erpPurchaseOrderService.createPurchaseOrder(addBo);
        if (result.getCode() == 0) return AjaxResult.success();
        else return AjaxResult.error(result.getMsg());
    }
    @PutMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody PurchaseOrderOptionRequest req, HttpServletRequest request)
    {
        req.setUpdateBy(getUsername());
        int result = erpPurchaseOrderService.updateScmPurchaseOrder(req);
        if(result == -1){
            return new AjaxResult(0,"状态不正确");
        }else{
            return toAjax(result);
        }

    }
}
