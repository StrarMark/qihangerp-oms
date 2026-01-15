package cn.qihangerp.erp.controller;


import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.entity.OShipment;
import cn.qihangerp.model.bo.OrderShipBo;
import cn.qihangerp.module.order.service.OOrderService;
import cn.qihangerp.module.order.service.ErpShipmentService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/shipping")
public class ShipmentController extends BaseController {
    private final ErpShipmentService shippingService;

    private final OOrderService orderService;
    @GetMapping("/list")
    public TableDataInfo list(OShipment shipping, PageQuery pageQuery)
    {
        return getDataTable(shippingService.queryPageList(shipping,pageQuery));
    }


    @GetMapping("/searchOrderConsignee")
    public TableDataInfo searchOrderConsignee(String consignee)
    {
        return getDataTable(orderService.searchOrderConsignee(consignee));
    }

    @GetMapping("/searchOrderItemByReceiverMobile")
    public TableDataInfo searchOrderItemByReceiverMobile(String receiverMobile)
    {
        return getDataTable(orderService.searchOrderItemByReceiverMobile(receiverMobile));
    }

    /**
     * 手动添加发货记录
     * @param shipping
     * @return
     */
    @PostMapping("/handShip")
//    public AjaxResult orderHandShip(@RequestHeader("Authorization") String authorization, @RequestBody OrderShipBo shipping)
    public AjaxResult orderHandShip( @RequestBody OrderShipBo shipping)
    {
        if(shipping.getShipType()==null) return AjaxResult.error("缺少参数：shipType");
        if(shipping.getShopId()==null) return AjaxResult.error("缺少参数：shopId");
        if(StringUtils.isEmpty(shipping.getOrderNum())) return AjaxResult.error("缺少参数：orderNum");
        if(StringUtils.isEmpty(shipping.getReceiverName())) return AjaxResult.error("缺少参数：receiverName");
        if(StringUtils.isEmpty(shipping.getReceiverMobile())) return AjaxResult.error("缺少参数：receiverMobile");
        if(StringUtils.isEmpty(shipping.getAddress())) return AjaxResult.error("缺少参数：address");

        var result = shippingService.addRecord(shipping);
        if(result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
//            // 发货
//            TaoOrderShipBo bo = new TaoOrderShipBo();
//            bo.setShopId(shipping.getShopId());
////            bo.setTid(shipping.getOrderId());
//            bo.setWaybillCode(shipping.getShipCode());
//            bo.setWaybillCompany(shipping.getShipCompany());
//
//            JSONObject jsonObject = taoApiService.orderShip(authorization, bo);
            return AjaxResult.success();
        } else{
            return AjaxResult.error(result.getCode(),result.getMsg());
        }
    }
}
