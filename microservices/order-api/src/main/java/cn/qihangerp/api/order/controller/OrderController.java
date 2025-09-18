package cn.qihangerp.api.order.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.request.OrderSearchRequest;
import cn.qihangerp.module.order.domain.bo.OrderAllocateShipRequest;
import cn.qihangerp.module.order.domain.bo.OrderShipRequest;
import cn.qihangerp.module.order.service.OOrderItemService;
import cn.qihangerp.module.order.service.OOrderService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺订单Controller
 *
 * @author qihang
 * @date 2023-12-31
 */
@AllArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController
{

    private final OOrderService orderService;
    private final OOrderItemService orderItemService;

    /**
     * 查询店铺订单列表
     */
    @PreAuthorize("@ss.hasPermi('shop:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(OrderSearchRequest bo, PageQuery pageQuery)
    {
        var pageList = orderService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }


    /**
     * 获取店铺订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderService.queryDetailById(id));
    }
    /**
     * 待发货列表（去除处理过的）
     * @param order
     * @param pageQuery
     * @return
     */
    @GetMapping("/waitShipmentList")
    public TableDataInfo waitShipmentList(OrderSearchRequest order, PageQuery pageQuery)
    {
        var pageList = orderService.queryWaitShipmentPageList(order,pageQuery);
        return getDataTable(pageList);
    }
    /**
     * 已分配供应商发货列表
     * @param order
     * @param pageQuery
     * @return
     */
    @GetMapping("/assignedShipmentList")
    public TableDataInfo assignedShipmentList(OrderSearchRequest order, PageQuery pageQuery)
    {
        var pageList = orderService.queryAssignedShipmentList(order,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 已发货列表
     * @param order
     * @param pageQuery
     * @return
     */
    @GetMapping("/shippedList")
    public TableDataInfo shippedList(OrderSearchRequest order, PageQuery pageQuery)
    {
        var pageList = orderService.queryShippedPageList(order,pageQuery);
        return getDataTable(pageList);
    }


//    @PostMapping
//    public AjaxResult add(@RequestBody OrderCreateBo order)
//    {
//        if(order.getGoodsAmount()==null)return new AjaxResult(1503,"请填写商品价格！");
//
//        int result = orderService.insertErpOrder(order,getUsername());
//        if(result == -1) return new AjaxResult(501,"订单号已存在！");
//        if(result == -2) return new AjaxResult(502,"请添加订单商品！");
//        if(result == -3) return new AjaxResult(503,"请完善订单商品明细！");
//        if(result == -4) return new AjaxResult(504,"请选择店铺！");
//        return toAjax(result);
//    }
//    /**
//     * 订单发货
//     * @param order
//     * @return
//     */
//    @Log(title = "店铺订单", businessType = BusinessType.UPDATE)
//    @PostMapping("/ship")
//    public AjaxResult ship(@RequestBody ErpOrder order)
//    {
//        order.setUpdateBy(getUsername());
//        int result = orderService.shipErpOrder(order);
//        if(result == -1) return new AjaxResult(501,"订单不存在！");
//        else if(result == -2) return new AjaxResult(502,"订单号已存在！");
//        return toAjax(result);
//    }

    /**
     * 订单发货(手动发货)
     * @param shipBo
     * @return
     */
    @PostMapping("/manualShipment")
    public AjaxResult manualShipment(@RequestBody OrderShipRequest shipBo)
    {
        var result = orderService.manualShipmentOrder(shipBo,getUsername());
        if(result.getCode() == 0) return AjaxResult.success();
        else return AjaxResult.error(result.getMsg());
//        return AjaxResult.error("未实现AAA");
    }

    /**
     * 分配供应商发货
     * @param shipBo
     * @return
     */
    @PostMapping("/allocateShipmentOrder")
    public AjaxResult allocateShipmentOrder(@RequestBody OrderAllocateShipRequest shipBo)
    {
        var result = orderService.allocateShipmentOrder(shipBo,getUsername());
        if(result.getCode() == 0) return AjaxResult.success();
        else return AjaxResult.error(result.getMsg());
    }

}
