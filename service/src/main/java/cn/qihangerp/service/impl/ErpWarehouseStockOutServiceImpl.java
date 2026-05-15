package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.StockOutItemRequest;
import cn.qihangerp.model.request.VMSStockOutCreateRequest;
import cn.qihangerp.mapper.OOrderStockingMapper;
import cn.qihangerp.service.*;
import cn.qihangerp.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.mapper.ErpWarehouseStockOutMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author qilip
* @description 针对表【erp_vendor_stock_out(出库单)】的数据库操作Service实现
* @createDate 2025-06-14 13:26:41
*/
@AllArgsConstructor
@Service
public class ErpWarehouseStockOutServiceImpl extends ServiceImpl<ErpWarehouseStockOutMapper, ErpWarehouseStockOut>
    implements ErpWarehouseStockOutService {
    private final ErpWarehouseStockOutItemService outItemService;
    private final ErpWarehouseStockOutItemPositionService outItemPositionService;
    private final ErpWarehouseGoodsStockBatchService goodsInventoryBatchService;
    private final ErpWarehouseGoodsStockService goodsInventoryService;
    private final ErpWarehouseGoodsService warehouseGoodsService;
    private final OOrderStockingItemBatchService orderStockingItemBatchService;
    private final OOrderStockingItemService orderStockingItemService;
    private final OOrderStockingMapper orderStockingMapper;
    @Override
    public PageResult<ErpWarehouseStockOut> queryPageList(ErpWarehouseStockOut bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseStockOut> queryWrapper = new LambdaQueryWrapper<ErpWarehouseStockOut>()
                .eq(ErpWarehouseStockOut::getVendorId, bo.getVendorId())
                .eq( bo.getStatus()!=null, ErpWarehouseStockOut::getStatus, bo.getStatus())
                .eq( bo.getType()!=null, ErpWarehouseStockOut::getType, bo.getType())
                .eq(StringUtils.isNotBlank(bo.getOutNum()), ErpWarehouseStockOut::getOutNum, bo.getOutNum())
                .eq(StringUtils.isNotBlank(bo.getSourceNum()), ErpWarehouseStockOut::getSourceNum, bo.getSourceNum())
                .eq(bo.getSourceId()!=null, ErpWarehouseStockOut::getSourceId, bo.getSourceId())
                ;

        Page<ErpWarehouseStockOut> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }


    @Transactional
    @Override
    public ResultVo<Long> createEntry(Long ownerId, String userName, VMSStockOutCreateRequest request) {
        if(request.getType() == null ) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数type");
        if(request.getItemList().isEmpty()) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数itemList");
        if(StringUtils.isBlank(request.getOutNum())){
            request.setOutNum(DateUtils.parseDateToStr("yyyyMMddHHmmss",new Date()));
        }
        if(StringUtils.isBlank(request.getOperator())){
            request.setOperator(userName);
        }

        Map<Long, List<VMSStockOutCreateRequest.VMSStockOutItem>> goodsGroup = request.getItemList().stream().collect(Collectors.groupingBy(x -> x.getId()));
        Long total = request.getItemList().stream().mapToLong(VMSStockOutCreateRequest.VMSStockOutItem::getQuantity).sum();

        //添加主表信息
        ErpWarehouseStockOut insert = new ErpWarehouseStockOut();
        insert.setVendorId(request.getVendorId());
        insert.setMerchantId(request.getMerchantId());
        insert.setOutNum(request.getOutNum());
        insert.setType(request.getType());
        insert.setSourceNum(request.getSourceNo());
        insert.setSourceId(0L);
        insert.setRemark(request.getRemark());
        insert.setCreateBy(userName);
        insert.setCreateTime(new Date());
        insert.setGoodsUnit(goodsGroup.size());
        insert.setSpecUnit(request.getItemList().size());
        insert.setSpecUnitTotal(total.intValue());
        insert.setOutTotal(0);
        insert.setOperatorId(ownerId);
        insert.setOperatorName(StringUtils.isEmpty(request.getOperator())?userName:request.getOperator());
        insert.setPrintStatus(0);
        insert.setRemark(request.getRemark());
        insert.setStatus(0);//状态（0待入库1部分入库2全部入库）
        this.baseMapper.insert(insert);

        //添加子表信息
        List<ErpWarehouseStockOutItem> itemList = new ArrayList<>();
        for(VMSStockOutCreateRequest.VMSStockOutItem item: request.getItemList()) {
            // 查询商品
            ErpWarehouseGoods warehouseGoods = warehouseGoodsService.getById(item.getId());
            if (warehouseGoods == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultVo.error("仓库商品数据找不到");
            }

            // 查询库存
//            Integer goodsStockQty = goodsInventoryService.getGoodsStockQty(item.getId());
//            if(goodsStockQty.intValue()< item.getQuantity().intValue()){
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                return ResultVo.error("仓库商品库存不足");
//            }

            ErpWarehouseStockOutItem inItem = new ErpWarehouseStockOutItem();
            inItem.setVendorId(insert.getVendorId());

            inItem.setEntryId(insert.getId());
            inItem.setType(request.getType());
            inItem.setGoodsId(item.getId());
            inItem.setGoodsName(warehouseGoods.getGoodsName());
            inItem.setGoodsNum(warehouseGoods.getGoodsNo());
            inItem.setGoodsImage(warehouseGoods.getImageUrl());
            inItem.setSkuName(warehouseGoods.getStandard());
            inItem.setOriginalQuantity(item.getQuantity());
            inItem.setOutQuantity(0);
            inItem.setStatus(0);
            inItem.setCreateBy(userName);
            inItem.setCreateTime(new Date());
            inItem.setWarehouseId(0L);
            inItem.setMerchantId(request.getMerchantId());
            inItem.setVendorId(request.getVendorId());
            itemList.add(inItem);
        }
        outItemService.saveBatch(itemList);
        return ResultVo.success();
    }

    @Override
    public ErpWarehouseStockOut getDetailAndItemById(Long id) {
        ErpWarehouseStockOut erpStockOut = this.baseMapper.selectById(id);
        if(erpStockOut !=null){
            erpStockOut.setItemList(outItemService.list(new LambdaQueryWrapper<ErpWarehouseStockOutItem>()
                    .eq(ErpWarehouseStockOutItem::getEntryId,id)));
            if(!erpStockOut.getItemList().isEmpty()){
                for (var item :erpStockOut.getItemList()){
                    List<ErpWarehouseGoodsStockBatch> list = goodsInventoryBatchService.list(
                            new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                                    .eq(ErpWarehouseGoodsStockBatch::getGoodsId, item.getGoodsId())
                                    .eq(ErpWarehouseGoodsStockBatch::getVendorId, item.getVendorId())
                                    .gt(ErpWarehouseGoodsStockBatch::getCurrentQty,0)
                    );
                    item.setBatchList(list);
                    item.setOutQty(item.getOriginalQuantity()-item.getOutQuantity());
                }
            }
        }
        return erpStockOut;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> stockOut(Long userId, String userName, StockOutItemRequest request) {
        if (request.getEntryItemId() == null) return ResultVo.error(1500, "缺少参数：outItemId");
        if (request.getOutQty() == null || request.getOutQty().longValue() <= 0)
            return ResultVo.error(1500, "缺少参数：出库数量");

        ErpWarehouseStockOutItem outItem = outItemService.getById(request.getEntryItemId());
        if (outItem == null) return ResultVo.error(1500, "出库数据错误");
        // 判断出库数量
        int shengyu = outItem.getOriginalQuantity().intValue() - outItem.getOutQuantity().intValue();

        if (shengyu < request.getOutQty().intValue()) {
            return ResultVo.error("可出库数量不足");
        }

        String outBatch = "";
        if(org.springframework.util.StringUtils.hasText(outItem.getOutBatch())){
            outBatch = outItem.getOutBatch();
        }

        // 判断库存够不够扣减的
        ErpWarehouseGoodsStockBatch batch = goodsInventoryBatchService.getById(request.getBatchId());
        if (batch == null) return ResultVo.error(1500, "库存数据不存在");
        if (batch.getCurrentQty() < request.getOutQty().intValue())
            return ResultVo.error(1500, "库存不足");
//        if (StringUtils.isEmpty(batch.getRemark())) batch.setRemark("");
        // 扣减库存
        // 1扣减批次库存
        ErpWarehouseGoodsStockBatch updateBatch = new ErpWarehouseGoodsStockBatch();
        updateBatch.setCurrentQty(batch.getCurrentQty() - request.getOutQty().intValue());
        updateBatch.setUpdateBy(userName);
        updateBatch.setUpdateTime(new Date());
//        updateBatch.setRemark(batch.getRemark() + "出库扣减库存；");
        updateBatch.setId(batch.getId());
        goodsInventoryBatchService.updateById(updateBatch);
        // 2扣减总库存
        ErpWarehouseGoodsStock goodsInventory = goodsInventoryService.getById(batch.getInventoryId());
        ErpWarehouseGoodsStock updateInventory = new ErpWarehouseGoodsStock();
        updateInventory.setId(goodsInventory.getId());
        updateInventory.setTotalNum(goodsInventory.getTotalNum() - request.getOutQty().intValue());
        updateInventory.setTotalNumValue(updateInventory.getTotalNum().doubleValue());
        updateInventory.setUsableNum(goodsInventory.getUsableNum() - request.getOutQty().intValue());
        updateInventory.setUsableNumValue(updateInventory.getUsableNum().doubleValue());
        updateInventory.setUpdateBy(userName);
        updateInventory.setUpdateTime(new Date());
        goodsInventoryService.updateById(updateInventory);

        // 记录发货备货批次信息
        Long orderStockingItemId = getOrderStockingItemIdByOutItem(outItem);
        if (orderStockingItemId != null) {
            OOrderStockingItemBatch batchRecord = new OOrderStockingItemBatch();
                    batchRecord.setOrderStockingItemId(orderStockingItemId);
                    batchRecord.setBatchId(batch.getId());
                    batchRecord.setBatchNum(batch.getBatchNum());
                    batchRecord.setInventoryId(batch.getInventoryId());
                    batchRecord.setGoodsId(batch.getGoodsId());
                    batchRecord.setGoodsNo(batch.getGoodsNo());
                    batchRecord.setQuantity(request.getOutQty());
                    batchRecord.setUnitCost(batch.getPurPrice() != null ? new java.math.BigDecimal(batch.getPurPrice()) : null);
                    batchRecord.setTotalCost(batch.getPurPrice() != null ? new java.math.BigDecimal(batch.getPurPrice()).multiply(new java.math.BigDecimal(request.getOutQty())) : null);
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
        }
        // 更新自己的状态
        ErpWarehouseStockOutItem outItemUpdate = new ErpWarehouseStockOutItem();
        outItemUpdate.setId(outItem.getId());
        // 判断是否全部出库
        if(shengyu-request.getOutQty().intValue()>0){
            outItemUpdate.setStatus(1);
        }else {
            outItemUpdate.setStatus(2);
        }
        outItemUpdate.setCompleteTime(new Date());
        outItemUpdate.setOutQuantity(outItem.getOutQuantity() + request.getOutQty().intValue());
        if(org.springframework.util.StringUtils.hasText(outItem.getOutBatch())){
            outItemUpdate.setOutBatch(outBatch+","+batch.getBatchNum());
        }else{
            outItemUpdate.setOutBatch(batch.getBatchNum());
        }

        outItemUpdate.setUpdateBy(userName);
        outItemUpdate.setUpdateTime(new Date());
        outItemService.updateById(outItemUpdate);

        // 增加出库item erp_warehouse_stock_out_item_position
        ErpWarehouseStockOutItemPosition itemPosition = new ErpWarehouseStockOutItemPosition();
        itemPosition.setEntryId(outItem.getEntryId());
        itemPosition.setEntryItemId(outItem.getId());
        itemPosition.setGoodsInventoryId(batch.getInventoryId());
        itemPosition.setGoodsInventoryDetailId(batch.getId());
        itemPosition.setQuantity((long) request.getOutQty().intValue());
        itemPosition.setLocationId(0);
        itemPosition.setOperatorId(0);
        itemPosition.setOperatorName(userName);
        itemPosition.setOutTime(new Date());
        itemPosition.setOutBatch(batch.getBatchNum());
        outItemPositionService.save(itemPosition);

        // 更新主表单数据
        ErpWarehouseStockOut erpStockOut = this.baseMapper.selectById(outItem.getEntryId());
        if (erpStockOut.getOutTotal() == null) erpStockOut.setOutTotal(0);
        // 查询入库表单是否入库完成
        List<ErpWarehouseStockOutItem> itemList = outItemService.list(new LambdaQueryWrapper<ErpWarehouseStockOutItem>()
                .eq(ErpWarehouseStockOutItem::getEntryId, outItem.getEntryId())
                .ne(ErpWarehouseStockOutItem::getStatus, 2));

        ErpWarehouseStockOut sUpdate = new ErpWarehouseStockOut();
        if (itemList.isEmpty()) {
            // 全部入库完成了
            sUpdate.setStatus(2);
            sUpdate.setCompleteTime(new Date());
        } else {
            // 部分入库
            sUpdate.setStatus(1);
        }

        sUpdate.setId(outItem.getEntryId());
        sUpdate.setOperatorId(userId);
        sUpdate.setOperatorName(userName);
        sUpdate.setOutTime(new Date());
        sUpdate.setOutTotal(erpStockOut.getOutTotal() + request.getOutQty().intValue());
        sUpdate.setUpdateBy(userName);
        sUpdate.setUpdateTime(new Date());
        this.baseMapper.updateById(sUpdate);

        return ResultVo.success();
    }
    
    /**
     * 根据出库明细获取发货备货明细ID
     * @param outItem 出库明细
     * @return 发货备货明细ID
     */
    private Long getOrderStockingItemIdByOutItem(ErpWarehouseStockOutItem outItem) {
        // 实际实现中，需要根据出库单的来源信息关联到发货备货明细
        // 例如：通过出库单的sourceId或sourceNum关联到发货备货单
        // 然后根据商品ID、仓库ID等信息查询对应的发货备货明细
        
        // 示例实现：假设通过出库单的sourceId关联到发货备货单
        ErpWarehouseStockOut stockOut = this.baseMapper.selectById(outItem.getEntryId());
        if (stockOut != null && stockOut.getSourceId() != null) {
            // 根据sourceId查询发货备货单
            OOrderStocking orderStocking = orderStockingMapper.selectById(stockOut.getSourceId());
            if (orderStocking != null) {
                // 根据商品ID和发货备货单ID查询发货备货明细
                List<OOrderStockingItem> orderStockingItems = orderStockingItemService.list(
                    new LambdaQueryWrapper<OOrderStockingItem>()
                        .eq(OOrderStockingItem::getShipOrderId, orderStocking.getId())
                        .eq(OOrderStockingItem::getGoodsSkuId, outItem.getGoodsId())
                        .eq(OOrderStockingItem::getWarehouseId, outItem.getWarehouseId())
                );
                if (!orderStockingItems.isEmpty()) {
                    return orderStockingItems.get(0).getId();
                }
            }
        }
        
        // 暂时返回null，实际实现需要根据具体业务逻辑调整
        return null;
    }
}




