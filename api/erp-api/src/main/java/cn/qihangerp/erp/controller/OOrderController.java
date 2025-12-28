//package cn.qihangerp.oms.controller;
//
//
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.common.PageQuery;
//import cn.qihangerp.common.TableDataInfo;
//import cn.qihangerp.module.order.service.OOrderItemService;
//import cn.qihangerp.module.order.service.OOrderService;
//import cn.qihangerp.request.OrderSearchRequest;
//import cn.qihangerp.security.common.BaseController;
//import lombok.AllArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
///**
// * 店铺订单Controller
// *
// * @author qihang
// * @date 2023-12-31
// */
//@AllArgsConstructor
//@RestController
//@RequestMapping("/oorder")
//public class OOrderController extends BaseController
//{
//
//    private final OOrderService orderService;
//    private final OOrderItemService orderItemService;
//
//    /**
//     * 查询店铺订单列表
//     */
//    @PreAuthorize("@ss.hasPermi('shop:order:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(OrderSearchRequest bo, PageQuery pageQuery)
//    {
//        var pageList = orderService.queryPageList(bo,pageQuery);
//        return getDataTable(pageList);
//    }
//
//
//    /**
//     * 获取店铺订单详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('shop:order:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return success(orderService.queryDetailById(id));
//    }
//
//
////    @PostMapping
////    public AjaxResult add(@RequestBody OrderCreateBo order)
////    {
////        if(order.getGoodsAmount()==null)return new AjaxResult(1503,"请填写商品价格！");
////
////        int result = orderService.insertErpOrder(order,getUsername());
////        if(result == -1) return new AjaxResult(501,"订单号已存在！");
////        if(result == -2) return new AjaxResult(502,"请添加订单商品！");
////        if(result == -3) return new AjaxResult(503,"请完善订单商品明细！");
////        if(result == -4) return new AjaxResult(504,"请选择店铺！");
////        return toAjax(result);
////    }
////    /**
////     * 订单发货
////     * @param order
////     * @return
////     */
////    @Log(title = "店铺订单", businessType = BusinessType.UPDATE)
////    @PostMapping("/ship")
////    public AjaxResult ship(@RequestBody ErpOrder order)
////    {
////        order.setUpdateBy(getUsername());
////        int result = orderService.shipErpOrder(order);
////        if(result == -1) return new AjaxResult(501,"订单不存在！");
////        else if(result == -2) return new AjaxResult(502,"订单号已存在！");
////        return toAjax(result);
////    }
//
//}
