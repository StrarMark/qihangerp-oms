package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.query.OrderSettlementQuery;
import cn.qihangerp.mapper.*;
import cn.qihangerp.service.FmsOrderSettlementService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class FmsOrderSettlementServiceImpl extends ServiceImpl<FmsOrderSettlementMapper, FmsOrderSettlement> implements FmsOrderSettlementService {

    @Autowired
    private FmsOrderSettlementItemMapper settlementItemMapper;

    @Autowired
    private cn.qihangerp.mapper.OOrderMapper orderMapper;

    @Autowired
    private cn.qihangerp.mapper.OOrderStockingMapper orderStockingMapper;

    @Autowired
    private cn.qihangerp.mapper.OOrderStockingItemBatchMapper itemBatchMapper;

    @Autowired
    private cn.qihangerp.mapper.FmsExpenseItemMapper expenseItemMapper;

    @Autowired
    private cn.qihangerp.mapper.FmsExpenseMapper expenseMapper;

    @Override
    public PageResult<FmsOrderSettlement> queryPageList(OrderSettlementQuery query, PageQuery pageQuery) {
        Page<FmsOrderSettlement> page = pageQuery.build();
        QueryWrapper<FmsOrderSettlement> wrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getMerchantId() != null) {
                wrapper.eq("merchant_id", query.getMerchantId());
            }
            if (query.getShopId() != null) {
                wrapper.eq("shop_id", query.getShopId());
            }
            if (query.getOrderNo() != null) {
                wrapper.like("order_no", query.getOrderNo());
            }
            if (query.getStatus() != null) {
                wrapper.eq("status", query.getStatus());
            }
        }
        wrapper.orderByDesc("create_time");
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.build(page);
    }

    @Override
    public List<OOrder> getUnsettledOrders(OrderSettlementQuery query, Page<OOrder> page) {
        QueryWrapper<OOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("settlement_status", 0);
        wrapper.or().isNull("settlement_status");
        if (query != null) {
            if (query.getMerchantId() != null) {
                wrapper.eq("merchant_id", query.getMerchantId());
            }
            if (query.getShopId() != null) {
                wrapper.eq("shop_id", query.getShopId());
            }
            if (query.getOrderNo() != null) {
                wrapper.like("order_num", query.getOrderNo());
            }
        }
        wrapper.orderByDesc("create_time");
        orderMapper.selectPage(page, wrapper);
        List<OOrder> orders = page.getRecords();
        for (OOrder order : orders) {
            order.setRemark(getPlatformName(order.getShopType()));
        }
        return orders;
    }

    @Override
    public List<FmsOrderSettlement> getSettledOrders(OrderSettlementQuery query, Page<FmsOrderSettlement> page) {
        QueryWrapper<FmsOrderSettlement> wrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getMerchantId() != null) {
                wrapper.eq("merchant_id", query.getMerchantId());
            }
            if (query.getShopId() != null) {
                wrapper.eq("shop_id", query.getShopId());
            }
            if (query.getOrderNo() != null) {
                wrapper.like("order_no", query.getOrderNo());
            }
        }
        wrapper.orderByDesc("create_time");
        baseMapper.selectPage(page, wrapper);
        return page.getRecords();
    }

    @Override
    @Transactional
    public Map<String, Object> autoSettlement(List<String> orderIds, String operator) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failReasons = new ArrayList<>();

        for (String orderId : orderIds) {
            try {
                FmsOrderSettlement settlement = calculateOrderSettlement(orderId);
                if (settlement != null) {
                    settlement.setCreateBy(operator);
                    settlement.setCreateTime(new Date());
                    settlement.setVersion(1);
                    settlement.setStatus(1);
                    baseMapper.insert(settlement);

                    saveSettlementItems(settlement);

                    updateOrderSettlementStatus(orderId, settlement.getId());
                    updateExpenseItemSettlementStatus(Long.valueOf(orderId), settlement.getId());

                    successCount++;
                } else {
                    failCount++;
                    failReasons.add("订单 " + orderId + " 无法结算：无法获取订单数据或订单已发货");
                }
            } catch (Exception e) {
                failCount++;
                failReasons.add("订单 " + orderId + " 结算失败：" + e.getMessage());
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failReasons", failReasons);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> manualSettlement(String orderId, String settlementId, String operator) {
        Map<String, Object> result = new HashMap<>();
        try {
            FmsOrderSettlement settlement = calculateOrderSettlement(orderId);
            if (settlement != null) {
                settlement.setCreateBy(operator);
                settlement.setCreateTime(new Date());
                settlement.setVersion(1);
                settlement.setStatus(1);
                baseMapper.insert(settlement);

                saveSettlementItems(settlement);

                updateOrderSettlementStatus(orderId, settlement.getId());
                updateExpenseItemSettlementStatus(Long.valueOf(orderId), settlement.getId());

                result.put("success", true);
                result.put("settlementId", settlement.getId());
            } else {
                result.put("success", false);
                result.put("message", "无法获取订单数据");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    @Transactional
    public boolean cancelSettlement(Long id, String operator) {
        FmsOrderSettlement settlement = baseMapper.selectById(id);
        if (settlement != null) {
            QueryWrapper<FmsOrderSettlementItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("settlement_id", id);
            settlementItemMapper.delete(itemWrapper);

            QueryWrapper<FmsExpenseItem> expenseWrapper = new QueryWrapper<>();
            expenseWrapper.eq("settlement_id", id);
            List<FmsExpenseItem> items = expenseItemMapper.selectList(expenseWrapper);
            for (FmsExpenseItem item : items) {
                item.setSettlementStatus(0);
                item.setSettlementId(null);
                item.setSettlementTime(null);
                expenseItemMapper.updateById(item);
            }

            OOrder order = orderMapper.selectById(settlement.getOrderId());
            if (order != null) {
                order.setSettlementStatus(0);
                order.setSettlementId(null);
                order.setSettlementTime(null);
                orderMapper.updateById(order);
            }

            return baseMapper.deleteById(id) > 0;
        }
        return false;
    }

    @Override
    public FmsOrderSettlement getSettlementDetail(Long id) {
        FmsOrderSettlement settlement = baseMapper.selectById(id);
        if (settlement != null) {
            QueryWrapper<FmsOrderSettlementItem> wrapper = new QueryWrapper<>();
            wrapper.eq("settlement_id", id);
            List<FmsOrderSettlementItem> items = settlementItemMapper.selectList(wrapper);
            settlement.setItems(items);
        }
        return settlement;
    }

    @Override
    public FmsOrderSettlement calculateOrderSettlement(String orderId) {
        OOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }

        FmsOrderSettlement settlement = new FmsOrderSettlement();
        settlement.setOrderId(Long.valueOf(orderId));
        settlement.setOrderNo(order.getOrderNum());
        settlement.setMerchantId(order.getMerchantId());
        settlement.setShopId(order.getShopId());

        settlement.setSalesAmount(order.getAmount() != null ? BigDecimal.valueOf(order.getAmount()) : BigDecimal.ZERO);

        BigDecimal purchaseCost = calculatePurchaseCost(orderId);
        settlement.setPurchaseCost(purchaseCost);

        BigDecimal shippingFee = calculateShippingFee(orderId);
        settlement.setShippingFee(shippingFee);

        Map<Integer, BigDecimal> expenseMap = calculateExpenses(orderId);
        BigDecimal platformFee = expenseMap.getOrDefault(10, BigDecimal.ZERO);
        BigDecimal marketingFee = expenseMap.getOrDefault(11, BigDecimal.ZERO);
        BigDecimal otherFee = expenseMap.getOrDefault(12, BigDecimal.ZERO)
                .add(expenseMap.getOrDefault(13, BigDecimal.ZERO))
                .add(expenseMap.getOrDefault(14, BigDecimal.ZERO))
                .add(expenseMap.getOrDefault(15, BigDecimal.ZERO))
                .add(expenseMap.getOrDefault(16, BigDecimal.ZERO))
                .add(expenseMap.getOrDefault(99, BigDecimal.ZERO));

        settlement.setPlatformFee(platformFee);
        settlement.setMarketingFee(marketingFee);
        settlement.setOtherFee(otherFee);

        BigDecimal totalCost = purchaseCost.add(shippingFee).add(platformFee).add(marketingFee).add(otherFee);
        settlement.setTotalCost(totalCost);

        BigDecimal profit = settlement.getSalesAmount().subtract(totalCost);
        settlement.setProfit(profit);

        if (settlement.getSalesAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitRate = profit.divide(settlement.getSalesAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            settlement.setProfitRate(profitRate);
        } else {
            settlement.setProfitRate(BigDecimal.ZERO);
        }

        settlement.setSettlementNo("STL" + System.currentTimeMillis());

        return settlement;
    }

    private BigDecimal calculatePurchaseCost(String orderId) {
        QueryWrapper<OOrderStockingItemBatch> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        List<OOrderStockingItemBatch> batches = itemBatchMapper.selectList(wrapper);

        BigDecimal totalCost = BigDecimal.ZERO;
        for (OOrderStockingItemBatch batch : batches) {
            if (batch.getTotalCost() != null) {
                totalCost = totalCost.add(batch.getTotalCost());
            } else if (batch.getUnitCost() != null && batch.getQuantity() != null) {
                totalCost = totalCost.add(batch.getUnitCost().multiply(BigDecimal.valueOf(batch.getQuantity())));
            } else if (batch.getPurPrice() != null && batch.getQuantity() != null) {
                totalCost = totalCost.add(batch.getPurPrice().multiply(BigDecimal.valueOf(batch.getQuantity())));
            }
        }
        return totalCost;
    }

    private BigDecimal calculateShippingFee(String orderId) {
        QueryWrapper<OOrderStocking> wrapper = new QueryWrapper<>();
        wrapper.eq("o_order_id", Long.valueOf(orderId));
        List<OOrderStocking> stockings = orderStockingMapper.selectList(wrapper);

        BigDecimal totalShippingFee = BigDecimal.ZERO;
        for (OOrderStocking stocking : stockings) {
            if (stocking.getShippingCost() != null) {
                totalShippingFee = totalShippingFee.add(stocking.getShippingCost());
            }
        }
        return totalShippingFee;
    }

    private Map<Integer, BigDecimal> calculateExpenses(String orderId) {
        QueryWrapper<FmsExpenseItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", Long.valueOf(orderId));
        wrapper.eq("settlement_status", 0);
        List<FmsExpenseItem> items = expenseItemMapper.selectList(wrapper);

        Map<Integer, BigDecimal> expenseMap = new HashMap<>();
        for (FmsExpenseItem item : items) {
            FmsExpense expense = expenseMapper.selectById(item.getExpenseId());
            if (expense != null && expense.getStatus() == 5) {
                Integer expenseType = expense.getExpenseType();
                BigDecimal amount = item.getAmount();
                if (amount == null) {
                    amount = BigDecimal.ZERO;
                }
                expenseMap.merge(expenseType, amount, BigDecimal::add);
            }
        }
        return expenseMap;
    }

    private void saveSettlementItems(FmsOrderSettlement settlement) {
        List<FmsOrderSettlementItem> items = new ArrayList<>();

        if (settlement.getPurchaseCost().compareTo(BigDecimal.ZERO) > 0) {
            FmsOrderSettlementItem item = new FmsOrderSettlementItem();
            item.setSettlementId(settlement.getId());
            item.setItemType(1);
            item.setItemName("商品采购成本");
            item.setAmount(settlement.getPurchaseCost());
            item.setCreateTime(new Date());
            items.add(item);
        }

        if (settlement.getShippingFee().compareTo(BigDecimal.ZERO) > 0) {
            FmsOrderSettlementItem item = new FmsOrderSettlementItem();
            item.setSettlementId(settlement.getId());
            item.setItemType(2);
            item.setItemName("发货费用");
            item.setAmount(settlement.getShippingFee());
            item.setCreateTime(new Date());
            items.add(item);
        }

        if (settlement.getPlatformFee().compareTo(BigDecimal.ZERO) > 0) {
            FmsOrderSettlementItem item = new FmsOrderSettlementItem();
            item.setSettlementId(settlement.getId());
            item.setItemType(4);
            item.setItemName("平台扣点");
            item.setAmount(settlement.getPlatformFee());
            item.setCreateTime(new Date());
            items.add(item);
        }

        if (settlement.getMarketingFee().compareTo(BigDecimal.ZERO) > 0) {
            FmsOrderSettlementItem item = new FmsOrderSettlementItem();
            item.setSettlementId(settlement.getId());
            item.setItemType(5);
            item.setItemName("营销费用");
            item.setAmount(settlement.getMarketingFee());
            item.setCreateTime(new Date());
            items.add(item);
        }

        if (settlement.getOtherFee().compareTo(BigDecimal.ZERO) > 0) {
            FmsOrderSettlementItem item = new FmsOrderSettlementItem();
            item.setSettlementId(settlement.getId());
            item.setItemType(99);
            item.setItemName("其他费用");
            item.setAmount(settlement.getOtherFee());
            item.setCreateTime(new Date());
            items.add(item);
        }

        for (FmsOrderSettlementItem item : items) {
            settlementItemMapper.insert(item);
        }
    }

    private void updateOrderSettlementStatus(String orderId, Long settlementId) {
        OOrder order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setSettlementStatus(1);
            order.setSettlementId(settlementId);
            order.setSettlementTime(new Date());
            orderMapper.updateById(order);
        }
    }

    private void updateExpenseItemSettlementStatus(Long orderId, Long settlementId) {
        QueryWrapper<FmsExpenseItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.eq("settlement_status", 0);
        List<FmsExpenseItem> items = expenseItemMapper.selectList(wrapper);

        for (FmsExpenseItem item : items) {
            item.setSettlementStatus(1);
            item.setSettlementId(settlementId);
            item.setSettlementTime(new Date());
            expenseItemMapper.updateById(item);
        }

        if (!items.isEmpty()) {
            Set<Long> expenseIds = new HashSet<>();
            for (FmsExpenseItem item : items) {
                expenseIds.add(item.getExpenseId());
            }
            for (Long expenseId : expenseIds) {
                FmsExpense expense = expenseMapper.selectById(expenseId);
                if (expense != null) {
                    QueryWrapper<FmsExpenseItem> checkWrapper = new QueryWrapper<>();
                    checkWrapper.eq("expense_id", expenseId);
                    checkWrapper.eq("settlement_status", 0);
                    long unsettledCount = expenseItemMapper.selectCount(checkWrapper);
                    if (unsettledCount == 0) {
                        expense.setSettlementStatus(1);
                        expense.setSettlementId(settlementId);
                        expense.setSettlementTime(new Date());
                        expenseMapper.updateById(expense);
                    }
                }
            }
        }
    }

    private String getPlatformName(Integer shopType) {
        if (shopType == null) return "未知";
        switch (shopType) {
            case 1: return "淘宝";
            case 2: return "拼多多";
            case 3: return "抖音";
            case 4: return "京东";
            default: return "未知";
        }
    }
}
