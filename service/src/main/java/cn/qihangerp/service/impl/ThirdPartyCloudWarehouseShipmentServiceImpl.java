package cn.qihangerp.service.impl;

import cn.qihangerp.common.*;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.ThirdPartyCloudWarehouseShipRequest;
import cn.qihangerp.mapper.OOrderStockingMapper;
import cn.qihangerp.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* 第三方云仓发货出库服务实现
*/
@AllArgsConstructor
@Service
public class ThirdPartyCloudWarehouseShipmentServiceImpl extends ServiceImpl<OOrderStockingMapper, OOrderStocking>
    implements ThirdPartyCloudWarehouseShipmentService {
    private final OOrderStockingService orderStockingService;
    private final OOrderStockingItemService orderStockingItemService;
    private final ErpWarehouseGoodsService warehouseGoodsService;
    private final ErpWarehouseGoodsStockService warehouseGoodsStockService;
    private final ErpWarehouseGoodsStockBatchService warehouseGoodsStockBatchService;
    private final OOrderStockingItemBatchService orderStockingItemBatchService;
    private final ErpWarehouseStockOutService warehouseStockOutService;
    private final ErpWarehouseStockOutItemService warehouseStockOutItemService;
    private final ErpWarehouseStockOutItemPositionService warehouseStockOutItemPositionService;

    @Override
    public ResultVo getBatches(Long warehouseId, Long goodsSkuId, Integer quantity) {
        try {
            // 查询商品对应的仓库商品信息
            List<ErpWarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(
                new LambdaQueryWrapper<ErpWarehouseGoods>()
                    .eq(ErpWarehouseGoods::getErpGoodsSkuId, goodsSkuId)
                    .eq(ErpWarehouseGoods::getWarehouseId, warehouseId)
            );

            if (warehouseGoodsList.isEmpty()) {
                return ResultVo.error("仓库没有找到该商品SKU: " + goodsSkuId);
            }

            ErpWarehouseGoods warehouseGoods = warehouseGoodsList.get(0);

            // 查询仓库商品库存
            List<ErpWarehouseGoodsStock> stockList = warehouseGoodsStockService.list(
                new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                    .eq(ErpWarehouseGoodsStock::getGoodsId, warehouseGoods.getId())
                    .eq(ErpWarehouseGoodsStock::getWarehouseId, warehouseId)
            );

            if (stockList.isEmpty()) {
                return ResultVo.error("商品库存不存在");
            }

            ErpWarehouseGoodsStock stock = stockList.get(0);

            // 检查库存是否足够
            if (stock.getUsableNum() < quantity) {
                return ResultVo.error("商品库存不足");
            }

            // 查询批次信息（先进先出）
            List<ErpWarehouseGoodsStockBatch> batches = warehouseGoodsStockBatchService.list(
                new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                    .eq(ErpWarehouseGoodsStockBatch::getGoodsId, warehouseGoods.getId())
                    .eq(ErpWarehouseGoodsStockBatch::getWarehouseId, warehouseId)
                    .gt(ErpWarehouseGoodsStockBatch::getCurrentQty, 0)
                    .orderByAsc(ErpWarehouseGoodsStockBatch::getCreateTime)
            );

            if (batches.isEmpty()) {
                return ResultVo.error("商品批次不存在");
            }

            return ResultVo.success(batches);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("获取批次列表失败: " + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo manualShipment(Long userId, String userName, ThirdPartyCloudWarehouseShipRequest request) {
        // 1. 查询发货单信息
        OOrderStocking shipOrder = orderStockingService.getById(request.getShipOrderId());
        if (shipOrder == null) {
            return ResultVo.error("发货单不存在");
        }

        // 2. 查询发货明细
        List<OOrderStockingItem> orderItems = orderStockingItemService.list(
            new LambdaQueryWrapper<OOrderStockingItem>()
                .eq(OOrderStockingItem::getShipOrderId, request.getShipOrderId())
        );

        if (orderItems.isEmpty()) {
            return ResultVo.error("发货单无明细");
        }

        // 3. 创建系统云仓出库单
        ErpWarehouseStockOut stockOut = new ErpWarehouseStockOut();
        stockOut.setMerchantId(shipOrder.getMerchantId());
        stockOut.setType(1); // 订单出库
        stockOut.setSourceId(shipOrder.getId());
        stockOut.setSourceNum(shipOrder.getOrderNum());
        stockOut.setStatus(0); // 待出库
        stockOut.setOperatorId(userId);
        stockOut.setOperatorName(userName);
        stockOut.setCreateBy(userName);
        stockOut.setCreateTime(new Date());
        warehouseStockOutService.save(stockOut);

        // 4. 处理每个发货明细
        for (OOrderStockingItem orderItem : orderItems) {
            // 5. 查询商品对应的仓库商品信息
            List<ErpWarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(
                new LambdaQueryWrapper<ErpWarehouseGoods>()
                    .eq(ErpWarehouseGoods::getErpGoodsSkuId, orderItem.getGoodsSkuId())
                    .eq(ErpWarehouseGoods::getWarehouseId, shipOrder.getShipperId())
            );

            if (warehouseGoodsList.isEmpty()) {
                return ResultVo.error("仓库没有找到该商品SKU: " + orderItem.getGoodsSkuId());
            }

            ErpWarehouseGoods warehouseGoods = warehouseGoodsList.get(0);

            // 6. 查询仓库商品库存
            List<ErpWarehouseGoodsStock> stockList = warehouseGoodsStockService.list(
                new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                    .eq(ErpWarehouseGoodsStock::getGoodsId, warehouseGoods.getId())
                    .eq(ErpWarehouseGoodsStock::getWarehouseId, shipOrder.getShipperId())
            );

            if (stockList.isEmpty()) {
                return ResultVo.error("商品库存不存在");
            }

            ErpWarehouseGoodsStock stock = stockList.get(0);

            // 7. 创建出库明细
            ErpWarehouseStockOutItem stockOutItem = new ErpWarehouseStockOutItem();
            stockOutItem.setMerchantId(shipOrder.getMerchantId());
            stockOutItem.setEntryId(stockOut.getId());
            stockOutItem.setType(1); // 订单出库
            stockOutItem.setGoodsId(warehouseGoods.getId());
            stockOutItem.setGoodsName(orderItem.getGoodsName());
            stockOutItem.setSkuName(orderItem.getSkuName());
            stockOutItem.setGoodsImage(orderItem.getGoodsImg());
            stockOutItem.setOriginalQuantity(orderItem.getShipQuantity());
            stockOutItem.setOutQuantity(0);
            stockOutItem.setStatus(0); // 待出库
            stockOutItem.setWarehouseId(shipOrder.getShipperId());
            stockOutItem.setCreateBy(userName);
            stockOutItem.setCreateTime(new Date());
            warehouseStockOutItemService.save(stockOutItem);

            // 8. 查询批次信息（先进先出）
            List<ErpWarehouseGoodsStockBatch> batches = warehouseGoodsStockBatchService.list(
                new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                    .eq(ErpWarehouseGoodsStockBatch::getGoodsId, warehouseGoods.getId())
                    .eq(ErpWarehouseGoodsStockBatch::getWarehouseId, shipOrder.getShipperId())
                    .gt(ErpWarehouseGoodsStockBatch::getCurrentQty, 0)
                    .orderByAsc(ErpWarehouseGoodsStockBatch::getCreateTime)
            );

            if (batches.isEmpty()) {
                return ResultVo.error("商品批次不存在");
            }

            // 9. 处理批次扣减
            int remainQty = orderItem.getShipQuantity();
            for (ErpWarehouseGoodsStockBatch batch : batches) {
                if (remainQty <= 0) break;

                int deductQty = Math.min(batch.getCurrentQty(), remainQty);

                // 10. 扣减批次库存
                ErpWarehouseGoodsStockBatch updateBatch = new ErpWarehouseGoodsStockBatch();
                updateBatch.setId(batch.getId());
                updateBatch.setCurrentQty(batch.getCurrentQty() - deductQty);
                updateBatch.setUpdateBy(userName);
                updateBatch.setUpdateTime(new Date());
                updateBatch.setRemark((batch.getRemark() == null ? "" : batch.getRemark()) + "第三方云仓发货扣减库存；");
                warehouseGoodsStockBatchService.updateById(updateBatch);

                // 11. 记录发货备货批次信息
                OOrderStockingItemBatch batchRecord = new OOrderStockingItemBatch();
                batchRecord.setOrderStockingItemId(orderItem.getId());
                batchRecord.setOrderStockingId(shipOrder.getId());
                batchRecord.setOrderId(shipOrder.getOrderNum());
                batchRecord.setOrderItemId(orderItem.getOOrderItemId().toString());
                batchRecord.setBatchId(batch.getId());
                batchRecord.setBatchNum(batch.getBatchNum());
                batchRecord.setInventoryId(stock.getId());
                batchRecord.setGoodsId(batch.getGoodsId());
                batchRecord.setGoodsNo(batch.getGoodsNo());
                batchRecord.setQuantity(deductQty);
                batchRecord.setUnitCost(batch.getPurPrice() != null ? new java.math.BigDecimal(batch.getPurPrice()) : null);
                batchRecord.setTotalCost(batch.getPurPrice() != null ? new java.math.BigDecimal(batch.getPurPrice()).multiply(new java.math.BigDecimal(deductQty)) : null);
                batchRecord.setWarehouseId(batch.getWarehouseId());
                batchRecord.setInventoryMode(batch.getInventoryMode());
                batchRecord.setBarcode(batch.getBarcode());
                batchRecord.setActualGoldWeight(batch.getActualGoldWeight() != null ? new java.math.BigDecimal(batch.getActualGoldWeight()) : null);
                batchRecord.setActualSilverWeight(batch.getActualSilverWeight() != null ? new java.math.BigDecimal(batch.getActualSilverWeight()) : null);
                batchRecord.setLaborCost(batch.getLaborCost() != null ? new java.math.BigDecimal(batch.getLaborCost()) : null);
                batchRecord.setCertificateNo(batch.getCertificateNo());
                batchRecord.setPurPrice(batch.getPurPrice() != null ? new java.math.BigDecimal(batch.getPurPrice()) : null);
                batchRecord.setCreateTime(new Date());
                batchRecord.setCreateBy(userName);
                orderStockingItemBatchService.save(batchRecord);

                // 12. 增加出库item position
                ErpWarehouseStockOutItemPosition itemPosition = new ErpWarehouseStockOutItemPosition();
                itemPosition.setEntryId(stockOut.getId());
                itemPosition.setEntryItemId(stockOutItem.getId());
                itemPosition.setGoodsInventoryId(stock.getId());
                itemPosition.setGoodsInventoryDetailId(batch.getId());
                itemPosition.setQuantity((long) deductQty);
                itemPosition.setLocationId(0);
                itemPosition.setOperatorId((int) userId.longValue());
                itemPosition.setOperatorName(userName);
                itemPosition.setOutTime(new Date());
                itemPosition.setOutBatch(batch.getBatchNum());
                warehouseStockOutItemPositionService.save(itemPosition);

                remainQty -= deductQty;
            }

            if (remainQty > 0) {
                return ResultVo.error("商品库存不足");
            }

            // 13. 扣减总库存
            ErpWarehouseGoodsStock updateStock = new ErpWarehouseGoodsStock();
            updateStock.setId(stock.getId());
            updateStock.setTotalNum(stock.getTotalNum() - orderItem.getShipQuantity());
            updateStock.setTotalNumValue(updateStock.getTotalNum().doubleValue());
            updateStock.setUsableNum(stock.getUsableNum() - orderItem.getShipQuantity());
            updateStock.setUsableNumValue(updateStock.getUsableNum().doubleValue());
            updateStock.setUpdateBy(userName);
            updateStock.setUpdateTime(new Date());
            warehouseGoodsStockService.updateById(updateStock);

            // 14. 更新出库明细状态
            ErpWarehouseStockOutItem updateStockOutItem = new ErpWarehouseStockOutItem();
            updateStockOutItem.setId(stockOutItem.getId());
            updateStockOutItem.setOutQuantity(orderItem.getShipQuantity());
            updateStockOutItem.setStatus(2); // 已完成
            updateStockOutItem.setCompleteTime(new Date());
            updateStockOutItem.setUpdateBy(userName);
            updateStockOutItem.setUpdateTime(new Date());
            warehouseStockOutItemService.updateById(updateStockOutItem);
        }

        // 15. 更新出库单状态
        ErpWarehouseStockOut updateStockOut = new ErpWarehouseStockOut();
        updateStockOut.setId(stockOut.getId());
        updateStockOut.setStatus(2); // 已完成
        updateStockOut.setCompleteTime(new Date());
        updateStockOut.setOutTime(new Date());
        updateStockOut.setUpdateBy(userName);
        updateStockOut.setUpdateTime(new Date());
        warehouseStockOutService.updateById(updateStockOut);

        // 16. 更新发货单状态
        OOrderStocking updateShipOrder = new OOrderStocking();
        updateShipOrder.setId(shipOrder.getId());
        updateShipOrder.setSendStatus(2); // 已发货
        updateShipOrder.setShippingTime(new Date());
        updateShipOrder.setUpdateBy(userName);
        updateShipOrder.setUpdateTime(new Date());
        orderStockingService.updateById(updateShipOrder);

        return ResultVo.success("第三方云仓手动出库成功");
    }
}