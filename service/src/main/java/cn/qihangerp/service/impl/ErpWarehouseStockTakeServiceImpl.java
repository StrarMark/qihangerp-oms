package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.enums.EnumStockInType;
import cn.qihangerp.enums.EnumStockOutType;
import cn.qihangerp.mapper.*;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.WarehouseStockTakeAddItemRequest;
import cn.qihangerp.model.request.WarehouseStockTakeCreateRequest;
import cn.qihangerp.model.request.WarehouseStockTakeSaveItemRequest;
import cn.qihangerp.model.request.WarehouseStockTakeSaveRequest;
import cn.qihangerp.service.ErpWarehouseGoodsService;
import cn.qihangerp.service.ErpWarehouseGoodsStockService;
import cn.qihangerp.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.service.ErpWarehouseStockTakeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 1
* @description 针对表【erp_warehouse_stock_take(仓库盘点表)】的数据库操作Service实现
* @createDate 2025-10-15 21:54:31
*/
@AllArgsConstructor
@Service
public class ErpWarehouseStockTakeServiceImpl extends ServiceImpl<ErpWarehouseStockTakeMapper, ErpWarehouseStockTake>
    implements ErpWarehouseStockTakeService{
    private final ErpWarehouseMapper erpWarehouseMapper;
    private final ErpWarehouseStockTakeItemMapper stockTakeItemMapper;
    private final ErpWarehouseGoodsService goodsService;
    private final ErpWarehouseGoodsStockService goodsStockService;
    private final ErpWarehouseStockInMapper stockInMapper;
    private final ErpWarehouseStockInItemMapper stockInItemMapper;
    private final ErpWarehouseStockOutMapper stockOutMapper;
    private final ErpWarehouseStockOutItemMapper stockOutItemMapper;
    @Override
    public PageResult<ErpWarehouseStockTake> queryPageList(ErpWarehouseStockTake bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseStockTake> queryWrapper = new LambdaQueryWrapper<ErpWarehouseStockTake>()
                .eq(bo.getWarehouseId()!=null, ErpWarehouseStockTake::getWarehouseId,bo.getWarehouseId())
                .eq( bo.getStatus()!=null, ErpWarehouseStockTake::getStatus, bo.getStatus())
                .eq(StringUtils.hasText(bo.getStockTakeDate()), ErpWarehouseStockTake::getStockTakeDate, bo.getStockTakeDate())
                .eq(bo.getMerchantId()!=null, ErpWarehouseStockTake::getMerchantId, bo.getMerchantId())

                ;

        Page<ErpWarehouseStockTake> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ErpWarehouseStockTake getDetailAndItemById(Long id) {
        ErpWarehouseStockTake erpStockIn = this.baseMapper.selectById(id);
        if(erpStockIn !=null){
            erpStockIn.setItemList(stockTakeItemMapper.selectList(new LambdaQueryWrapper<cn.qihangerp.model.entity.ErpWarehouseStockTakeItem>()
                    .eq(cn.qihangerp.model.entity.ErpWarehouseStockTakeItem::getStockTakeId,id)));
            return erpStockIn;
        }else
            return null;
    }

    @Override
    public ResultVo createEntry(Long userId, String userName, WarehouseStockTakeCreateRequest request) {
        ErpWarehouse erpWarehouse = erpWarehouseMapper.selectById(request.getWarehouseId());
        if(erpWarehouse == null){
            return ResultVo.error("没有找到仓库");
        }
        ErpWarehouseStockTake stockTake = new ErpWarehouseStockTake();
        stockTake.setStockTakeDate(DateUtils.getDate());
        stockTake.setStockTakeMan(userName);
        stockTake.setSkuUnit(0);
        stockTake.setTotalQuantity(0);
        stockTake.setResultQuantity(0);
        stockTake.setRemark(request.getRemark());
        stockTake.setWarehouseId(request.getWarehouseId());
        stockTake.setWarehouseName(erpWarehouse.getWarehouseName());
        stockTake.setMerchantId(erpWarehouse.getMerchantId());
        if(request.getMerchantId()==0) {
            stockTake.setMerchantName("总部");
        }
        stockTake.setStatus(0);
        stockTake.setCreateTime(new Date());
        stockTake.setCreateBy(userName);
        this.baseMapper.insert(stockTake);
        return ResultVo.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<ErpWarehouseStockTakeItem> addTakeGoods(WarehouseStockTakeSaveItemRequest request,String userName) {
        ErpWarehouseStockTake stockTake = this.baseMapper.selectById(request.getId());
        if(stockTake == null){
            return ResultVo.error("数据不存在");
        }else if(stockTake.getStatus()!=0 && stockTake.getStatus()!=1){
            return ResultVo.error("已完成的不允许操作");
        }
        List<ErpWarehouseStockTakeItem> erpWarehouseStockTakeItems = stockTakeItemMapper.selectList(new LambdaQueryWrapper<ErpWarehouseStockTakeItem>()
                .eq(ErpWarehouseStockTakeItem::getStockTakeId, stockTake.getId())
                .eq(ErpWarehouseStockTakeItem::getWarehouseId, stockTake.getWarehouseId())
                .eq(ErpWarehouseStockTakeItem::getGoodsId, request.getGoodsId()));
        if(erpWarehouseStockTakeItems!=null&&erpWarehouseStockTakeItems.size()>0){
            return ResultVo.error("商品已存在");
        }
        // 查商品
        ErpWarehouseGoods warehouseGoods = goodsService.getById(request.getGoodsId());
        if (warehouseGoods == null) {
            return ResultVo.error("商品不存在！" + request.getGoodsId());
        }

        // 查库存
        Integer goodsStockQty = goodsStockService.getGoodsStockQty(request.getGoodsId());

        // 添加记录
        ErpWarehouseStockTakeItem stockTakeItem = new ErpWarehouseStockTakeItem();
        stockTakeItem.setStockTakeId(stockTake.getId());
        stockTakeItem.setWarehouseId(stockTake.getWarehouseId());
        stockTakeItem.setMerchantId(stockTake.getMerchantId());
        stockTakeItem.setGoodsId(request.getGoodsId());
        stockTakeItem.setGoodsNo(warehouseGoods.getGoodsNo());
        stockTakeItem.setGoodsName(warehouseGoods.getGoodsName());
        stockTakeItem.setGoodsImage(warehouseGoods.getImageUrl());
        stockTakeItem.setSkuName(warehouseGoods.getStandard());
        stockTakeItem.setQuantity(goodsStockQty);
        stockTakeItem.setTakeQuantity(0);
        //盘点结果（0未出结果10盘平20盘盈30盘亏）
        stockTakeItem.setResult(0);
        stockTakeItem.setStatus(0);
        stockTakeItem.setCreateTime(new Date());
        stockTakeItem.setCreateBy(userName);
        stockTakeItemMapper.insert(stockTakeItem);

        //更新盘点主表数据
        // 更新自己
        ErpWarehouseStockTake update = new ErpWarehouseStockTake();
        update.setId(stockTake.getId());
        update.setStatus(1);
        if(stockTake.getFirstTakeTime()==null){
            update.setFirstTakeTime(new Date());
        }
        update.setSkuUnit(stockTake.getSkuUnit()+1);
        update.setUpdateTime(new Date());
        update.setUpdateBy(userName);
        this.baseMapper.updateById(update);

        return ResultVo.success(stockTakeItem);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> deleteTakeGoods(Long takeItemId) {
        ErpWarehouseStockTakeItem item = stockTakeItemMapper.selectById(takeItemId);
        if(item!=null){
            ErpWarehouseStockTake stockTake = this.baseMapper.selectById(item.getStockTakeId());
            if(stockTake == null){
                return ResultVo.error("数据不存在");
            }else if(stockTake.getStatus()!=0 && stockTake.getStatus()!=1){
                return ResultVo.error("已完成的不允许操作");
            }
            stockTakeItemMapper.deleteById(takeItemId);
            // 更新自己
            ErpWarehouseStockTake update = new ErpWarehouseStockTake();
            update.setId(item.getStockTakeId());
            if (stockTake.getSkuUnit() > 0) {
                update.setSkuUnit(stockTake.getSkuUnit()-1);
            }

            update.setUpdateTime(new Date());
            this.baseMapper.updateById(update);
        }
        return ResultVo.success();
    }

    /**
     * 盘点保存
     * @param request
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> saveTake(WarehouseStockTakeSaveRequest request, String userName) {
        ErpWarehouseStockTake stockTake = this.baseMapper.selectById(request.getId());
        if(stockTake == null){
            return ResultVo.error("数据不存在");
        }else if(stockTake.getStatus()!=0 && stockTake.getStatus()!=1){
            return ResultVo.error("已完成的不允许操作");
        }
        if(request.getItemList()==null||request.getItemList().size()==0){
            return ResultVo.error("没有数据");
        }
        for (var item : request.getItemList()) {
            if(item.getTakeQuantity()!=null&&item.getTakeQuantity()>=0){
                ErpWarehouseStockTakeItem itemUpdate = new ErpWarehouseStockTakeItem();
                itemUpdate.setId(item.getId());
                itemUpdate.setTakeQuantity(item.getTakeQuantity());
                //盘点结果（0未出结果10盘平20盘盈30盘亏）
                int result = 0;
                if (item.getQuantity() == item.getTakeQuantity()) {
                    result = 10;
                } else if (item.getQuantity() < item.getTakeQuantity()) {
                    result = 20;
                } else if (item.getQuantity() > item.getTakeQuantity()) {
                    result = 30;
                }
                itemUpdate.setResult(result);
                itemUpdate.setStatus(2);
                itemUpdate.setUpdateTime(new Date());
                itemUpdate.setUpdateBy(userName);
                stockTakeItemMapper.updateById(itemUpdate);
            }
        }
        return ResultVo.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo addItem(Long userId, String userName, WarehouseStockTakeAddItemRequest request) {
        ErpWarehouseStockTake stockTake = this.baseMapper.selectById(request.getId());
        if(stockTake == null){
            return ResultVo.error("数据不存在");
        }else if(stockTake.getStatus()!=0 && stockTake.getStatus()!=1){
            return ResultVo.error("已完成的不允许操作");
        }
        if(request.getItemList()==null||request.getItemList().size()==0){
            return ResultVo.error("没有数据");
        }
        int totalStock = 0;
        int resultTotal = 0;
        for (var item : request.getItemList()) {
            List<cn.qihangerp.model.entity.ErpWarehouseStockTakeItem> erpWarehouseStockTakeItems = stockTakeItemMapper.selectList(new LambdaQueryWrapper<cn.qihangerp.model.entity.ErpWarehouseStockTakeItem>()
                    .eq(cn.qihangerp.model.entity.ErpWarehouseStockTakeItem::getStockTakeId, stockTake.getId())
                    .eq(cn.qihangerp.model.entity.ErpWarehouseStockTakeItem::getWarehouseId, stockTake.getWarehouseId())
                    .eq(cn.qihangerp.model.entity.ErpWarehouseStockTakeItem::getGoodsId, item.getId()));
            if(erpWarehouseStockTakeItems!=null && erpWarehouseStockTakeItems.size()>0){
                // 查库存
                Integer goodsStockQty = goodsStockService.getGoodsStockQty(item.getId());
                //更新
                cn.qihangerp.model.entity.ErpWarehouseStockTakeItem stockTakeItemUpdate = new cn.qihangerp.model.entity.ErpWarehouseStockTakeItem();
                stockTakeItemUpdate.setId(erpWarehouseStockTakeItems.get(0).getId());

                stockTakeItemUpdate.setQuantity(goodsStockQty);
                stockTakeItemUpdate.setTakeQuantity(item.getQuantity());
                //盘点结果（0未出结果10盘平20盘盈30盘亏）
                int result = 0;
                if (goodsStockQty == item.getQuantity()) {
                    result = 10;
                } else if (goodsStockQty < item.getQuantity()) {
                    result = 20;
                } else if (goodsStockQty > item.getQuantity()) {
                    result = 30;
                }
                stockTakeItemUpdate.setResult(result);
                stockTakeItemUpdate.setStatus(2);
                stockTakeItemUpdate.setUpdateTime(new Date());
                stockTakeItemUpdate.setUpdateBy(userName);
                stockTakeItemMapper.updateById(stockTakeItemUpdate);
            }else {
                // 新增
                // 查商品
                ErpWarehouseGoods warehouseGoods = goodsService.getById(item.getId());
                if (warehouseGoods == null) {
                    return ResultVo.error("商品不存在！" + item.getId());
                }
                if (item.getQuantity() == null) item.setQuantity(0);
                // 查库存
                Integer goodsStockQty = goodsStockService.getGoodsStockQty(item.getId());

                cn.qihangerp.model.entity.ErpWarehouseStockTakeItem stockTakeItem = new cn.qihangerp.model.entity.ErpWarehouseStockTakeItem();
                stockTakeItem.setStockTakeId(stockTake.getId());
                stockTakeItem.setWarehouseId(stockTake.getWarehouseId());
                stockTakeItem.setMerchantId(stockTake.getMerchantId());
                stockTakeItem.setGoodsId(item.getId());
                stockTakeItem.setGoodsNo(warehouseGoods.getGoodsNo());
                stockTakeItem.setGoodsName(warehouseGoods.getGoodsName());
                stockTakeItem.setGoodsImage(warehouseGoods.getImageUrl());
                stockTakeItem.setSkuName(warehouseGoods.getStandard());
                stockTakeItem.setQuantity(goodsStockQty);
                stockTakeItem.setTakeQuantity(item.getQuantity());
                //盘点结果（0未出结果10盘平20盘盈30盘亏）
                int result = 0;
                if (goodsStockQty == item.getQuantity()) {
                    result = 10;
                } else if (goodsStockQty < item.getQuantity()) {
                    result = 20;
                } else if (goodsStockQty > item.getQuantity()) {
                    result = 30;
                }
                stockTakeItem.setResult(result);
                stockTakeItem.setStatus(2);
                stockTakeItem.setCreateTime(new Date());
                stockTakeItem.setCreateBy(userName);
                stockTakeItemMapper.insert(stockTakeItem);
            }
        }

        // 更新自己
        ErpWarehouseStockTake update = new ErpWarehouseStockTake();
        update.setId(stockTake.getId());
        update.setStatus(1);
        if(stockTake.getFirstTakeTime()==null){
            update.setFirstTakeTime(new Date());
        }
        update.setUpdateTime(new Date());
        update.setUpdateBy(userName);
        this.baseMapper.updateById(update);
        return ResultVo.success();
    }

    /**
     * 盘点完成
     * 1、生成盘盈入库单，生成盘亏出库单
     * 2、修改状态
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo complete(Long id,Long userId,String userName) {
        ErpWarehouseStockTake stockTake = this.baseMapper.selectById(id);
        if(stockTake == null){
            return ResultVo.error("数据不存在");
        }else if(stockTake.getStatus()!=0 && stockTake.getStatus()!=1){
            return ResultVo.error("已完成的不允许操作");
        }
        List<cn.qihangerp.model.entity.ErpWarehouseStockTakeItem> items = stockTakeItemMapper.selectList(new LambdaQueryWrapper<cn.qihangerp.model.entity.ErpWarehouseStockTakeItem>().eq(cn.qihangerp.model.entity.ErpWarehouseStockTakeItem::getStockTakeId, id));
        if(items.isEmpty()) return ResultVo.error("没有找到盘点数据");

        // 盘盈list
        List<cn.qihangerp.model.entity.ErpWarehouseStockTakeItem> panying = items.stream()
                .filter(item -> item.getResult() ==20)
                .collect(Collectors.toList());
        if(!panying.isEmpty()){
            // 添加盘盈入库单
            //添加主表信息
            ErpWarehouseStockIn stockIn = new ErpWarehouseStockIn();
            stockIn.setVendorId(stockTake.getWarehouseId());
            stockIn.setMerchantId(stockTake.getMerchantId());
            stockIn.setStockInNum("PYRK-" + DateUtils.parseDateToStr("yyyyMMdd", new Date()) + "-" + System.currentTimeMillis() / 1000);
            stockIn.setStockInType(EnumStockInType.TAKE_STOCK_IN.getIndex());
            stockIn.setSourceType(0);//来源（0自己入库1商户申请入库）
            stockIn.setApplyId(0L);
            stockIn.setStockInOperator(userName);
            stockIn.setStockInOperatorId(userId);
//        insert.setStockInTime(new Date());
            stockIn.setSourceNo(stockTake.getId().toString());
            stockIn.setRemark("盘盈生成入库单");
            stockIn.setCreateBy(userName);
            stockIn.setCreateTime(new Date());
            stockIn.setGoodsUnit(panying.size());
            stockIn.setGoodsSkuUnit(panying.size());
            stockIn.setStatus(0);//状态（0待入库1部分入库2全部入库）
            int total=0;
            List<ErpWarehouseStockInItem> inItemList = new ArrayList<>();

            for(cn.qihangerp.model.entity.ErpWarehouseStockTakeItem item : panying){
                ErpWarehouseStockInItem inItem = new ErpWarehouseStockInItem();
                inItem.setMerchantId(stockIn.getMerchantId());
//                inItem.setStockInId(stockIn.getId());
                inItem.setMerchantId(stockIn.getMerchantId());
                inItem.setVendorId(stockIn.getVendorId());
                inItem.setGoodsId(item.getGoodsId());
                inItem.setGoodsName(item.getGoodsName());
                inItem.setGoodsImage(item.getGoodsImage());
                inItem.setGoodsNo(item.getGoodsNo());
                inItem.setSkuName(item.getSkuName());
                inItem.setQuantity(item.getTakeQuantity()-item.getQuantity());
                inItem.setInQuantity(0);
                inItem.setStatus(0);
                inItem.setCreateBy(userName);
                inItem.setCreateTime(new Date());
                inItemList.add(inItem);
                total+=inItem.getQuantity();
            }
            stockIn.setTotal(total);
            stockInMapper.insert(stockIn);
            for(ErpWarehouseStockInItem item : inItemList){
                item.setStockInId(stockIn.getId());
                stockInItemMapper.insert(item);
            }

        }

        // 盘亏list
        List<cn.qihangerp.model.entity.ErpWarehouseStockTakeItem> pankui = items.stream()
                .filter(item -> item.getResult() ==30)
                .collect(Collectors.toList());
        if(!pankui.isEmpty()){
            // 添加盘亏出库单
            //添加主表信息
            ErpWarehouseStockOut stockOut = new ErpWarehouseStockOut();
            stockOut.setVendorId(stockTake.getWarehouseId());
            stockOut.setMerchantId(stockTake.getMerchantId());
            stockOut.setOutNum("PKCK-" + DateUtils.parseDateToStr("yyyyMMdd", new Date()) + "-" + System.currentTimeMillis() / 1000);
            stockOut.setType(EnumStockOutType.TAKE_STOCK_OUT.getIndex());
            stockOut.setSourceNum(stockTake.getId().toString());
            stockOut.setSourceId(stockTake.getId());
            stockOut.setRemark("盘亏生成出库单");
            stockOut.setCreateBy(userName);
            stockOut.setCreateTime(new Date());
            stockOut.setGoodsUnit(pankui.size());
            stockOut.setSpecUnit(pankui.size());

            stockOut.setOutTotal(0);
            stockOut.setOperatorId(userId);
            stockOut.setOperatorName(userName);
            stockOut.setPrintStatus(0);
            stockOut.setStatus(0);//状态（0待入库1部分入库2全部入库）
            int total=0;
            List<ErpWarehouseStockOutItem> outItemList = new ArrayList<>();
            for(cn.qihangerp.model.entity.ErpWarehouseStockTakeItem item : pankui){
                ErpWarehouseStockOutItem inItem = new ErpWarehouseStockOutItem();
                inItem.setVendorId(stockOut.getVendorId());
                inItem.setMerchantId(stockOut.getMerchantId());
//                inItem.setEntryId(stockOut.getId());
                inItem.setType(stockOut.getType());
                inItem.setGoodsId(item.getGoodsId());
                inItem.setGoodsName(item.getGoodsName());
                inItem.setGoodsNum(item.getGoodsNo());
                inItem.setGoodsImage(item.getGoodsImage());
                inItem.setSkuName(item.getSkuName());
                inItem.setOriginalQuantity(item.getQuantity()-item.getTakeQuantity());
                inItem.setOutQuantity(0);
                inItem.setStatus(0);
                inItem.setCreateBy(userName);
                inItem.setCreateTime(new Date());
                inItem.setWarehouseId(stockOut.getVendorId());
                outItemList.add(inItem);
                total+=inItem.getOriginalQuantity();
            }
            stockOut.setSpecUnitTotal(total);
            stockOutMapper.insert(stockOut);
            for(ErpWarehouseStockOutItem item : outItemList){
                item.setEntryId(stockOut.getId());
                stockOutItemMapper.insert(item);
            }
        }

        // 更新自己
        ErpWarehouseStockTake update = new ErpWarehouseStockTake();
        update.setId(stockTake.getId());
        update.setUpdateTime(new Date());
        update.setUpdateBy(userName);
        update.setSkuUnit(items.size());
        update.setPanyingUnit(panying.size());
        update.setPankuiUnit(pankui.size());
        update.setTotalQuantity(items.stream().mapToInt(item -> item.getQuantity()).sum());
        update.setResultQuantity(items.stream().mapToInt(item -> item.getTakeQuantity()).sum());
        update.setStatus(2);
        update.setCompleteTime(new Date());
        update.setRemark("生成出入库单");
        this.baseMapper.updateById(update);
        return ResultVo.success();
    }
}




