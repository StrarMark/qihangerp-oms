package cn.qihangerp.erp.controller.oms;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.entity.ShopOrder;
import cn.qihangerp.model.entity.ShopOrderItem;
import cn.qihangerp.model.query.ShopOrderQueryBo;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.ShopOrderItemService;
import cn.qihangerp.service.ShopOrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/shop/order")
public class ShopOrderController extends BaseController {

    private final ShopOrderService shopOrderService;
    private final ShopOrderItemService shopOrderItemService;

    /**
     * 查询店铺订单列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ShopOrderQueryBo bo, PageQuery pageQuery) {
        PageResult<ShopOrder> pageList = shopOrderService.queryOrderPageList(bo, pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 查询店铺订单明细列表
     */
    @GetMapping("/item_list")
    public TableDataInfo itemList(ShopOrderQueryBo bo, PageQuery pageQuery) {
        PageResult<ShopOrderItem> pageList = shopOrderItemService.queryPageList(bo, pageQuery);
        return getDataTable(pageList);
    }
}
