package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.StockInItem;
import cn.qihangerp.model.request.StockInRequest;
import cn.qihangerp.model.request.WarehouseStockInCreateItem;
import cn.qihangerp.model.request.WarehouseStockInCreateRequest;
import cn.qihangerp.service.*;
import cn.qihangerp.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.mapper.ErpWarehouseStockInMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【erp_vendor_stock_in(入库单)】的数据库操作Service实现
* @createDate 2025-06-14 12:47:54
*/
@AllArgsConstructor
@Service
public class ErpWarehouseStockInServiceImpl extends ServiceImpl<ErpWarehouseStockInMapper, ErpWarehouseStockIn>
    implements ErpWarehouseStockInService {
    private final ErpWarehouseStockInItemService inItemService;
    private final ErpWarehouseGoodsService warehouseGoodsService;
    private final ErpWarehouseGoodsStockService warehouseGoodsStockService;
    private final ErpWarehouseGoodsStockBatchService warehouseGoodsStockBatchService;
    private final ErpWarehouseService warehouseService;
    private final OGoodsSkuService goodsSkuService;

    /**
     * 分页查询
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<ErpWarehouseStockIn> queryPageList(ErpWarehouseStockIn bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseStockIn> queryWrapper = new LambdaQueryWrapper<ErpWarehouseStockIn>()
                .eq(bo.getVendorId()!=null, ErpWarehouseStockIn::getVendorId,bo.getVendorId())
                .eq( bo.getStatus()!=null, ErpWarehouseStockIn::getStatus, bo.getStatus())
                .eq(bo.getMerchantId()!=null, ErpWarehouseStockIn::getMerchantId, bo.getMerchantId())
                .eq(StringUtils.isNotBlank(bo.getStockInNum()), ErpWarehouseStockIn::getStockInNum, bo.getStockInNum())
                .eq(StringUtils.isNotBlank(bo.getSourceNo()), ErpWarehouseStockIn::getSourceNo, bo.getSourceNo())

                ;

        Page<ErpWarehouseStockIn> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> createEntry(Long userId, String userName, WarehouseStockInCreateRequest request) {
        if(request.getStockInType() == null ) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数stockInType");
        if(request.getItemList().isEmpty()) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数itemList");
        if(StringUtils.isBlank(request.getStockInNum())){
            request.setStockInNum(DateUtils.parseDateToStr("yyyyMMddHHmmss",new Date()));
        }
        if(StringUtils.isBlank(request.getStockInOperator())){
            request.setStockInOperator(userName);
        }

//        Map<String, List<StockInCreateItem>> goodsGroup = request.getItemList().stream().collect(Collectors.groupingBy(x -> x.getGoodsId()));
        Long total = request.getItemList().stream().mapToLong(WarehouseStockInCreateItem::getQuantity).sum();

        //添加主表信息
        ErpWarehouseStockIn insert = new ErpWarehouseStockIn();
        insert.setVendorId(request.getWarehouseId());
        insert.setMerchantId(request.getMerchantId());
        insert.setStockInNum(request.getStockInNum());
        insert.setStockInType(request.getStockInType());
        insert.setSourceType(0);//来源（0自己入库1商户申请入库）
        insert.setApplyId(0L);
        insert.setStockInOperator(request.getStockInOperator());
        insert.setStockInOperatorId(userId);
//        insert.setStockInTime(new Date());
        insert.setSourceNo(request.getSourceNo());
        insert.setRemark(request.getRemark());
        insert.setCreateBy(userName);
        insert.setCreateTime(new Date());
        insert.setGoodsUnit(request.getItemList().size());
        insert.setGoodsSkuUnit(request.getItemList().size());
        insert.setTotal(total.intValue());
        insert.setStatus(0);//状态（0待入库1部分入库2全部入库）
        this.baseMapper.insert(insert);

        //添加子表信息
        List<ErpWarehouseStockInItem> itemList = new ArrayList<>();
        for(WarehouseStockInCreateItem item: request.getItemList()){
            if(item.getId()!=null) {
                //TODO:换成仓库goodsId
                ErpWarehouseGoods goods = warehouseGoodsService.getById(item.getId());

                if(goods!=null) {

                    ErpWarehouseStockInItem inItem = new ErpWarehouseStockInItem();
                    inItem.setMerchantId(insert.getMerchantId());
                    inItem.setVendorId(insert.getVendorId());
                    inItem.setStockInId(insert.getId());
                    inItem.setGoodsId(goods.getId());
                    inItem.setGoodsName(item.getGoodsName());
                    inItem.setGoodsImage(item.getImageUrl());
                    inItem.setGoodsNo(goods.getErpGoodsNo());
                    inItem.setSkuName(item.getStandard());
                    inItem.setQuantity(item.getQuantity());
                    inItem.setInQuantity(0);
                    inItem.setStatus(0);
                    inItem.setCreateBy(userName);
                    inItem.setCreateTime(new Date());
                    itemList.add(inItem);
                }
            }
        }
        inItemService.saveBatch(itemList);
        return ResultVo.success();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> stockIn(Long userId, String userName, StockInRequest request) {

        if (request.getStockInId() == null) return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数stockInId");
        if (request.getWarehouseId() == null||request.getWarehouseId()<=0) return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数warehouseId");
        if (request.getItemList().isEmpty()) return ResultVo.error(ResultVoEnum.ParamsError, "缺少入库数据");
        ErpWarehouse erpWarehouse = warehouseService.getById(request.getWarehouseId());
        if(erpWarehouse==null) return ResultVo.error("仓库不存在");

        ErpWarehouseStockIn erpStockIn = this.baseMapper.selectById(request.getStockInId());
        if (erpStockIn == null) return ResultVo.error(ResultVoEnum.NotFound, "没有找到入库单");
        else if (erpStockIn.getStatus() == 2) {
            return ResultVo.error(ResultVoEnum.SystemException, "入库单状态不能入库");
        }

        List<StockInItem> waitList = new ArrayList<>();
        for (StockInItem item : request.getItemList()) {
//            if (item.getIntoQuantity() != null && item.getIntoQuantity() > 0 && item.getPositionId() != null && item.getPositionId() > 0) {
//                waitList.add(item);
//            }
            if (item.getIntoQuantity() != null && item.getIntoQuantity() > 0) {
                waitList.add(item);
            }
        }
        if (waitList.size() == 0) return ResultVo.error(ResultVoEnum.ParamsError, "缺少入库明细数据");

        // 开始入库
        for (StockInItem item : waitList) {
            // 查询明细
            ErpWarehouseStockInItem stockInItem = inItemService.getById(item.getId());
            if (stockInItem == null) {
                return ResultVo.error(ResultVoEnum.DataError, "数据错误！没有找到入库单明细");
            }

            // 查询商品
            ErpWarehouseGoods warehouseGoods = warehouseGoodsService.getById(stockInItem.getGoodsId());
            if (warehouseGoods == null) {
                return ResultVo.error("数据错误！找不到商品："+stockInItem.getGoodsId());
            }

            // 添加库存操作表
            Long goodsStockId = null;
            // 增加商品库存表
            List<ErpWarehouseGoodsStock> warehouseGoodsStockList = warehouseGoodsStockService.list(new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                    .eq(ErpWarehouseGoodsStock::getWarehouseId, request.getWarehouseId())
                    .eq(ErpWarehouseGoodsStock::getMerchantId,stockInItem.getMerchantId())
                    .eq(ErpWarehouseGoodsStock::getGoodsId, stockInItem.getGoodsId())
                    .eq(ErpWarehouseGoodsStock::getStockStatus,1)
            );

            if (warehouseGoodsStockList.isEmpty()) {
                // 没有库存信息，新增
                ErpWarehouseGoodsStock inventory = new ErpWarehouseGoodsStock();
                inventory.setWarehouseId(request.getWarehouseId());
                inventory.setWarehouseNo(erpWarehouse.getWarehouseNo());
                inventory.setWarehouseName(erpWarehouse.getWarehouseName());
                inventory.setWarehouseType(erpWarehouse.getWarehouseType());
                inventory.setMerchantId(stockInItem.getMerchantId());
                inventory.setGoodsNo(stockInItem.getGoodsNo());
                inventory.setGoodsId(stockInItem.getGoodsId());
                inventory.setGoodsName(stockInItem.getGoodsName());
                inventory.setErpGoodsNo(warehouseGoods.getErpGoodsNo());
                inventory.setErpGoodsSign(warehouseGoods.getErpGoodsSign());
                inventory.setErpGoodsId(warehouseGoods.getErpGoodsId());
                inventory.setErpGoodsSkuId(warehouseGoods.getErpGoodsSkuId());

                inventory.setSellerGoodsSign("");
                inventory.setStockStatus(1);//库存状态：1-良品；2-残品
                inventory.setStockType(1);//库存类型：1-可销售；2-可退品；3-商家预留；4-仓库锁定；5-临期锁定；6-盘点锁定；7-内配出库锁定；8-在途库存；9-质押；10-VMI锁定；11-过期锁定；13-在途差异

                inventory.setTotalNum(item.getIntoQuantity());
                inventory.setUsableNum(item.getIntoQuantity());
                inventory.setTotalNumValue(item.getIntoQuantity().doubleValue());
                inventory.setUsableNumValue(item.getIntoQuantity().doubleValue());
                inventory.setCreateBy("首次入库"+userName);
                inventory.setCreateTime(new Date());
                warehouseGoodsStockService.save(inventory);
                goodsStockId = inventory.getId();
            } else {
                //修改
                ErpWarehouseGoodsStock update = new ErpWarehouseGoodsStock();
                update.setId(warehouseGoodsStockList.get(0).getId());
                update.setUpdateBy(userName);
                update.setUpdateTime(new Date());
                update.setTotalNum(warehouseGoodsStockList.get(0).getTotalNum() + item.getIntoQuantity());
                update.setTotalNumValue(update.getTotalNum().doubleValue());
                update.setUsableNum(warehouseGoodsStockList.get(0).getUsableNum()+item.getIntoQuantity());
                update.setUsableNumValue(update.getUsableNum().doubleValue());
                warehouseGoodsStockService.updateById(update);
                goodsStockId = warehouseGoodsStockList.get(0).getId();
            }

            // 增加商品库存批次表
            ErpWarehouseGoodsStockBatch inventoryBatch = new ErpWarehouseGoodsStockBatch();
            inventoryBatch.setInventoryId(goodsStockId);
            inventoryBatch.setBatchNum(DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()));
            inventoryBatch.setOriginQty(item.getIntoQuantity());
            inventoryBatch.setCurrentQty(item.getIntoQuantity());
            inventoryBatch.setGoodsNo(stockInItem.getGoodsNo());
            inventoryBatch.setGoodsId(stockInItem.getGoodsId());
            inventoryBatch.setWarehouseId(request.getWarehouseId());
            inventoryBatch.setVendorId(request.getWarehouseId());
            if(item.getPositionId()!=null){
                inventoryBatch.setPositionId(item.getPositionId());
            }else{
                inventoryBatch.setPositionId(0L);
            }

            inventoryBatch.setPositionNum(item.getPositionNum());
            inventoryBatch.setCreateTime(new Date());
            inventoryBatch.setCreateBy(userName);
            warehouseGoodsStockBatchService.save(inventoryBatch);

            // 回写状态
            ErpWarehouseStockInItem update = new ErpWarehouseStockInItem();
            update.setId(stockInItem.getId());
            update.setInQuantity(stockInItem.getInQuantity() + item.getIntoQuantity());
            update.setStatus(2);
            update.setWarehouseId(request.getWarehouseId());
            update.setPositionId(item.getPositionId());
            update.setPositionNum(item.getPositionNum());
            update.setUpdateBy(userName);
            update.setUpdateTime(new Date());
            inItemService.updateById(update);
        }

        // 查询入库表单是否入库完成
        List<ErpWarehouseStockInItem> itemList = inItemService.list(new LambdaQueryWrapper<ErpWarehouseStockInItem>()
                .eq(ErpWarehouseStockInItem::getStockInId, erpStockIn.getId())
                .ne(ErpWarehouseStockInItem::getStatus, 2));

        ErpWarehouseStockIn sUpdate = new ErpWarehouseStockIn();
        if (itemList.isEmpty()) {
            // 全部入库完成了
            sUpdate.setStatus(2);
        } else {
            // 部分入库
            sUpdate.setStatus(1);
        }
        sUpdate.setId(erpStockIn.getId());
        sUpdate.setStockInOperatorId(userId);
        sUpdate.setStockInOperator(request.getStockInOperator());
        sUpdate.setStockInTime(new Date());
        sUpdate.setUpdateBy(userName);
        sUpdate.setUpdateTime(new Date());
        this.baseMapper.updateById(sUpdate);


        return ResultVo.success();
    }
    @Override
    public ErpWarehouseStockIn getDetailAndItemById(Long id) {
        ErpWarehouseStockIn erpStockIn = this.baseMapper.selectById(id);
        if(erpStockIn !=null){
            erpStockIn.setItemList(inItemService.list(new LambdaQueryWrapper<ErpWarehouseStockInItem>()
                    .eq(ErpWarehouseStockInItem::getStockInId,id)));
            return erpStockIn;
        }else
            return null;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo updateInventory(Long warehouseId, String skuCode, Integer qty, Boolean initBatch) {
        OGoodsSku goodsSku = goodsSkuService.getGoodsSkuByCode(skuCode);
        if (goodsSku == null) {
            goodsSku = goodsSkuService.getGoodsSkuByOuterSkuCode(skuCode);
        }
        if (goodsSku == null) return ResultVo.error("找不到SKU");

        ErpWarehouse erpWarehouse = warehouseService.getById(warehouseId);
        if (erpWarehouse == null) {
            return ResultVo.error("仓库不存在");
        }
        // 统一处理逻辑，所有仓库类型都使用云仓的表结构
        Long warehouseGoodsId = 0L;
        // 增加仓库商品表数据 erp_warehouse_goods
        List<ErpWarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(ErpWarehouseGoods::getWarehouseId, warehouseId)
                .eq(ErpWarehouseGoods::getErpGoodsSkuId, goodsSku.getId()));
        if (warehouseGoodsList.isEmpty()) {
            // 新增
            ErpWarehouseGoods warehouseGoods = new ErpWarehouseGoods();
            warehouseGoods.setErpGoodsNo(goodsSku.getSkuCode());
            warehouseGoods.setErpGoodsSign(goodsSku.getGoodsNum());
            warehouseGoods.setGoodsNo(goodsSku.getGoodsNum());
            warehouseGoods.setGoodsName(goodsSku.getGoodsName());
            warehouseGoods.setStandard(goodsSku.getSkuName());
            warehouseGoods.setImageUrl(goodsSku.getColorImage());
            warehouseGoods.setColor(goodsSku.getColorValue());
            warehouseGoods.setSize(goodsSku.getSizeValue());
            warehouseGoods.setErpGoodsId(Long.parseLong(goodsSku.getGoodsId()));
            warehouseGoods.setErpGoodsSkuId(Long.parseLong(goodsSku.getId()));
            warehouseGoods.setMerchantId(goodsSku.getMerchantId());
            warehouseGoods.setWarehouseNo(erpWarehouse.getWarehouseNo());
            warehouseGoods.setWarehouseId(warehouseId);
            warehouseGoods.setWarehouseType(erpWarehouse.getWarehouseType());
            warehouseGoods.setCreateTime(new Date());
            // 添加仓库商品数据
            warehouseGoodsService.save(warehouseGoods);
            warehouseGoodsId = warehouseGoods.getId();
        }else{
            warehouseGoodsId = warehouseGoodsList.get(0).getId();
        }

        //增加仓库商品库存
        List<ErpWarehouseGoodsStock> warehouseGoodsStockList = warehouseGoodsStockService.list(new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                .eq(ErpWarehouseGoodsStock::getWarehouseId, warehouseId)
                .eq(ErpWarehouseGoodsStock::getGoodsId, warehouseGoodsId));
        if (warehouseGoodsStockList==null|| warehouseGoodsStockList.isEmpty()) {
            ErpWarehouseGoodsStock insert = new ErpWarehouseGoodsStock();
            insert.setGoodsId(warehouseGoodsId);
            insert.setWarehouseId(warehouseId);
            insert.setGoodsNo(goodsSku.getGoodsNum());
            insert.setGoodsName(goodsSku.getGoodsName());
            insert.setWarehouseType(erpWarehouse.getWarehouseType());
            insert.setWarehouseNo(erpWarehouse.getWarehouseNo());
            insert.setWarehouseName(erpWarehouse.getWarehouseName());
            insert.setSellerGoodsSign(goodsSku.getGoodsNum());
            insert.setErpGoodsId(Long.parseLong(goodsSku.getGoodsId()));
            insert.setErpGoodsSkuId(Long.parseLong(goodsSku.getId()));
            insert.setStockStatus(1);
            insert.setStockType(1);
            insert.setTotalNum(qty);
            insert.setTotalNumValue(qty.doubleValue());
            insert.setUsableNum(qty);
            insert.setUsableNumValue(qty.doubleValue());
            insert.setMerchantId(goodsSku.getMerchantId());
            insert.setCreateTime(new Date());
            insert.setCreateBy("手动同步库存");
            warehouseGoodsStockService.save(insert);

            // 如果初始化批次，则执行如下操作
            if (initBatch) {
                ErpWarehouseGoodsStockBatch insertBatch = new ErpWarehouseGoodsStockBatch();
                insertBatch.setInventoryId(insert.getId());
//                    insertBatch.setBatchNum(DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()));
                insertBatch.setBatchNum("000000");
                insertBatch.setGoodsId(warehouseGoodsId);
                insertBatch.setWarehouseId(warehouseId);
                insertBatch.setVendorId(warehouseId);
                insertBatch.setGoodsNo(goodsSku.getGoodsNum());
                insertBatch.setOriginQty(qty);
                insertBatch.setCurrentQty(qty);
//                    insertBatch.setRemark("手动同步库存");
                insertBatch.setPositionId(0L);
                insertBatch.setCreateTime(new Date());
                insertBatch.setCreateBy("手动同步库存");
                insertBatch.setUpdateTime(new Date());
                warehouseGoodsStockBatchService.save(insertBatch);
            }
        }else{
            // 存在，修改（修改库存）

            List<ErpWarehouseGoodsStockBatch> batchList = warehouseGoodsStockBatchService.list(new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                    .eq(ErpWarehouseGoodsStockBatch::getInventoryId, warehouseGoodsStockList.get(0).getId())
                    .ne(ErpWarehouseGoodsStockBatch::getBatchNum,"000000"));
            if(batchList==null || batchList.isEmpty()) {
                // 修改批次（没有其他批次允许修改库存总数）
                ErpWarehouseGoodsStock update = new ErpWarehouseGoodsStock();
                update.setId(warehouseGoodsStockList.get(0).getId());
                update.setTotalNum(qty);
                update.setTotalNumValue(qty.doubleValue());
                update.setUsableNum(qty);
                update.setUsableNumValue(qty.doubleValue());
                update.setUpdateTime(new Date());
                update.setUpdateBy("手动修改库存数量");
                warehouseGoodsStockService.updateById(update);

                // 更新批次
                List<ErpWarehouseGoodsStockBatch> batch1 = warehouseGoodsStockBatchService.list(new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                        .eq(ErpWarehouseGoodsStockBatch::getInventoryId,update.getId())
                        .eq(ErpWarehouseGoodsStockBatch::getBatchNum,"000000"));
                if (batch1 == null || batch1.isEmpty()) {
                    // 新增批次
                    ErpWarehouseGoodsStockBatch insertBatch = new ErpWarehouseGoodsStockBatch();
                    insertBatch.setInventoryId(update.getId());
//                        insertBatch.setBatchNum(DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()));
                    insertBatch.setBatchNum("000000");
                    insertBatch.setGoodsId(warehouseGoodsId);
                    insertBatch.setWarehouseId(warehouseId);
                    insertBatch.setVendorId(warehouseId);
                    insertBatch.setGoodsNo(goodsSku.getGoodsNum());
                    insertBatch.setOriginQty(qty);
                    insertBatch.setCurrentQty(qty);
//                        insertBatch.setRemark("手动同步库存");
                    insertBatch.setPositionId(0L);
                    insertBatch.setCreateTime(new Date());
                    insertBatch.setCreateBy("手动同步库存");
                    insertBatch.setUpdateTime(new Date());
                    warehouseGoodsStockBatchService.save(insertBatch);
                }else{
                    // 修改批次
                    ErpWarehouseGoodsStockBatch inventoryBatch = new ErpWarehouseGoodsStockBatch();
                    inventoryBatch.setId(batch1.get(0).getId());
                    inventoryBatch.setOriginQty(qty);
                    inventoryBatch.setCurrentQty(qty);
                    inventoryBatch.setUpdateTime(new Date());
                    inventoryBatch.setUpdateBy("飞书库存初始化--修改");
                    warehouseGoodsStockBatchService.updateById(inventoryBatch);
                }

            }else {
                return ResultVo.error("库存存在批次数据，不允许手动修改库存");
            }


        }
        return ResultVo.success();
    }
}




