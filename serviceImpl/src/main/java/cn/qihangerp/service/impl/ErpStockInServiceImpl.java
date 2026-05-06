package cn.qihangerp.service.impl;

import cn.qihangerp.common.*;
import cn.qihangerp.enums.EnumWarehouseType;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.*;
import cn.qihangerp.mapper.ErpWarehousePositionMapper;
import cn.qihangerp.service.*;
import cn.qihangerp.utils.DateUtils;
import cn.qihangerp.mapper.ErpStockInMapper;
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
* @description 针对表【wms_stock_in(入库单)】的数据库操作Service实现
* @createDate 2024-09-22 16:10:08
*/
@AllArgsConstructor
@Service
public class ErpStockInServiceImpl extends ServiceImpl<ErpStockInMapper, ErpStockIn>
    implements ErpStockInService {
    private final ErpStockInMapper mapper;
    private final ErpStockInItemService inItemService;
    private final OGoodsSkuService skuService;
    private final OGoodsService goodsService;
    private final ErpWarehousePositionMapper warehousePositionMapper;
    private final ErpWarehouseGoodsService warehouseGoodsService;
    private final ErpWarehouseService warehouseService;
    private final ErpWarehouseStockInService vendorStockInService;
    private final ErpWarehouseStockInItemService vendorStockInItemService;
    private final ErpWarehouseGoodsStockService warehouseGoodsStockService;
    private final ErpWarehouseGoodsStockBatchService warehouseGoodsStockBatchService;


    @Override
    public PageResult<ErpStockIn> queryPageList(ErpStockIn bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpStockIn> queryWrapper = new LambdaQueryWrapper<ErpStockIn>()
                .eq(bo.getMerchantId()!=null,ErpStockIn::getMerchantId,bo.getMerchantId())
                .eq(bo.getShopId()!=null,ErpStockIn::getShopId,bo.getShopId())
                .eq(bo.getWarehouseId()!=null,ErpStockIn::getWarehouseId,bo.getWarehouseId())
                .eq( bo.getStatus()!=null, ErpStockIn::getStatus, bo.getStatus())
                .eq( bo.getStockInType()!=null, ErpStockIn::getStockInType, bo.getStockInType())
                .eq(StringUtils.isNotBlank(bo.getStockInNum()), ErpStockIn::getStockInNum, bo.getStockInNum())
                .eq(StringUtils.isNotBlank(bo.getSourceNo()), ErpStockIn::getSourceNo, bo.getSourceNo())
                .eq(bo.getSourceId()!=null, ErpStockIn::getSourceId, bo.getSourceId())
            ;

        Page<ErpStockIn> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(!pages.getRecords().isEmpty()){
            for (ErpStockIn record : pages.getRecords()){
                record.setItemList(inItemService.list(new LambdaQueryWrapper<ErpStockInItem>().eq(ErpStockInItem::getStockInId,record.getId())));
            }
        }
        return PageResult.build(pages);
    }

    @Override
    public PageResult<ErpStockIn> queryCloudWarehouseStockInPageList(ErpStockIn bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpStockIn> queryWrapper = new LambdaQueryWrapper<ErpStockIn>()
                .eq(bo.getMerchantId()!=null,ErpStockIn::getMerchantId,bo.getMerchantId())
                .eq(bo.getWarehouseId()!=null,ErpStockIn::getWarehouseId,bo.getWarehouseId())
                .eq( bo.getStatus()!=null, ErpStockIn::getStatus, bo.getStatus())
                .eq( bo.getStockInType()!=null, ErpStockIn::getStockInType, bo.getStockInType())
                .eq(StringUtils.isNotBlank(bo.getStockInNum()), ErpStockIn::getStockInNum, bo.getStockInNum())
                .eq(StringUtils.isNotBlank(bo.getSourceNo()), ErpStockIn::getSourceNo, bo.getSourceNo())
                .eq(bo.getSourceId()!=null, ErpStockIn::getSourceId, bo.getSourceId())
                ;

        Page<ErpStockIn> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(!pages.getRecords().isEmpty()){
            for (ErpStockIn record : pages.getRecords()){
                record.setItemList(inItemService.list(new LambdaQueryWrapper<ErpStockInItem>().eq(ErpStockInItem::getStockInId,record.getId())));
            }
        }
        return PageResult.build(pages);
    }

    @Transactional
    @Override
    public ResultVo<Long> createEntry(Long userId, String userName, StockInCreateRequest request) {
        if(request.getStockInType() == null ) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数stockInType");
        if(request.getItemList().isEmpty()) return ResultVo.error(ResultVoEnum.ParamsError,"缺少参数itemList");
        if(request.getWarehouseId()==null) return ResultVo.error("缺少参数：warehouseId");

        ErpWarehouse erpWarehouse = warehouseService.getById(request.getWarehouseId());
        if(erpWarehouse==null) {
            return ResultVo.error("仓库不存在");
        }
        if(StringUtils.isBlank(request.getStockInNum())){
            request.setStockInNum(DateUtils.parseDateToStr("yyyyMMddHHmmss",new Date()));
        }
        if(StringUtils.isBlank(request.getStockInOperator())){
            request.setStockInOperator(userName);
        }

        Map<String, List<StockInCreateItem>> goodsGroup = request.getItemList().stream().collect(Collectors.groupingBy(x -> x.getGoodsId()));
        Long total = request.getItemList().stream().mapToLong(StockInCreateItem::getQuantity).sum();
        //添加主表信息
        ErpStockIn insert = new ErpStockIn();
        insert.setMerchantId(request.getMerchantId());
        insert.setShopId(request.getShopId());
        insert.setStockInNum(request.getStockInNum());
        insert.setStockInType(request.getStockInType());
        insert.setStockInOperator(request.getStockInOperator());
        insert.setStockInOperatorId(userId+"");
//        insert.setStockInTime(new Date());
        insert.setSourceNo(request.getSourceNo());
        insert.setRemark(request.getRemark());
        insert.setCreateBy(userName);
        insert.setCreateTime(new Date());
        insert.setSourceGoodsUnit(goodsGroup.size());
        insert.setSourceSpecUnit(request.getItemList().size());
        insert.setSourceSpecUnitTotal(total.intValue());
        insert.setStatus(0);//状态（0待入库1部分入库2全部入库）
        insert.setWarehouseId(erpWarehouse.getId());
        insert.setWarehouseNo(erpWarehouse.getWarehouseNo());
        insert.setWarehouseName(erpWarehouse.getWarehouseName());
        insert.setWarehouseType(erpWarehouse.getWarehouseType());
        mapper.insert(insert);

        //添加子表信息
        List<ErpStockInItem> itemList = new ArrayList<>();
        for(StockInCreateItem item: request.getItemList()){
            if(item.getSkuId()!=null) {
                OGoodsSku goodsSku = skuService.getById(item.getSkuId());
                if(goodsSku!=null) {
                    OGoods goods = goodsService.getById(goodsSku.getGoodsId());
                    ErpStockInItem inItem = new ErpStockInItem();
                    inItem.setMerchantId(request.getMerchantId());
                    inItem.setShopId(request.getShopId());
                    inItem.setStockInId(insert.getId());
                    inItem.setStockInType(insert.getStockInType());
                    inItem.setSourceNo(insert.getSourceNo());
                    inItem.setSourceId(0L);
                    inItem.setSourceItemId(0L);
                    inItem.setGoodsId(goodsSku.getGoodsId());
                    inItem.setGoodsName(item.getGoodsName());
                    inItem.setGoodsImage(item.getGoodsImg());
                    inItem.setGoodsNum(goods!=null?goods.getGoodsNum():"");
                    inItem.setSkuName(item.getSkuName());
                    inItem.setSkuId(item.getSkuId());
                    inItem.setSkuCode(item.getSkuCode());
                    inItem.setQuantity(item.getQuantity());
                    inItem.setInQuantity(0);
                    inItem.setPurPrice(item.getPurPrice());
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
        if (request.getWarehouseId() == null) return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数warehouseId");
        if (request.getItemList().isEmpty()) return ResultVo.error(ResultVoEnum.ParamsError, "缺少入库数据");

        ErpStockIn erpStockIn = mapper.selectById(request.getStockInId());
        if (erpStockIn == null) return ResultVo.error(ResultVoEnum.NotFound, "没有找到入库单");
        else if (erpStockIn.getStatus() == 2) {
            return ResultVo.error(ResultVoEnum.SystemException, "入库单状态不能入库");
        }

        List<StockInItem> waitList = new ArrayList<>();
        for (StockInItem item : request.getItemList()) {
//            if (item.getIntoQuantity() != null && item.getIntoQuantity() > 0 && item.getPositionId() != null && item.getPositionId() > 0) {
            if (item.getIntoQuantity() != null && item.getIntoQuantity() > 0 ) {
                waitList.add(item);
            }
        }
        if (waitList.size() == 0) return ResultVo.error(ResultVoEnum.ParamsError, "缺少入库明细数据");

        // 开始入库
        for (StockInItem item : waitList) {
            // 查询明细
            ErpStockInItem stockInItem = inItemService.getById(item.getId());
            if (stockInItem == null) {
                return ResultVo.error(ResultVoEnum.DataError, "数据错误！没有找到入库单明细");
            }
            OGoodsSku oGoodsSku = skuService.getById(stockInItem.getSkuId());
            // 添加库存操作表

            Long goodsInventoryId = null;
            Long warehouseGoodsId = null;
            // 增加商品库存表
            List<ErpWarehouseGoodsStock> inventoryList = warehouseGoodsStockService.list(
                    new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                            .eq(ErpWarehouseGoodsStock::getGoodsId, stockInItem.getSkuId()));
            if (inventoryList.isEmpty()) {
                // 新增
                ErpWarehouseGoodsStock inventory = new ErpWarehouseGoodsStock();
                inventory.setWarehouseId(request.getWarehouseId());
                inventory.setMerchantId(stockInItem.getMerchantId());
                inventory.setGoodsId(Long.parseLong(stockInItem.getGoodsId()));
                inventory.setGoodsNo(oGoodsSku.getSkuCode());
                inventory.setErpGoodsNo(oGoodsSku.getSkuCode());
                inventory.setErpGoodsSign(oGoodsSku.getGoodsNum());

                inventory.setErpGoodsSkuId(Long.parseLong(stockInItem.getSkuId()));

                inventory.setGoodsName(oGoodsSku.getGoodsName());
//                inventory.setGoodsImg(oGoodsSku.getColorImage());
                inventory.setGoodsName(oGoodsSku.getSkuName());
                inventory.setUsableNum(item.getIntoQuantity());
//                inventory.setIsDelete(0);
                inventory.setCreateBy(userName);
                inventory.setCreateTime(new Date());
                warehouseGoodsStockService.save(inventory);
                goodsInventoryId = inventory.getId();

                // 增加仓库商品表数据 erp_warehouse_goods
                List<ErpWarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(
                        new LambdaQueryWrapper<ErpWarehouseGoods>()
                                .eq(ErpWarehouseGoods::getWarehouseId, request.getWarehouseId())
                        .eq(ErpWarehouseGoods::getErpGoodsSkuId, inventory.getErpGoodsSkuId()));

                if(warehouseGoodsList.isEmpty()) {
                    // 新增
                    ErpWarehouseGoods warehouseGoods = new ErpWarehouseGoods();
                    warehouseGoods.setGoodsNo(oGoodsSku.getSkuCode());
                    warehouseGoods.setErpGoodsNo(oGoodsSku.getSkuCode());
                    warehouseGoods.setErpGoodsSign(oGoodsSku.getGoodsNum());
                    warehouseGoods.setGoodsName(oGoodsSku.getGoodsName());
                    warehouseGoods.setStandard(oGoodsSku.getSkuName());
                    warehouseGoods.setImageUrl(oGoodsSku.getColorImage());
                    warehouseGoods.setColor(oGoodsSku.getColorValue());
                    warehouseGoods.setSize(oGoodsSku.getSizeValue());
                    warehouseGoods.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
                    warehouseGoods.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
                    warehouseGoods.setMerchantId(inventory.getMerchantId());
                    warehouseGoods.setWarehouseId(inventory.getWarehouseId());
                    warehouseGoods.setWarehouseType("LOCAL");
                    warehouseGoods.setCreateTime(new Date());

                    warehouseGoodsService.save(warehouseGoods);
                    warehouseGoodsId = warehouseGoods.getId();
                }else {
                    warehouseGoodsId =  warehouseGoodsList.get(0).getId();
                }
            } else {
                //修改
                ErpWarehouseGoodsStock update = new ErpWarehouseGoodsStock();
                update.setId(inventoryList.get(0).getId());
                update.setUpdateBy(userName);
                update.setUpdateTime(new Date());
                update.setUsableNum(inventoryList.get(0).getUsableNum() + item.getIntoQuantity());
                warehouseGoodsStockService.updateById(update);
                goodsInventoryId = inventoryList.get(0).getId();
            }

            // 增加商品库存批次表
            ErpWarehouseGoodsStockBatch inventoryBatch = new ErpWarehouseGoodsStockBatch();
            inventoryBatch.setInventoryId(goodsInventoryId);
            inventoryBatch.setMerchantId(erpStockIn.getMerchantId());
            inventoryBatch.setBatchNum(DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()));
            inventoryBatch.setOriginQty(item.getIntoQuantity());
            inventoryBatch.setCurrentQty(item.getIntoQuantity());
            inventoryBatch.setPurPrice(stockInItem.getPurPrice());
            inventoryBatch.setPurId(0L);
            inventoryBatch.setPurItemId(0L);
//            inventoryBatch.setSkuId(stockInItem.getSkuId());
//            inventoryBatch.setSkuCode(stockInItem.getSkuCode());
            inventoryBatch.setGoodsId(warehouseGoodsId);
            inventoryBatch.setWarehouseId(request.getWarehouseId());
            inventoryBatch.setPositionId(item.getPositionId()==null?0:item.getPositionId());
            inventoryBatch.setPositionNum(item.getPositionNum());
            inventoryBatch.setCreateTime(new Date());
            inventoryBatch.setCreateBy(userName);
            warehouseGoodsStockBatchService.save(inventoryBatch);

            // 回写状态
            ErpStockInItem update = new ErpStockInItem();
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
        List<ErpStockInItem> itemList = inItemService.list(new LambdaQueryWrapper<ErpStockInItem>().eq(ErpStockInItem::getStockInId, erpStockIn.getId()).ne(ErpStockInItem::getStatus, 2));
        ErpStockIn sUpdate = new ErpStockIn();
        if (itemList.isEmpty()) {
            // 全部入库完成了
            sUpdate.setStatus(2);
        } else {
            // 部分入库
            sUpdate.setStatus(1);
        }
        sUpdate.setId(erpStockIn.getId());
        sUpdate.setStockInOperatorId(userId.toString());
        sUpdate.setStockInOperator(request.getStockInOperator());
        sUpdate.setStockInTime(new Date());
        sUpdate.setUpdateBy(userName);
        sUpdate.setUpdateTime(new Date());
        mapper.updateById(sUpdate);


        return ResultVo.success();
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> StockInItemIn(Long userId, String userName, StockInItemInRequest request) {
        if (request.getStockInId() == null) return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数stockInId");
        if (request.getStockInItemId() == null) return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数stockInItemId");
        if (request.getWarehouseId() == null) return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数warehouseId");
        if (request.getIntoQuantity() == null) return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数intoQuantity");

        ErpStockIn erpStockIn = mapper.selectById(request.getStockInId());
        if (erpStockIn == null) return ResultVo.error(ResultVoEnum.NotFound, "没有找到入库单");
        else if (erpStockIn.getStatus() == 2) {
            return ResultVo.error(ResultVoEnum.SystemException, "入库单状态不能入库");
        }

        // 查询明细
        ErpStockInItem stockInItem = inItemService.getById(request.getStockInItemId());
        if (stockInItem == null) {
            return ResultVo.error(ResultVoEnum.DataError, "数据错误！没有找到入库单明细");
        }
        String positionNum="";
        if(request.getPositionId()!=null&&request.getPositionId()>0) {
            ErpWarehousePosition erpWarehousePosition = warehousePositionMapper.selectById(request.getPositionId());
            if(erpWarehousePosition!=null) {
                positionNum = erpWarehousePosition.getNumber();
            }else return ResultVo.error("仓库仓位不存在");
        }

        // 开始入库
        OGoodsSku oGoodsSku = skuService.getById(stockInItem.getSkuId());
        if (oGoodsSku == null) return ResultVo.error("数据错误！没有找到入库商品SKU信息");
        
        // 增加仓库商品表数据 erp_warehouse_goods
        List<ErpWarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(ErpWarehouseGoods::getWarehouseId, request.getWarehouseId())
                .eq(ErpWarehouseGoods::getErpGoodsSkuId, Long.parseLong(oGoodsSku.getId())));
        Long warehouseGoodsId = null;
        if (warehouseGoodsList.isEmpty()) {
            // 新增
            ErpWarehouseGoods warehouseGoods = new ErpWarehouseGoods();
            warehouseGoods.setErpGoodsNo(oGoodsSku.getSkuCode());
            warehouseGoods.setErpGoodsSign(oGoodsSku.getGoodsNum());
            warehouseGoods.setGoodsName(oGoodsSku.getGoodsName());
            warehouseGoods.setStandard(oGoodsSku.getSkuName());
            warehouseGoods.setImageUrl(oGoodsSku.getColorImage());
            warehouseGoods.setColor(oGoodsSku.getColorValue());
            warehouseGoods.setSize(oGoodsSku.getSizeValue());
            warehouseGoods.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
            warehouseGoods.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
            warehouseGoods.setMerchantId(stockInItem.getMerchantId());
            warehouseGoods.setShopId(stockInItem.getShopId());
            warehouseGoods.setWarehouseId(request.getWarehouseId());
            warehouseGoods.setWarehouseType("LOCAL");
            warehouseGoods.setCreateTime(new Date());
            warehouseGoodsService.save(warehouseGoods);
            warehouseGoodsId = warehouseGoods.getId();
        } else {
            warehouseGoodsId = warehouseGoodsList.get(0).getId();
        }

        // 增加商品库存表 erp_warehouse_goods_stock
        List<ErpWarehouseGoodsStock> warehouseGoodsStockList = warehouseGoodsStockService.list(new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                .eq(ErpWarehouseGoodsStock::getWarehouseId, request.getWarehouseId())
                .eq(ErpWarehouseGoodsStock::getGoodsId, warehouseGoodsId)
                .eq(ErpWarehouseGoodsStock::getStockStatus, 1));
        Long goodsStockId = null;
        if (warehouseGoodsStockList.isEmpty()) {
            // 新增
            ErpWarehouse erpWarehouse = warehouseService.getById(request.getWarehouseId());
            if (erpWarehouse == null) return ResultVo.error("仓库不存在");
            
            ErpWarehouseGoodsStock inventory = new ErpWarehouseGoodsStock();
            inventory.setWarehouseId(request.getWarehouseId());
            inventory.setWarehouseNo(erpWarehouse.getWarehouseNo());
            inventory.setWarehouseName(erpWarehouse.getWarehouseName());
            inventory.setWarehouseType(erpWarehouse.getWarehouseType());
            inventory.setMerchantId(stockInItem.getMerchantId());
            inventory.setShopId(stockInItem.getShopId());
            inventory.setGoodsNo(oGoodsSku.getSkuCode());
            inventory.setGoodsId(warehouseGoodsId);
            inventory.setGoodsName(stockInItem.getGoodsName());
            inventory.setErpGoodsNo(oGoodsSku.getSkuCode());
            inventory.setErpGoodsSign(oGoodsSku.getGoodsNum());
            inventory.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
            inventory.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
            inventory.setSellerGoodsSign("");
            inventory.setStockStatus(1); // 1-良品
            inventory.setStockType(1); // 1-可销售
            inventory.setTotalNum(request.getIntoQuantity());
            inventory.setTotalNumValue(request.getIntoQuantity().doubleValue());
            inventory.setUsableNum(request.getIntoQuantity());
            inventory.setUsableNumValue(request.getIntoQuantity().doubleValue());
            inventory.setCreateBy(userName);
            inventory.setCreateTime(new Date());
            warehouseGoodsStockService.save(inventory);
            goodsStockId = inventory.getId();
        } else {
            // 修改
            ErpWarehouseGoodsStock update = new ErpWarehouseGoodsStock();
            update.setId(warehouseGoodsStockList.get(0).getId());
            update.setUpdateBy(userName);
            update.setUpdateTime(new Date());
            update.setTotalNum(warehouseGoodsStockList.get(0).getTotalNum() + request.getIntoQuantity());
            update.setTotalNumValue(update.getTotalNum().doubleValue());
            update.setUsableNum(warehouseGoodsStockList.get(0).getUsableNum() + request.getIntoQuantity());
            update.setUsableNumValue(update.getUsableNum().doubleValue());
            warehouseGoodsStockService.updateById(update);
            goodsStockId = warehouseGoodsStockList.get(0).getId();
        }

        // 增加商品库存批次表 erp_warehouse_goods_stock_batch
        ErpWarehouseGoodsStockBatch inventoryBatch = new ErpWarehouseGoodsStockBatch();
        inventoryBatch.setInventoryId(goodsStockId);
        inventoryBatch.setBatchNum(DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()));
        inventoryBatch.setOriginQty(request.getIntoQuantity());
        inventoryBatch.setCurrentQty(request.getIntoQuantity());
        inventoryBatch.setPurPrice(stockInItem.getPurPrice());
        inventoryBatch.setPurId(stockInItem.getSourceId());
        inventoryBatch.setPurItemId(stockInItem.getSourceItemId());
        inventoryBatch.setMerchantId(stockInItem.getMerchantId());
        inventoryBatch.setShopId(stockInItem.getShopId());
        inventoryBatch.setInventoryMode(stockInItem.getInventoryMode());

        inventoryBatch.setGoodsNo(oGoodsSku.getSkuCode());
        inventoryBatch.setGoodsId(warehouseGoodsId);
        inventoryBatch.setWarehouseId(request.getWarehouseId());
        inventoryBatch.setVendorId(request.getWarehouseId());
        inventoryBatch.setPositionId(request.getPositionId() == null ? 0L : request.getPositionId());
        inventoryBatch.setPositionNum(positionNum);
        inventoryBatch.setCreateTime(new Date());
        inventoryBatch.setCreateBy(userName);
        warehouseGoodsStockBatchService.save(inventoryBatch);

        // 回写状态
        ErpStockInItem update = new ErpStockInItem();
        update.setId(stockInItem.getId());
        Integer inQuantity = stockInItem.getInQuantity() + request.getIntoQuantity();
        update.setInQuantity(inQuantity);
        update.setStatus(inQuantity.intValue() >= stockInItem.getQuantity().intValue() ? 2 : 1); // 状态（0待入库1部分入库2已入库）
        update.setWarehouseId(request.getWarehouseId());
        update.setPositionId(request.getPositionId());
        String oldPosition = stockInItem.getPositionNum();
        if(org.springframework.util.StringUtils.isEmpty(positionNum)){
            positionNum = "空";
        }
        String newPosition = "";
        if(org.springframework.util.StringUtils.isEmpty(oldPosition)){
            oldPosition = "";
            newPosition = positionNum + ":" + request.getIntoQuantity();
        }else{
            newPosition = oldPosition + "," + positionNum + ":" + request.getIntoQuantity();
        }

        update.setPositionNum(newPosition);
        update.setUpdateBy(userName);
        update.setUpdateTime(new Date());
        inItemService.updateById(update);

        // 查询入库表单是否入库完成
        List<ErpStockInItem> itemList = inItemService.list(new LambdaQueryWrapper<ErpStockInItem>().eq(ErpStockInItem::getStockInId, erpStockIn.getId()).ne(ErpStockInItem::getStatus, 2));
        ErpStockIn sUpdate = new ErpStockIn();
        if (itemList.isEmpty()) {
            // 全部入库完成了
            sUpdate.setStatus(2);
        } else {
            // 部分入库
            sUpdate.setStatus(1);
        }
        sUpdate.setId(erpStockIn.getId());
        sUpdate.setStockInOperatorId(userId.toString());
        sUpdate.setStockInTime(new Date());
        sUpdate.setUpdateBy(userName);
        sUpdate.setUpdateTime(new Date());
        mapper.updateById(sUpdate);

        return ResultVo.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> localStockIn(StockInLocalRequest request, Long userId, String userName) {
        if(request.getId()==null) return ResultVo.error("入库单id不能为空");
        if(request.getItems()==null||request.getItems().size()==0) return ResultVo.error("入库商品不能为空");

        ErpStockIn erpStockIn = mapper.selectById(request.getId());
        if (erpStockIn == null) return ResultVo.error(ResultVoEnum.NotFound, "没有找到入库单");
        else if (erpStockIn.getStatus() == 2) {
            return ResultVo.error(ResultVoEnum.SystemException, "入库单状态不能操作");
        }

        for (StockInLocalRequest.Item item : request.getItems()) {
            // 查询明细
            ErpStockInItem stockInItem = inItemService.getById(item.getItemId());
            if (stockInItem == null) {
                return ResultVo.error(ResultVoEnum.DataError, "数据错误！没有找到入库单明细");
            }else if (stockInItem.getStatus() == 2) {
                return ResultVo.error("该商品已全部入库");
            }
            if(stockInItem.getInventoryMode()==0 && item.getQuantity()<=0) return ResultVo.error("没有入库数量");
            else if(stockInItem.getInventoryMode()==1){
                // 一物一码
                if(item.getBatchList()==null||item.getBatchList().isEmpty()) return ResultVo.error("没有录入批次信息");
            }

            // 开始入库
            OGoodsSku oGoodsSku = skuService.getById(stockInItem.getSkuId());
            if (oGoodsSku == null) return ResultVo.error("数据错误！没有找到入库商品SKU信息");
            
            // 计算入库数量
            Integer intoQty = null;
            if(stockInItem.getInventoryMode()==0) intoQty = item.getQuantity();
            else if(stockInItem.getInventoryMode()==1) intoQty = item.getBatchList().size();
            
            // 增加仓库商品表数据 erp_warehouse_goods
            List<ErpWarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(new LambdaQueryWrapper<ErpWarehouseGoods>()
                    .eq(ErpWarehouseGoods::getWarehouseId, erpStockIn.getWarehouseId())
                    .eq(ErpWarehouseGoods::getErpGoodsSkuId, Long.parseLong(oGoodsSku.getId())));
            Long warehouseGoodsId = null;
            if (warehouseGoodsList.isEmpty()) {
                // 新增
                ErpWarehouseGoods warehouseGoods = new ErpWarehouseGoods();
                warehouseGoods.setGoodsNo(oGoodsSku.getSkuCode());
                warehouseGoods.setErpGoodsNo(oGoodsSku.getId());
                warehouseGoods.setErpGoodsSign(oGoodsSku.getGoodsId());
                warehouseGoods.setGoodsName(oGoodsSku.getGoodsName());
                warehouseGoods.setStandard(oGoodsSku.getSkuName());
                warehouseGoods.setImageUrl(oGoodsSku.getColorImage());
                warehouseGoods.setColor(oGoodsSku.getColorValue());
                warehouseGoods.setSize(oGoodsSku.getSizeValue());
                warehouseGoods.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
                warehouseGoods.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
                warehouseGoods.setMerchantId(stockInItem.getMerchantId());
                warehouseGoods.setShopId(stockInItem.getShopId());
                warehouseGoods.setWarehouseId(erpStockIn.getWarehouseId());
                warehouseGoods.setWarehouseType("LOCAL");
                warehouseGoods.setCreateTime(new Date());
                warehouseGoodsService.save(warehouseGoods);
                warehouseGoodsId = warehouseGoods.getId();
            } else {
                warehouseGoodsId = warehouseGoodsList.get(0).getId();
            }

            // 增加商品库存表 erp_warehouse_goods_stock
            List<ErpWarehouseGoodsStock> warehouseGoodsStockList = warehouseGoodsStockService.list(new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                    .eq(ErpWarehouseGoodsStock::getWarehouseId, erpStockIn.getWarehouseId())
                    .eq(ErpWarehouseGoodsStock::getGoodsId, warehouseGoodsId)
                    .eq(ErpWarehouseGoodsStock::getStockStatus, 1));
            Long goodsStockId = null;
            if (warehouseGoodsStockList.isEmpty()) {
                // 新增
                ErpWarehouse erpWarehouse = warehouseService.getById(erpStockIn.getWarehouseId());
                if (erpWarehouse == null) return ResultVo.error("仓库不存在");
                
                ErpWarehouseGoodsStock inventory = new ErpWarehouseGoodsStock();
                inventory.setWarehouseId(erpStockIn.getWarehouseId());
                inventory.setWarehouseNo(erpWarehouse.getWarehouseNo());
                inventory.setWarehouseName(erpWarehouse.getWarehouseName());
                inventory.setWarehouseType(erpWarehouse.getWarehouseType());
                inventory.setMerchantId(stockInItem.getMerchantId());
                inventory.setShopId(stockInItem.getShopId());
                inventory.setGoodsNo(oGoodsSku.getSkuCode());
                inventory.setGoodsId(warehouseGoodsId);
                inventory.setGoodsName(stockInItem.getGoodsName());
                inventory.setErpGoodsNo(oGoodsSku.getSkuCode());
                inventory.setErpGoodsSign(oGoodsSku.getGoodsNum());
                inventory.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
                inventory.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
                inventory.setSellerGoodsSign("");
                inventory.setStockStatus(1); // 1-良品
                inventory.setStockType(1); // 1-可销售
                inventory.setTotalNum(intoQty);
                inventory.setTotalNumValue(intoQty.doubleValue());
                inventory.setUsableNum(intoQty);
                inventory.setUsableNumValue(intoQty.doubleValue());
                inventory.setCreateBy(userName);
                inventory.setCreateTime(new Date());
                warehouseGoodsStockService.save(inventory);
                goodsStockId = inventory.getId();
            } else {
                // 修改
                ErpWarehouseGoodsStock update = new ErpWarehouseGoodsStock();
                update.setId(warehouseGoodsStockList.get(0).getId());
                update.setUpdateBy(userName);
                update.setUpdateTime(new Date());
                update.setTotalNum(warehouseGoodsStockList.get(0).getTotalNum() + intoQty);
                update.setTotalNumValue(update.getTotalNum().doubleValue());
                update.setUsableNum(warehouseGoodsStockList.get(0).getUsableNum() + intoQty);
                update.setUsableNumValue(update.getUsableNum().doubleValue());
                warehouseGoodsStockService.updateById(update);
                goodsStockId = warehouseGoodsStockList.get(0).getId();
            }

            // 增加商品库存批次表
            if(stockInItem.getInventoryMode()==0) {
                // 普通模式
                ErpWarehouseGoodsStockBatch inventoryBatch = new ErpWarehouseGoodsStockBatch();
                inventoryBatch.setInventoryId(goodsStockId);
                inventoryBatch.setBatchNum(DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()));
                inventoryBatch.setOriginQty(intoQty);
                inventoryBatch.setCurrentQty(intoQty);

                inventoryBatch.setPurPrice(stockInItem.getPurPrice());
                inventoryBatch.setPurId(stockInItem.getSourceId());
                inventoryBatch.setPurItemId(stockInItem.getSourceItemId());
                inventoryBatch.setMerchantId(stockInItem.getMerchantId());
                inventoryBatch.setShopId(stockInItem.getShopId());
                inventoryBatch.setInventoryMode(stockInItem.getInventoryMode());

                inventoryBatch.setGoodsNo(oGoodsSku.getSkuCode());
                inventoryBatch.setGoodsId(warehouseGoodsId);
                inventoryBatch.setWarehouseId(erpStockIn.getWarehouseId());
                inventoryBatch.setVendorId(erpStockIn.getWarehouseId());
                inventoryBatch.setPositionId(0L);
                inventoryBatch.setPositionNum("");
                inventoryBatch.setCreateTime(new Date());
                inventoryBatch.setCreateBy(userName);
                warehouseGoodsStockBatchService.save(inventoryBatch);
            } else if(stockInItem.getInventoryMode()==1) {
                // 一物一码
                for (var batch : item.getBatchList()) {
                    ErpWarehouseGoodsStockBatch inventoryBatch = new ErpWarehouseGoodsStockBatch();
                    inventoryBatch.setInventoryId(goodsStockId);
                    inventoryBatch.setBatchNum(batch.getBarcode());
                    inventoryBatch.setOriginQty(1);
                    inventoryBatch.setCurrentQty(1);
                    inventoryBatch.setPurPrice(stockInItem.getPurPrice());
                    inventoryBatch.setPurId(stockInItem.getSourceId());
                    inventoryBatch.setPurItemId(stockInItem.getSourceItemId());
                    inventoryBatch.setMerchantId(stockInItem.getMerchantId());
                    inventoryBatch.setShopId(stockInItem.getShopId());
                    inventoryBatch.setInventoryMode(stockInItem.getInventoryMode());

                    inventoryBatch.setActualGoldWeight(batch.getGoldWeight());
                    inventoryBatch.setActualSilverWeight(batch.getSilverWeight());
                    inventoryBatch.setLaborCost(batch.getLaborCost());
                    inventoryBatch.setCertificateNo(batch.getCertificateNo());

                    inventoryBatch.setGoodsNo(oGoodsSku.getSkuCode());
                    inventoryBatch.setGoodsId(warehouseGoodsId);
                    inventoryBatch.setWarehouseId(erpStockIn.getWarehouseId());
                    inventoryBatch.setVendorId(erpStockIn.getWarehouseId());
                    inventoryBatch.setPositionId(0L);
                    inventoryBatch.setPositionNum("");
                    inventoryBatch.setCreateTime(new Date());
                    inventoryBatch.setCreateBy(userName);
                    warehouseGoodsStockBatchService.save(inventoryBatch);

                    //OGoodsInventoryBatch inventoryBatch = new OGoodsInventoryBatch();
                    //                    inventoryBatch.setInventoryId(Long.parseLong(goodsInventoryId));
                    //                    inventoryBatch.setMerchantId(erpStockIn.getMerchantId());
                    //                    inventoryBatch.setShopId(erpStockIn.getShopId());
                    //                    inventoryBatch.setBatchNum(batch.getBarcode());
                    //                    inventoryBatch.setBarcode(batch.getBarcode());
                    //                    inventoryBatch.setOriginQty(1);
                    //                    inventoryBatch.setCurrentQty(1);
                    //                    inventoryBatch.setPurPrice(stockInItem.getPurPrice());
                    //                    inventoryBatch.setPurId(stockInItem.getSourceId());
                    //                    inventoryBatch.setPurItemId(stockInItem.getSourceItemId());
                    //                    inventoryBatch.setSkuId(stockInItem.getSkuId());
                    //                    inventoryBatch.setSkuCode(stockInItem.getSkuCode());
                    //                    inventoryBatch.setGoodsId(stockInItem.getGoodsId());
                    //                    inventoryBatch.setInventoryMode(stockInItem.getInventoryMode());
                    //                    inventoryBatch.setWarehouseId(erpStockIn.getWarehouseId());
                    //                    inventoryBatch.setPositionId(0L);
                    //                    inventoryBatch.setActualGoldWeight(batch.getGoldWeight());
                    //                    inventoryBatch.setActualSilverWeight(batch.getSilverWeight());
                    //                    inventoryBatch.setLaborCost(batch.getLaborCost());
                    //                    inventoryBatch.setCertificateNo(batch.getCertificateNo());
                    ////                    inventoryBatch.setPositionNum(positionNum);
                    ////                    inventoryBatch.setProductionDate(request.getProductionDate());
                    ////                    inventoryBatch.setPeriod(request.getPeriod());
                    //                    inventoryBatch.setCreateTime(new Date());
                    //                    inventoryBatch.setCreateBy(userName);
                    //                    inventoryBatchService.save(inventoryBatch);
                }
            }

            // 回写状态
            ErpStockInItem update = new ErpStockInItem();
            update.setId(stockInItem.getId());
            Integer inQuantity = stockInItem.getInQuantity() + intoQty;
            update.setInQuantity(inQuantity);
            update.setStatus(inQuantity.intValue() >= stockInItem.getQuantity().intValue() ? 2 : 1); // 状态（0待入库1部分入库2已入库）
            update.setUpdateBy(userName);
            update.setUpdateTime(new Date());
            inItemService.updateById(update);
        }
        // 查询入库表单是否入库完成
        List<ErpStockInItem> itemList = inItemService.list(new LambdaQueryWrapper<ErpStockInItem>().eq(ErpStockInItem::getStockInId, erpStockIn.getId()).ne(ErpStockInItem::getStatus, 2));
        ErpStockIn sUpdate = new ErpStockIn();
        if (itemList.isEmpty()) {
            // 全部入库完成了
            sUpdate.setStatus(2);
        } else {
            // 部分入库
            sUpdate.setStatus(1);
        }
        sUpdate.setId(erpStockIn.getId());
        sUpdate.setStockInOperatorId(userId.toString());
        sUpdate.setStockInTime(new Date());
        sUpdate.setUpdateBy(userName);
        sUpdate.setUpdateTime(new Date());
        mapper.updateById(sUpdate);

        return ResultVo.success();
    }

    @Override
    public ErpStockIn getDetailAndItemById(Long id) {
        ErpStockIn erpStockIn = mapper.selectById(id);
        if(erpStockIn !=null){
            erpStockIn.setItemList(inItemService.list(new LambdaQueryWrapper<ErpStockInItem>().eq(ErpStockInItem::getStockInId,id)));
            return erpStockIn;
        }else
            return null;
    }

    /**
     * 商户采购入库到云仓入库单确认并生成云仓仓库入库单
     * @param stockInId 商户入库单ID（erp_stock_in）
     * @param warehouseId 云仓仓库ID（erp_warehouse）
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> merchantStockInConfirmAndCreateCloudWarehouseStockIn(Long stockInId,Long warehouseId) {
        ErpStockIn stockIn = mapper.selectById(stockInId);
        if(stockIn==null) return ResultVo.error("入库单不存在");
        else if(stockIn.getStatus().intValue()!=0) return ResultVo.error("入库单已经操作过了");

        ErpWarehouse erpWarehouse = warehouseService.getById(warehouseId);
        if(erpWarehouse==null) return ResultVo.error("仓库不存在");
        if(erpWarehouse.getType()!=2) return ResultVo.error("仓库不是云仓，无法操作！");
        if(!erpWarehouse.getWarehouseType().equals(EnumWarehouseType.CLOUD.getType())) return ResultVo.error("仓库不是系统云仓，无法操作！");
        List<ErpStockInItem> stockInItemList = inItemService.list(new LambdaQueryWrapper<ErpStockInItem>().eq(ErpStockInItem::getStockInId, stockInId));
        if(stockInItemList.isEmpty()) return ResultVo.error("数据错误！未找到入库单item");

        // 开始新增云仓仓库入库单
        ErpWarehouseStockIn vendorStockIn = new ErpWarehouseStockIn();
        vendorStockIn.setStockInNum(stockIn.getStockInNum());
        vendorStockIn.setStockInType(stockIn.getStockInType());
        vendorStockIn.setSourceType(1);//来源（0自己入库1商户申请入库）
        vendorStockIn.setSourceNo(stockIn.getStockInNum());
        vendorStockIn.setGoodsUnit(stockIn.getSourceGoodsUnit());
        vendorStockIn.setGoodsSkuUnit(stockIn.getSourceSpecUnit());
        vendorStockIn.setTotal(stockIn.getSourceSpecUnitTotal());
        vendorStockIn.setRemark(stockIn.getRemark());
        vendorStockIn.setApplyId(stockIn.getStockInOperatorId()==null?stockIn.getMerchantId():Long.parseLong(stockIn.getStockInOperatorId()));
        vendorStockIn.setApplyMan(stockIn.getStockInOperator());
        vendorStockIn.setApplyMobile("");
        vendorStockIn.setStatus(1);//状态（0申请中1待入库2已入库）
        vendorStockIn.setCreateBy("仓库审核商户入库单");
        vendorStockIn.setCreateTime(new Date());
        vendorStockIn.setVendorId(warehouseId);
        vendorStockIn.setVendorName(erpWarehouse.getWarehouseName());
        vendorStockIn.setMerchantId(stockIn.getMerchantId());
        vendorStockIn.setMerchantName("");
        vendorStockInService.save(vendorStockIn);
        // 开始新增云仓仓库入库明细
        for(var item :stockInItemList){
            ErpWarehouseStockInItem inItem = new ErpWarehouseStockInItem();
            Long warehouseGoodsId = 0L;
            // 查询skuid或skucode是否存在仓库商品表
            List<ErpWarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(
                    new LambdaQueryWrapper<ErpWarehouseGoods>()
                            .eq(ErpWarehouseGoods::getWarehouseId,warehouseId)
                            .and(x->x.eq(ErpWarehouseGoods::getErpGoodsNo, item.getSkuCode()).or().eq(ErpWarehouseGoods::getErpGoodsNo, item.getSkuId())))
                            ;
            if(warehouseGoodsList.isEmpty()){
                // 仓库商品表中不存在，新增
                ErpWarehouseGoods warehouseGoods = new ErpWarehouseGoods();
                warehouseGoods.setGoodsNo("WH"+erpWarehouse.getId()+item.getSkuCode());
                warehouseGoods.setErpGoodsNo(item.getSkuCode());
                warehouseGoods.setErpGoodsSign(item.getSkuId());
                warehouseGoods.setGoodsName(item.getGoodsName());
                warehouseGoods.setImageUrl(item.getGoodsImage());
                warehouseGoods.setStandard(item.getSkuName());
                warehouseGoods.setErpGoodsId(Long.parseLong(item.getGoodsId()));
                warehouseGoods.setErpGoodsSkuId(Long.parseLong(item.getSkuId()));
                warehouseGoods.setCreateBy("审核商户入库单自动添加商品");
                warehouseGoods.setCreateTime(new Date());
                warehouseGoods.setWarehouseNo(erpWarehouse.getWarehouseNo());
                warehouseGoods.setWarehouseId(erpWarehouse.getId());
                warehouseGoods.setWarehouseType(erpWarehouse.getWarehouseType());
                warehouseGoods.setMerchantId(stockIn.getMerchantId());
                warehouseGoodsService.save(warehouseGoods);
                warehouseGoodsId = warehouseGoods.getId();
            }else{
                warehouseGoodsId = warehouseGoodsList.get(0).getId();
            }

            inItem.setStockInId(vendorStockIn.getId());
            inItem.setGoodsId(warehouseGoodsId);
            inItem.setGoodsName(item.getGoodsName());
            inItem.setGoodsImage(item.getGoodsImage());
            inItem.setSkuName(item.getSkuName());
            inItem.setGoodsNo("WH"+erpWarehouse.getId()+item.getSkuCode());
            inItem.setQuantity(item.getQuantity());
            inItem.setInQuantity(0);
            inItem.setStatus(0);
            inItem.setCreateBy("审核商户入库单");
            inItem.setCreateTime(new Date());
            inItem.setVendorId(vendorStockIn.getVendorId());
            inItem.setMerchantId(vendorStockIn.getMerchantId());
            vendorStockInItemService.save(inItem);
        }

        // 更新自己的状态
        ErpStockIn erpStockInUpdate =new ErpStockIn();
        erpStockInUpdate.setId(stockIn.getId());
        erpStockInUpdate.setUpdateBy("仓库审核");
        erpStockInUpdate.setUpdateTime(new Date());
        erpStockInUpdate.setStatus(1);//审核通过
        mapper.updateById(erpStockInUpdate);

        return ResultVo.success(vendorStockIn.getId());
    }
}



