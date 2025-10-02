package cn.qihangerp.api.tao.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.common.bo.ShopOrderShipBo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.module.open.tao.domain.TaoOrder;
import cn.qihangerp.module.open.tao.domain.bo.TaoOrderBo;
import cn.qihangerp.module.open.tao.domain.bo.TaoOrderConfirmBo;
import cn.qihangerp.module.open.tao.domain.bo.TaoOrderPushBo;
import cn.qihangerp.module.open.tao.service.TaoOrderService;
import cn.qihangerp.security.common.BaseController;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/tao/order")
public class TaoOrderController extends BaseController {
    private final TaoOrderService orderService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(TaoOrderBo bo, PageQuery pageQuery) {
        PageResult<TaoOrder> result = orderService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        TaoOrder taoOrder = orderService.queryDetailById(id);
        if(taoOrder==null) return AjaxResult.error("没有找到订单信息");
        return success(taoOrder);
    }

    /**
     * 手动推送到系统
     * @param bo
     * @return
     */
    @PostMapping("/push_oms")
    @ResponseBody
    public AjaxResult pushOms(@RequestBody TaoOrderPushBo bo) {
        // TODO:需要优化消息格式
        if(bo!=null && bo.getIds()!=null) {
            for(String id: bo.getIds()) {
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.TAO, MqType.ORDER_MESSAGE, id));
            }
        }
        return success();
    }

    @PostMapping("/confirmOrder")
    public AjaxResult confirmOrder(@RequestBody TaoOrderConfirmBo bo) {
        log.info("=========确认订单======={}", JSONObject.toJSONString(bo));

//        var result = orderService.confirmOrder(bo);
//        if(result.getCode()==0) return success();
//        else return AjaxResult.error(result.getMsg());

        return AjaxResult.error("未实现");
    }
}
