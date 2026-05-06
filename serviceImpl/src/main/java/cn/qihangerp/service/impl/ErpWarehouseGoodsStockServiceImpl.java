package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.response.ShopGoodsSkuStock;
import cn.qihangerp.model.vo.CloudWarehouseGoodsStockVo;
import cn.qihangerp.model.request.CloudWarehouseGoodsStockRequest;
import cn.qihangerp.mapper.ErpWarehouseGoodsStockBatchMapper;
import cn.qihangerp.mapper.OGoodsSkuMapper;
import cn.qihangerp.service.ErpWarehouseGoodsService;
import cn.qihangerp.service.ErpWarehouseService;
import cn.qihangerp.service.GoodsDailyQuotationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.service.ErpWarehouseGoodsStockService;
import cn.qihangerp.mapper.ErpWarehouseGoodsStockMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author 1
* @description 针对表【erp_cloud_warehouse_goods_stock(云仓商品库存)】的数据库操作Service实现
* @createDate 2025-08-09 11:34:34
*/
@AllArgsConstructor
@Service
public class ErpWarehouseGoodsStockServiceImpl extends ServiceImpl<ErpWarehouseGoodsStockMapper, ErpWarehouseGoodsStock>
    implements ErpWarehouseGoodsStockService {

    private final ErpWarehouseGoodsService warehouseGoodsService;
    private final ErpWarehouseGoodsStockBatchMapper goodsStockBatchMapper;
    private final OGoodsSkuMapper goodsSkuMapper;
    private final ErpWarehouseService erpWarehouseService;
    private final GoodsDailyQuotationService goodsDailyQuotationService;

    @Override
    public PageResult<ErpWarehouseGoodsStock> queryCloudWarehousePageList(CloudWarehouseGoodsStockRequest bo, PageQuery pageQuery) {
//        LambdaQueryWrapper<ErpWarehouseGoodsStock> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
//                .eq(bo.getMerchantId()!=null, ErpWarehouseGoodsStock::getMerchantId,bo.getMerchantId())
//                .eq(StringUtils.hasText(bo.getGoodsNo()), ErpWarehouseGoodsStock::getGoodsNo,bo.getGoodsNo())
//                .eq(StringUtils.hasText(bo.getSellerGoodsSign()), ErpWarehouseGoodsStock::getSellerGoodsSign,bo.getSellerGoodsSign())
//                .like(StringUtils.hasText(bo.getGoodsName()), ErpWarehouseGoodsStock::getGoodsName,bo.getGoodsName())
//                ;
//
//        Page<ErpWarehouseGoodsStock> goodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        pageQuery.setIsAsc("desc");
        pageQuery.setOrderByColumn("usable_num");
        IPage<ErpWarehouseGoodsStock> goodsPage = this.baseMapper.selectPageList(pageQuery.build(), bo.getMerchantId(), null, bo.getWarehouseId(), bo.getGoodsNo(), bo.getSellerGoodsSign(), bo.getGoodsName());
        return PageResult.build(goodsPage);
    }

    @Override
    public List<CloudWarehouseGoodsStockVo> queryCloudWarehouseList(CloudWarehouseGoodsStockRequest bo) {
//                LambdaQueryWrapper<ErpWarehouseGoodsStock> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
//                .eq(bo.getMerchantId()!=null, ErpWarehouseGoodsStock::getMerchantId,bo.getMerchantId())
//                .eq(bo.getWarehouseId()!=null, ErpWarehouseGoodsStock::getWarehouseId,bo.getWarehouseId());
//                .eq(StringUtils.hasText(bo.getSellerGoodsSign()), ErpWarehouseGoodsStock::getSellerGoodsSign,bo.getSellerGoodsSign())
//                .like(StringUtils.hasText(bo.getGoodsName()), ErpWarehouseGoodsStock::getGoodsName,bo.getGoodsName())
//                ;
//
//        Page<ErpWarehouseGoodsStock> goodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
//        return this.baseMapper.selectList(queryWrapper);
        return this.baseMapper.selectExportList(bo.getMerchantId(),bo.getWarehouseId());
    }

    @Override
    public PageResult<ErpWarehouseGoodsStock> queryPageList(ErpWarehouseGoodsStock bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseGoodsStock> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                .eq(bo.getWarehouseId() != null, ErpWarehouseGoodsStock::getWarehouseId, bo.getWarehouseId())
                .eq(bo.getMerchantId() != null, ErpWarehouseGoodsStock::getMerchantId, bo.getMerchantId())
                .eq(bo.getGoodsId() != null, ErpWarehouseGoodsStock::getGoodsId, bo.getGoodsId())
                .eq(StringUtils.hasText(bo.getGoodsNo()), ErpWarehouseGoodsStock::getGoodsNo, bo.getGoodsNo())
                .eq(StringUtils.hasText(bo.getErpGoodsNo()), ErpWarehouseGoodsStock::getErpGoodsNo, bo.getErpGoodsNo())
                .eq(StringUtils.hasText(bo.getErpGoodsSign()), ErpWarehouseGoodsStock::getErpGoodsSign, bo.getErpGoodsSign())
                .eq(StringUtils.hasText(bo.getSellerGoodsSign()), ErpWarehouseGoodsStock::getSellerGoodsSign, bo.getSellerGoodsSign())
                .like(StringUtils.hasText(bo.getGoodsName()), ErpWarehouseGoodsStock::getGoodsName, bo.getGoodsName());

        Page<ErpWarehouseGoodsStock> goodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(goodsPage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo saveAndUpdateJD(ErpWarehouseGoodsStock stock, String warehouseType) {
        List<ErpWarehouse> warehouseList = erpWarehouseService.list(new LambdaQueryWrapper<ErpWarehouse>()
                .eq(ErpWarehouse::getWarehouseNo, stock.getWarehouseNo())
                .eq(ErpWarehouse::getWarehouseType, warehouseType)
        );
        if (warehouseList.isEmpty()) return ResultVo.error("没有找到仓库信息");

        List<ErpWarehouseGoodsStock> stockList = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                .eq(ErpWarehouseGoodsStock::getOwnerNo, stock.getOwnerNo())
                .eq(ErpWarehouseGoodsStock::getGoodsNo, stock.getGoodsNo())
                .eq(ErpWarehouseGoodsStock::getWarehouseId, warehouseList.get(0).getId())
        );
        if (stockList.isEmpty()) {
            // 新增
            stock.setWarehouseId(warehouseList.get(0).getId());
            stock.setWarehouseName(warehouseList.get(0).getWarehouseName());
            stock.setWarehouseType(warehouseList.get(0).getWarehouseType());
            stock.setCreateTime(new Date());
            stock.setCreateBy("拉取云仓库存");
            this.baseMapper.insert(stock);
        } else {
            // 修改
            stock.setWarehouseId(warehouseList.get(0).getId());
            stock.setWarehouseName(warehouseList.get(0).getWarehouseName());
            stock.setWarehouseType(warehouseList.get(0).getWarehouseType());
            stock.setUpdateBy("更新云仓库存");
            stock.setUpdateTime(new Date());
            stock.setId(stockList.get(0).getId());
            this.baseMapper.updateById(stock);
        }
        return ResultVo.success();
    }

    @Override
    public ResultVo saveAndUpdateJky(ErpWarehouseGoodsStock stock) {
        List<ErpWarehouse> warehouseList = erpWarehouseService.list(new LambdaQueryWrapper<ErpWarehouse>()
                .eq(ErpWarehouse::getWarehouseNo, stock.getWarehouseNo())
                .eq(ErpWarehouse::getWarehouseType, "JKYYC")
        );
        if (warehouseList.isEmpty()) return ResultVo.error("没有找到仓库信息");

        List<ErpWarehouseGoodsStock> stockList = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                .eq(ErpWarehouseGoodsStock::getGoodsNo, stock.getGoodsNo())
                .eq(ErpWarehouseGoodsStock::getWarehouseId, warehouseList.get(0).getId())
        );
        // 查询warehouseGoods
        List<ErpWarehouseGoods> goodsItem = warehouseGoodsService.list(new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(ErpWarehouseGoods::getGoodsNo, stock.getGoodsNo())
                .eq(ErpWarehouseGoods::getWarehouseId, warehouseList.get(0).getId()));

        if (stockList.isEmpty()) {
            // 新增
            stock.setGoodsId(goodsItem.isEmpty()?0:goodsItem.get(0).getId());
            stock.setWarehouseId(warehouseList.get(0).getId());
            stock.setWarehouseName(warehouseList.get(0).getWarehouseName());
            stock.setWarehouseType(warehouseList.get(0).getWarehouseType());
            stock.setCreateTime(new Date());
            stock.setCreateBy("拉取云仓库存");
            this.baseMapper.insert(stock);
        } else {
            // 修改
            stock.setGoodsId(goodsItem.isEmpty()?0:goodsItem.get(0).getId());
            stock.setWarehouseId(warehouseList.get(0).getId());
            stock.setWarehouseName(warehouseList.get(0).getWarehouseName());
            stock.setWarehouseType(warehouseList.get(0).getWarehouseType());
            stock.setUpdateBy("更新云仓库存");
            stock.setUpdateTime(new Date());
            stock.setId(stockList.get(0).getId());
            this.baseMapper.updateById(stock);
        }
        return ResultVo.success();
    }

    /**
     * API更新库存
     *
     * @param stock
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> saveAndUpdateAPI(ErpWarehouseGoodsStock stock) {
        ErpWarehouse warehouse = erpWarehouseService.getById(stock.getWarehouseId());
        if (warehouse == null) return ResultVo.error("没有找到仓库信息");

        ErpWarehouseGoods warehouseGoods = null;
        if (stock.getGoodsId() != null) {
            warehouseGoods = warehouseGoodsService.getById(stock.getGoodsId());
        } else {
            warehouseGoods = warehouseGoodsService.queryByErpGoodsNoAndWarehouse(stock.getSellerGoodsSign(), stock.getWarehouseId());
        }
        if (warehouseGoods == null) return ResultVo.error("找不到仓库商品数据！");


        List<ErpWarehouseGoodsStock> stockList = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                .eq(ErpWarehouseGoodsStock::getGoodsId, warehouseGoods.getId())
                .eq(ErpWarehouseGoodsStock::getWarehouseId, stock.getWarehouseId())
                .eq(ErpWarehouseGoodsStock::getStockStatus, stock.getStockStatus())
                .eq(ErpWarehouseGoodsStock::getStockType, stock.getStockType())
        );

        stock.setGoodsId(warehouseGoods.getId());
        stock.setGoodsNo(warehouseGoods.getGoodsNo());
        stock.setGoodsName(warehouseGoods.getGoodsName());
        stock.setSellerGoodsSign(warehouseGoods.getErpGoodsNo());

        stock.setOwnerNo(warehouseGoods.getOwnerNo());
        stock.setOwnerName(warehouseGoods.getShopName());

        stock.setWarehouseNo(warehouse.getWarehouseNo());
        stock.setWarehouseId(warehouse.getId());
        stock.setWarehouseType(warehouse.getWarehouseType());
        stock.setWarehouseName(warehouse.getWarehouseName());

        stock.setTotalNumValue(stock.getTotalNum().doubleValue());
        stock.setUsableNumValue(stock.getUsableNum().doubleValue());
        stock.setErpGoodsId(warehouseGoods.getErpGoodsId());
        stock.setErpGoodsSkuId(warehouseGoods.getErpGoodsSkuId());

        if (stockList.isEmpty()) {
            // 新增
            stock.setMerchantId(warehouseGoods.getMerchantId());
            stock.setCreateTime(new Date());
            stock.setCreateBy("API添加库存");
            this.baseMapper.insert(stock);
        } else {
            // 修改
            stock.setUpdateBy("API更新库存");
            stock.setUpdateTime(new Date());
            stock.setId(stockList.get(0).getId());
            this.baseMapper.updateById(stock);
        }
        return ResultVo.success();
    }

    @Override
    public Integer getGoodsStockQty(Long goodsId) {
        List<ErpWarehouseGoodsStock> erpWarehouseGoodsStocks = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoodsStock>().eq(ErpWarehouseGoodsStock::getGoodsId, goodsId));
        if (erpWarehouseGoodsStocks.isEmpty()) return 0;
        return erpWarehouseGoodsStocks.stream().mapToInt(ErpWarehouseGoodsStock::getTotalNum).sum();
    }

    /**
     * 搜索店铺库存
     * @param code 条码、sku编码
     * @param shopId 店铺id
     * @return
     */
    @Override
    public List<ShopGoodsSkuStock> searchShopGoodsSkuAndStockAndPrice(String code, Long shopId) {

        List<ShopGoodsSkuStock> list = new ArrayList<>();

        // 搜索 商品库存明细 （条码的搜索情况）
        LambdaQueryWrapper<ErpWarehouseGoodsStockBatch> qw = new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                .eq(ErpWarehouseGoodsStockBatch::getShopId, shopId)
                .like(ErpWarehouseGoodsStockBatch::getBarcode, code);
        List<ErpWarehouseGoodsStockBatch> oGoodsInventoryBatches = goodsStockBatchMapper.selectList(qw);
        if (oGoodsInventoryBatches != null || oGoodsInventoryBatches.size() > 0) {
            // 存在条码记录，返回条码信息
            for (ErpWarehouseGoodsStockBatch stockBatch : oGoodsInventoryBatches) {
                // 仓库商品
                var warehouseGoods = warehouseGoodsService.getById(stockBatch.getGoodsId());
                if(warehouseGoods == null) continue;
                // 查找sku以便确定价格
                OGoodsSku oGoodsSku = goodsSkuMapper.selectById(warehouseGoods.getErpGoodsSkuId());
//                OGoodsInventory oGoodsInventory = mapper.selectById(oGoodsInventoryBatch.getInventoryId());
                if (oGoodsSku != null) {
                    ShopGoodsSkuStock ss = new ShopGoodsSkuStock();
                    ss.setSkuId(oGoodsSku.getId());
                    ss.setSkuName(oGoodsSku.getSkuName());
                    ss.setSkuCode(oGoodsSku.getSkuCode());
                    ss.setGoodsName(oGoodsSku.getGoodsName());
                    ss.setQuantity(null);
                    ss.setPrice(null);
                    ss.setInventoryMode(oGoodsSku.getInventoryMode());
                    // 看看 库存模式
                    if (oGoodsSku.getInventoryMode() == 1) {
                        //一物一码模式（珠宝）
                        List<ShopGoodsSkuStock.Batch> batches = new ArrayList<>();

                        ShopGoodsSkuStock.Batch batch = new ShopGoodsSkuStock.Batch();
                        batch.setBatchNum(stockBatch.getBatchNum());
                        batch.setBarCode(stockBatch.getBarcode());
                        batch.setQuantity(stockBatch.getCurrentQty());
                        batch.setGoldWeight(stockBatch.getActualGoldWeight());
                        batch.setSilverWeight(stockBatch.getActualSilverWeight());
                        batch.setLaborCost(stockBatch.getLaborCost());
                        if (oGoodsSku.getPriceType() == 0) {
                            // 一口价模式，取sku的最新价格
                            batch.setPrice(oGoodsSku.getRetailPrice());
                        } else if (oGoodsSku.getPriceType() == 1) {
                            BigDecimal goldPrice = BigDecimal.ZERO;
                            BigDecimal silverPrice = BigDecimal.ZERO;
                            BigDecimal laborCost = BigDecimal.ZERO;
                            // 金包银+工费； 取最新报价
                            GoodsDailyQuotation goodsDailyQuotation = goodsDailyQuotationService.queryNewGoodsDailyQuotation(1);
                            if (goodsDailyQuotation != null) {
                                batch.setGoldPrice(goodsDailyQuotation.getPrice1());
                                batch.setSilverPrice(goodsDailyQuotation.getPrice2());
                                batch.setLaborPrice(goodsDailyQuotation.getPrice3());

                                goldPrice = BigDecimal.valueOf(batch.getGoldWeight()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice1()));
                                silverPrice = BigDecimal.valueOf(batch.getSilverWeight()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice2()));
                                laborCost = BigDecimal.valueOf(batch.getLaborCost()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice3()));
                            }else {
                                batch.setGoldPrice(0.0);
                                batch.setSilverPrice(0.0);
                                batch.setLaborPrice(0.0);
                            }

                            batch.setPrice(goldPrice.add(silverPrice).add(laborCost));
                        }
                        batches.add(batch);
                        ss.setBatchList(batches);
                    }else{
                        // 非一物一码的方式 (价格=price1*weight1 + price2+weight2 + price3*weight3)
                        if (oGoodsSku.getPriceType() == 0) {
                            // 一口价模式，取sku的最新价格
                            ss.setPrice(oGoodsSku.getRetailPrice());
                        } else if (oGoodsSku.getPriceType() == 1) {
                            BigDecimal goldPrice = BigDecimal.ZERO;
                            BigDecimal silverPrice = BigDecimal.ZERO;
                            BigDecimal laborCost = BigDecimal.ZERO;
                            // 金包银+工费； 取最新报价
                            GoodsDailyQuotation goodsDailyQuotation = goodsDailyQuotationService.queryNewGoodsDailyQuotation(1);
                            if (goodsDailyQuotation != null) {
                                goldPrice = BigDecimal.valueOf(oGoodsSku.getWeight1()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice1()));
                                silverPrice = BigDecimal.valueOf(oGoodsSku.getWeight2()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice2()));
                                silverPrice = BigDecimal.valueOf(oGoodsSku.getWeight3()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice3()));
                            }
                            ss.setPrice(goldPrice.add(silverPrice).add(laborCost));
                        }
                    }
                    list.add(ss);
                }
            }
            return list;
        }

        // 搜索 商品库存（SKU编码的情况）
        LambdaQueryWrapper<ErpWarehouseGoodsStock> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                .eq(ErpWarehouseGoodsStock::getShopId, shopId)
                .like(ErpWarehouseGoodsStock::getGoodsName, code)
                .like(ErpWarehouseGoodsStock::getGoodsNo, code)
                .like(ErpWarehouseGoodsStock::getErpGoodsNo, code);
        queryWrapper.last("limit 10");
        List<ErpWarehouseGoodsStock> goodsStocks = this.baseMapper.selectList(queryWrapper);
        if (goodsStocks != null && goodsStocks.size() > 0) {
            for (ErpWarehouseGoodsStock oi : goodsStocks) {
                // 仓库商品
                var warehouseGoods = warehouseGoodsService.getById(oi.getGoodsId());
                if(warehouseGoods == null) continue;
                // 查找sku以便确定价格
                OGoodsSku oGoodsSku = goodsSkuMapper.selectById(warehouseGoods.getErpGoodsSkuId());

                if (oGoodsSku != null) {
                    ShopGoodsSkuStock ss = new ShopGoodsSkuStock();
                    ss.setSkuId(oGoodsSku.getId());
                    ss.setSkuCode(oGoodsSku.getSkuCode());
                    ss.setSkuName(oGoodsSku.getSkuName());
                    ss.setGoodsName(oGoodsSku.getGoodsName());
                    ss.setQuantity(oGoodsSku.getQuantity());
                    ss.setInventoryMode(oGoodsSku.getInventoryMode());
                    // 看看 库存模式
                    if (oGoodsSku.getInventoryMode() == 1) {
                        //一物一码模式（珠宝）
                        List<ShopGoodsSkuStock.Batch> batches = new ArrayList<>();
                        // 搜索 商品库存明细
                        LambdaQueryWrapper<ErpWarehouseGoodsStockBatch> qw1 = new LambdaQueryWrapper<ErpWarehouseGoodsStockBatch>()
                                .eq(ErpWarehouseGoodsStockBatch::getInventoryId, oGoodsSku.getId());
                        qw1.last("limit 10");
                        List<ErpWarehouseGoodsStockBatch> batchList = goodsStockBatchMapper.selectList(qw1);
                        if (batchList != null || batchList.size() > 0) {
                            for (ErpWarehouseGoodsStockBatch batch : batchList) {
                                ShopGoodsSkuStock.Batch b = new ShopGoodsSkuStock.Batch();
                                b.setBatchNum(batch.getBatchNum());
                                b.setBarCode(batch.getBarcode());
                                b.setQuantity(batch.getCurrentQty());
                                b.setGoldWeight(batch.getActualGoldWeight());
                                b.setSilverWeight(batch.getActualSilverWeight());
                                b.setLaborCost(batch.getLaborCost());
                                if (oGoodsSku.getPriceType() == 0) {
                                    // 一口价模式，取sku的最新价格
                                    b.setPrice(oGoodsSku.getRetailPrice());
                                } else if (oGoodsSku.getPriceType() == 1) {
                                    BigDecimal goldPrice = BigDecimal.ZERO;
                                    BigDecimal silverPrice = BigDecimal.ZERO;
                                    BigDecimal laborCost = BigDecimal.ZERO;
                                    // 金包银+工费； 取最新报价
                                    GoodsDailyQuotation goodsDailyQuotation = goodsDailyQuotationService.queryNewGoodsDailyQuotation(1);
                                    if (goodsDailyQuotation != null) {
                                        goldPrice = BigDecimal.valueOf(batch.getActualGoldWeight()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice1()));
                                        silverPrice = BigDecimal.valueOf(batch.getActualSilverWeight()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice2()));
                                        silverPrice = BigDecimal.valueOf(batch.getLaborCost()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice3()));
                                    }
                                    b.setPrice(goldPrice.add(silverPrice).add(laborCost));
                                }
                                batches.add(b);
                            }

                            ss.setBatchList(batches);
                        }

                    }else{
                        // 非一物一码的方式 (价格=price1*weight1 + price2+weight2 + price3*weight3)
                        if (oGoodsSku.getPriceType() == 0) {
                            // 一口价模式，取sku的最新价格
                            ss.setPrice(oGoodsSku.getRetailPrice());
                        } else if (oGoodsSku.getPriceType() == 1) {
                            BigDecimal goldPrice = BigDecimal.ZERO;
                            BigDecimal silverPrice = BigDecimal.ZERO;
                            BigDecimal laborCost = BigDecimal.ZERO;
                            // 金包银+工费； 取最新报价
                            GoodsDailyQuotation goodsDailyQuotation = goodsDailyQuotationService.queryNewGoodsDailyQuotation(1);
                            if (goodsDailyQuotation != null) {
                                goldPrice = BigDecimal.valueOf(oGoodsSku.getWeight1()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice1()));
                                silverPrice = BigDecimal.valueOf(oGoodsSku.getWeight2()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice2()));
                                silverPrice = BigDecimal.valueOf(oGoodsSku.getWeight3()).multiply(BigDecimal.valueOf(goodsDailyQuotation.getPrice3()));
                            }
                            ss.setPrice(goldPrice.add(silverPrice).add(laborCost));
                        }
                    }
                    list.add(ss);
                }
            }
            return list;
        }
        return List.of();
    }

}



