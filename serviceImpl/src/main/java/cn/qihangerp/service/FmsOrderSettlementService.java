package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.FmsOrderSettlement;
import cn.qihangerp.model.entity.OOrder;
import cn.qihangerp.model.query.OrderSettlementQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public interface FmsOrderSettlementService {

    PageResult<FmsOrderSettlement> queryPageList(OrderSettlementQuery query, PageQuery pageQuery);

    List<OOrder> getUnsettledOrders(OrderSettlementQuery query, Page<OOrder> page);

    List<FmsOrderSettlement> getSettledOrders(OrderSettlementQuery query, Page<FmsOrderSettlement> page);

    Map<String, Object> autoSettlement(List<String> orderIds, String operator);

    Map<String, Object> manualSettlement(String orderId, String settlementId, String operator);

    boolean cancelSettlement(Long id, String operator);

    FmsOrderSettlement getSettlementDetail(Long id);

    FmsOrderSettlement calculateOrderSettlement(String orderId);
}
