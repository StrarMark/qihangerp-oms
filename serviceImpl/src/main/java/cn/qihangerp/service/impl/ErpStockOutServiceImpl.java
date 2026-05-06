package cn.qihangerp.service.impl;

import cn.qihangerp.common.*;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.StockOutItemRequest;
import cn.qihangerp.mapper.ErpStockOutMapper;
import cn.qihangerp.mapper.OOrderStockingMapper;
import cn.qihangerp.service.*;
import cn.qihangerp.utils.DateUtils;
import cn.qihangerp.model.vo.GoodsSkuInventoryVo;
import cn.qihangerp.model.request.StockOutCreateRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author qilip
* @description 针对表【wms_stock_out(出库单)】的数据库操作Service实现
* @createDate 2024-09-22 11:13:23
*/
@AllArgsConstructor
@Service
public class ErpStockOutServiceImpl extends ServiceImpl<ErpStockOutMapper, ErpStockOut>
    implements ErpStockOutService {
    private final ErpStockOutMapper outMapper;
    private final ErpStockOutItemService outItemService;
    private final ErpWarehouseService erpWarehouseService;
    private final ErpWarehouseGoodsStockBatchService goodsStockBatchService;
    private final ErpWarehouseGoodsStockService goodsStockService;
    private final OOrderStockingItemBatchService orderStockingItemBatchService;
    private final OOrderStockingItemService orderStockingItemService;
    private final OOrderStockingMapper orderStockingMapper;

    @Override
    public PageResult<ErpStockOut> queryPageList(ErpStockOut bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpStockOut> queryWrapper = new LambdaQueryWrapper<ErpStockOut>()
                .eq(ErpStockOut::getMerchantId, bo.getMerchantId())
                .eq( bo.getShopId()!=null, ErpStockOut::getShopId, bo.getShopId())
                .eq( bo.getShopGroupId()!=null, ErpStockOut::getShopGroupId, bo.getShopGroupId())
                .eq( bo.getStatus()!=null, ErpStockOut::getStatus, bo.getStatus())
                .eq( bo.getType()!=null, ErpStockOut::getType, bo.getType())
                .eq( bo.getWarehouseId()!=null, ErpStockOut::getWarehouseId, bo.getWarehouseId())
                .eq(StringUtils.isNotBlank(bo.getOutNum()), ErpStockOut::getOutNum, bo.getOutNum())
                .eq(StringUtils.isNotBlank(bo.getSourceNum()), ErpStockOut::getSourceNum, bo.getSourceNum())
                .eq(bo.getSourceId()!=null, ErpStockOut::getSourceId, bo.getSourceId())
                ;

        Page<ErpStockOut> pages = outMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    /**
     * 手动创建出库单
     * @param userId
     * @param userName
     * @param request
     * @return
     */
    @Transactional
    @Override
    public ResultVo<Long> createEntry(Long userId, String userName, StockOutCreateRequest request) {
        if(request.getType() == null ) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数type");
        if(request.getItemList().isEmpty()) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数itemList");
        ErpWarehouse erpWarehouse = erpWarehouseService.getById(request.getWarehouseId());
        if(erpWarehouse==null) return ResultVo.error("仓库信息不存在");
        if(StringUtils.isBlank(request.getOutNum())){
            request.setOutNum(DateUtils.parseDateToStr("yyyyMMddHHmmss",new Date()));
        }
        if(StringUtils.isBlank(request.getOperator())){
            request.setOperator(userName);
        }

        Map<Long, List<GoodsSkuInventoryVo>> goodsGroup = request.getItemList().stream().collect(Collectors.groupingBy(x -> x.getGoodsId()));
        Long total = request.getItemList().stream().mapToLong(GoodsSkuInventoryVo::getQuantity).sum();

        //添加主表信息
        ErpStockOut stockOut = new ErpStockOut();
        stockOut.setMerchantId(request.getMerchantId());
        stockOut.setOutNum(request.getOutNum());
        stockOut.setType(request.getType());
        stockOut.setShopId(request.getShopId()==null?0L:request.getShopId());
        stockOut.setShopGroupId(request.getShopGroupId()==null?0L:request.getShopGroupId());
        stockOut.setSourceNum(request.getSourceNo());
        stockOut.setSourceId(0L);
        stockOut.setWarehouseId(0L);
        stockOut.setRemark(request.getRemark());
        stockOut.setCreateBy(userName);
        stockOut.setCreateTime(new Date());
        stockOut.setGoodsUnit(goodsGroup.size());
        stockOut.setSpecUnit(request.getItemList().size());
        stockOut.setSpecUnitTotal(total.intValue());
        stockOut.setOutTotal(0);
        stockOut.setOperatorId(userId.toString());
        stockOut.setOperatorName(StringUtils.isEmpty(request.getOperator())?userName:request.getOperator());
        stockOut.setPrintStatus(0);
        stockOut.setRemark(request.getRemark());
        stockOut.setStatus(0);//状态（0待入库1部分入库2全部入库）
        stockOut.setWarehouseId(request.getWarehouseId());
        stockOut.setWarehouseName(erpWarehouse.getWarehouseName());
        outMapper.insert(stockOut);

        //添加子表信息
        List<ErpStockOutItem> itemList = new ArrayList<>();
        for(GoodsSkuInventoryVo item: request.getItemList()){
            if(org.springframework.util.StringUtils.hasText(item.getBatchId())) {
                ErpWarehouseGoodsStockBatch batch = goodsStockBatchService.getById(item.getBatchId());

                if(batch!=null) {
                    ErpStockOutItem outItem = new ErpStockOutItem();
                    outItem.setMerchantId(request.getMerchantId());
                    outItem.setShopId(request.getShopId()==null?0L:request.getShopId());
                    outItem.setShopGroupId(request.getShopGroupId()==null?0L:request.getShopGroupId());
                    outItem.setEntryId(stockOut.getId());
                    outItem.setType(request.getType());
                    outItem.setBatchId(batch.getId());
                    outItem.setInventoryMode(batch.getInventoryMode());
                    outItem.setGoodsId(batch.getGoodsId());
                    outItem.setPurPrice(batch.getPurPrice());
                    outItem.setSkuId(batch.getGoodsId());
                    outItem.setSkuCode(batch.getGoodsNo());
                    outItem.setGoodsName(item.getGoodsName());
                    outItem.setGoodsNum(item.getGoodsNum());
                    outItem.setSkuName(item.getSkuName());
                    outItem.setGoodsImage(item.getGoodsImg());
                    outItem.setOriginalQuantity(item.getQuantity());
                    outItem.setOutQuantity(0);
                    outItem.setStatus(0);
                    outItem.setCreateBy(userName);
                    outItem.setCreateTime(new Date());
                    outItem.setWarehouseId(batch.getWarehouseId());
                    outItem.setPositionId(batch.getPositionId());

                    outItem.setPositionNum(batch.getPositionNum());
                    itemList.add(outItem);
                }
            }
        }
        outItemService.saveBatch(itemList);
        return ResultVo.success(stockOut.getId());
    }

    @Override
    public ErpStockOut getDetailAndItemById(Long id) {
        ErpStockOut erpStockOut = outMapper.selectById(id);
        if(erpStockOut !=null){
            erpStockOut.setItemList(outItemService.list(new LambdaQueryWrapper<ErpStockOutItem>().eq(ErpStockOutItem::getEntryId,id)));
        }
        return erpStockOut;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> stockOut(Long userId, String userName, StockOutItemRequest request) {
        if(request.getEntryItemId() == null) return ResultVo.error(1500,"缺少参数：outItemId");
        if(request.getOutQty()==null || request.getOutQty().longValue()<=0) return ResultVo.error(1500,"缺少参数：出库数量");

        ErpStockOutItem outItem = outItemService.getById(request.getEntryItemId());
        if(outItem == null) return ResultVo.error(1500,"出库数据错误");
        // 判断库存够不够扣减的
        if(outItem.getBatchId()!=null) {
            /***指定了库存批次***减库存****/
            ErpWarehouseGoodsStockBatch batch = goodsStockBatchService.getById(outItem.getBatchId());
            if (batch == null) return ResultVo.error(1500, "库存数据不存在");
            if (batch.getCurrentQty().longValue() < request.getOutQty().longValue())
                return ResultVo.error(1500, "库存不足");

            if(StringUtils.isEmpty(batch.getRemark())) batch.setRemark("");
            // 扣减库存
            // 1扣减批次库存
            ErpWarehouseGoodsStockBatch updateBatch = new ErpWarehouseGoodsStockBatch();
            updateBatch.setCurrentQty(batch.getCurrentQty() - request.getOutQty());
            updateBatch.setUpdateBy(userName);
            updateBatch.setUpdateTime(new Date());
            updateBatch.setRemark(batch.getRemark()+"出库扣减库存；");
            updateBatch.setId(batch.getId());
            goodsStockBatchService.updateById(updateBatch);

            // 2扣减总库存
            ErpWarehouseGoodsStock goodsInventory = goodsStockService.getById(batch.getInventoryId());
            if(goodsInventory!=null){
                ErpWarehouseGoodsStock updateInventory = new ErpWarehouseGoodsStock();
                updateInventory.setId(goodsInventory.getId());
                updateInventory.setTotalNum(goodsInventory.getTotalNum() - request.getOutQty());
                updateInventory.setUsableNum(goodsInventory.getUsableNum() - request.getOutQty());

                updateInventory.setUpdateBy(userName);
                updateInventory.setUpdateTime(new Date());
                goodsStockService.updateById(updateInventory);
            }

            // 记录发货备货批次信息(出库类型：1 且 存在sourceOrderItemId)
            if (outItem.getType()==1&&outItem.getSourceOrderItemId() != null) {
                OOrderStockingItem orderStockingItem = orderStockingItemService.getById(outItem.getSourceOrderItemId());
                if (orderStockingItem != null) {
                    OOrderStockingItemBatch batchRecord = new OOrderStockingItemBatch();
                    batchRecord.setOrderStockingId(orderStockingItem.getShipOrderId());
                    batchRecord.setOrderStockingItemId(orderStockingItem.getId());
                    batchRecord.setOrderId(orderStockingItem.getOOrderId().toString());
                    batchRecord.setOrderItemId(orderStockingItem.getOOrderItemId().toString());

                    // 谁发的货算谁的
                    batchRecord.setMerchantId(outItem.getMerchantId());
                    batchRecord.setShopId(outItem.getShopId());

                    // 出库的批次
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
            }
        }else{
            /***没有指定库存批次***减库存****/
            // 判断总库存
            List<ErpWarehouseGoodsStock> inventoryList = goodsStockService.list(
                    new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                            .eq(ErpWarehouseGoodsStock::getGoodsId, outItem.getSkuId())
                            .eq(ErpWarehouseGoodsStock::getWarehouseId, outItem.getWarehouseId()));

            if(inventoryList.isEmpty()) return ResultVo.error("没有找到库存数据");
            else if (inventoryList.get(0).getUsableNum().intValue()<request.getOutQty().intValue()) {
                return ResultVo.error("库存不足");
            }
            // 查询出所有的批次
            List<ErpWarehouseGoodsStockBatch> batchList = goodsStockBatchService.list(
                    new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                            .eq(ErpWarehouseGoodsStockBatch::getGoodsId, outItem.getSkuId())
                            .eq(ErpWarehouseGoodsStockBatch::getInventoryId, inventoryList.get(0).getId()));
            if(batchList.isEmpty()) return ResultVo.error("库存批次数据错误");

            // 按次序减少批次库存（先进先出）
            int shengyu = request.getOutQty().intValue();
            for(var batch:batchList){
                if(shengyu==0) break;

                int batchQty = batch.getCurrentQty();
                int deductQuantity = Math.min(batchQty, shengyu);
                if(batchQty>shengyu){
                    ErpWarehouseGoodsStockBatch updateBatch = new ErpWarehouseGoodsStockBatch();
                    updateBatch.setCurrentQty(batchQty - shengyu);
                    updateBatch.setUpdateBy(userName);
                    updateBatch.setUpdateTime(new Date());
                    updateBatch.setRemark((batch.getRemark()==null?"":batch.getRemark())+"出库扣减库存；");
                    updateBatch.setId(batch.getId());
                    goodsStockBatchService.updateById(updateBatch);
                    shengyu=0;
                }else{
                    ErpWarehouseGoodsStockBatch updateBatch = new ErpWarehouseGoodsStockBatch();
                    updateBatch.setCurrentQty(0);
                    updateBatch.setUpdateBy(userName);
                    updateBatch.setUpdateTime(new Date());
                    updateBatch.setRemark((batch.getRemark()==null?"":batch.getRemark())+"出库扣减库存；");
                    updateBatch.setId(batch.getId());
                    goodsStockBatchService.updateById(updateBatch);
                    deductQuantity = batchQty;
                    shengyu=shengyu-batchQty;
                }
                
                // 记录发货备货批次信息(出库类型：1 且 存在sourceOrderItemId)
                if (outItem.getType()==1&&outItem.getSourceOrderItemId() != null) {
                    OOrderStockingItem orderStockingItem = orderStockingItemService.getById(outItem.getSourceOrderItemId());
                    if(orderStockingItem!=null){
                        OOrderStockingItemBatch batchRecord = new OOrderStockingItemBatch();

                        batchRecord.setOrderStockingId(orderStockingItem.getShipOrderId());
                        batchRecord.setOrderStockingItemId(orderStockingItem.getId());
                        batchRecord.setOrderId(orderStockingItem.getOOrderId().toString());
                        batchRecord.setOrderItemId(orderStockingItem.getOOrderItemId().toString());

                        // 谁发的货算谁的
                        batchRecord.setMerchantId(outItem.getMerchantId());
                        batchRecord.setShopId(outItem.getShopId());
                        // 出库的批次
                        batchRecord.setBatchId(batch.getId());
                        batchRecord.setBatchNum(batch.getBatchNum());
                        batchRecord.setInventoryId(batch.getInventoryId());
                        batchRecord.setGoodsId(batch.getGoodsId());
                        batchRecord.setGoodsNo(batch.getGoodsNo());
                        batchRecord.setQuantity(deductQuantity);
                        batchRecord.setUnitCost(batch.getPurPrice() != null ? new java.math.BigDecimal(batch.getPurPrice()) : null);
                        batchRecord.setTotalCost(batch.getPurPrice() != null ? new java.math.BigDecimal(batch.getPurPrice()).multiply(new java.math.BigDecimal(deductQuantity)) : null);
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

                }
            }
            // 减少总库存
            // 2扣减总库存

            ErpWarehouseGoodsStock updateInventory = new ErpWarehouseGoodsStock();
            updateInventory.setId(inventoryList.get(0).getId());
            updateInventory.setTotalNum(inventoryList.get(0).getTotalNum() - request.getOutQty());
            updateInventory.setUsableNum(inventoryList.get(0).getUsableNum() - request.getOutQty());
            updateInventory.setUpdateBy(userName);
            updateInventory.setUpdateTime(new Date());
            goodsStockService.updateById(updateInventory);
        }



        // 更新自己的状态
        ErpStockOutItem outItemUpdate = new ErpStockOutItem();
        outItemUpdate.setId(outItem.getId());
        outItemUpdate.setStatus(2);
        outItemUpdate.setCompleteTime(new Date());
        outItemUpdate.setOutQuantity(outItem.getOutQuantity()+ request.getOutQty());
        outItemUpdate.setUpdateBy(userName);
        outItemUpdate.setUpdateTime(new Date());
        outItemService.updateById(outItemUpdate);

        // 更新主表单数据
        ErpStockOut erpStockOut = outMapper.selectById(outItem.getEntryId());
        if(erpStockOut.getOutTotal()==null) erpStockOut.setOutTotal(0);
        // 查询入库表单是否入库完成
        List<ErpStockOutItem> itemList = outItemService.list(new LambdaQueryWrapper<ErpStockOutItem>().eq(ErpStockOutItem::getEntryId,outItem.getEntryId()).ne(ErpStockOutItem::getStatus, 2));
        ErpStockOut sUpdate = new ErpStockOut();
        if (itemList.isEmpty()) {
            // 全部入库完成了
            sUpdate.setStatus(2);
            sUpdate.setCompleteTime(new Date());
        } else {
            // 部分入库
            sUpdate.setStatus(1);
        }

        sUpdate.setId(outItem.getEntryId());
        sUpdate.setOperatorId(userId.toString());
        sUpdate.setOperatorName(userName);
        sUpdate.setOutTime(new Date());
        sUpdate.setOutTotal(erpStockOut.getOutTotal()+request.getOutQty().intValue());
        sUpdate.setUpdateBy(userName);
        sUpdate.setUpdateTime(new Date());
        outMapper.updateById(sUpdate);

        return ResultVo.success(outItem.getEntryId());
    }
    

}




