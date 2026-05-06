package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpWarehouse;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStock;
import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.model.request.WarehouseGoodsAddRequest;
import cn.qihangerp.mapper.ErpWarehouseGoodsStockMapper;
import cn.qihangerp.service.ErpWarehouseGoodsService;
import cn.qihangerp.service.ErpWarehouseService;
import cn.qihangerp.service.OGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpWarehouseGoods;
import cn.qihangerp.mapper.ErpWarehouseGoodsMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【erp_cloud_warehouse_goods】的数据库操作Service实现
* @createDate 2025-07-07 17:02:48
*/
@AllArgsConstructor
@Service
public class ErpWarehouseGoodsServiceImpl extends ServiceImpl<ErpWarehouseGoodsMapper, ErpWarehouseGoods>
    implements ErpWarehouseGoodsService {
    private final ErpWarehouseService warehouseService;
    private final OGoodsSkuService oGoodsSkuService;
    private final ErpWarehouseGoodsStockMapper warehouseGoodsStockMapper;
    @Override
    public PageResult<ErpWarehouseGoods> queryPageList(ErpWarehouseGoods bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseGoods> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(bo.getId()!=null, ErpWarehouseGoods::getId,bo.getId())
                .eq(bo.getMerchantId()!=null, ErpWarehouseGoods::getMerchantId,bo.getMerchantId())
                .eq(bo.getWarehouseId()!=null, ErpWarehouseGoods::getWarehouseId,bo.getWarehouseId())
                .eq(bo.getErpGoodsSkuId()!=null, ErpWarehouseGoods::getErpGoodsSkuId,bo.getErpGoodsSkuId())
                .eq(StringUtils.hasText(bo.getErpGoodsSign()), ErpWarehouseGoods::getErpGoodsSign,bo.getErpGoodsSign())
                .eq(StringUtils.hasText(bo.getWarehouseType()), ErpWarehouseGoods::getWarehouseType,bo.getWarehouseType())
                .like(StringUtils.hasText(bo.getErpGoodsNo()), ErpWarehouseGoods::getErpGoodsNo,bo.getErpGoodsNo())

                .eq(StringUtils.hasText(bo.getGoodsNo()), ErpWarehouseGoods::getGoodsNo,bo.getGoodsNo())

                .eq(StringUtils.hasText(bo.getErpGoodsNo()), ErpWarehouseGoods::getErpGoodsNo,bo.getErpGoodsNo())
                .like(StringUtils.hasText(bo.getGoodsName()), ErpWarehouseGoods::getGoodsName,bo.getGoodsName())
                ;
        pageQuery.setIsAsc("desc");
        pageQuery.setOrderByColumn("erp_goods_sku_id");
        Page<ErpWarehouseGoods> goodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
//        if(goodsPage.getRecords()!=null&&!goodsPage.getRecords().isEmpty()){
//            for(ErpWarehouseGoods goods:goodsPage.getRecords()){
//                goods.setStock(warehouseGoodsStockService.getGoodsStockQty(goods.getId()));
//            }
//        }
        return PageResult.build(goodsPage);
    }

    @Override
    public PageResult<ErpWarehouseGoods> queryGoodsAndStockPageList(ErpWarehouseGoods bo, PageQuery pageQuery) {

        LambdaQueryWrapper<ErpWarehouseGoods> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(bo.getId()!=null, ErpWarehouseGoods::getId,bo.getId())
                .eq(bo.getMerchantId()!=null, ErpWarehouseGoods::getMerchantId,bo.getMerchantId())
                .eq(bo.getWarehouseId()!=null, ErpWarehouseGoods::getWarehouseId,bo.getWarehouseId())
                .eq(bo.getErpGoodsSkuId()!=null, ErpWarehouseGoods::getErpGoodsSkuId,bo.getErpGoodsSkuId())
                .eq(StringUtils.hasText(bo.getErpGoodsSign()), ErpWarehouseGoods::getErpGoodsSign,bo.getErpGoodsSign())
                .eq(StringUtils.hasText(bo.getWarehouseType()), ErpWarehouseGoods::getWarehouseType,bo.getWarehouseType())
                .like(StringUtils.hasText(bo.getErpGoodsNo()), ErpWarehouseGoods::getErpGoodsNo,bo.getErpGoodsNo())

                .eq(StringUtils.hasText(bo.getGoodsNo()), ErpWarehouseGoods::getGoodsNo,bo.getGoodsNo())

                .eq(StringUtils.hasText(bo.getErpGoodsNo()), ErpWarehouseGoods::getErpGoodsNo,bo.getErpGoodsNo())
                .like(StringUtils.hasText(bo.getGoodsName()), ErpWarehouseGoods::getGoodsName,bo.getGoodsName())
                ;

        Page<ErpWarehouseGoods> goodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        if(goodsPage.getRecords()!=null&&!goodsPage.getRecords().isEmpty()){
            for(ErpWarehouseGoods goods:goodsPage.getRecords()){
                Integer stock =0;
                List<ErpWarehouseGoodsStock> erpWarehouseGoodsStocks = warehouseGoodsStockMapper.selectList(
                        new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                                .eq(ErpWarehouseGoodsStock::getGoodsId, goods.getId()));

                if (erpWarehouseGoodsStocks!=null&&!erpWarehouseGoodsStocks.isEmpty()) {
                    stock = erpWarehouseGoodsStocks.stream().mapToInt(ErpWarehouseGoodsStock::getTotalNum).sum();
                }
                goods.setStock(stock);
            }
        }
        return PageResult.build(goodsPage);
    }


    @Override
    public ResultVo saveAndUpdate(ErpWarehouseGoods bo) {
        String warehouseNo="";
        String warehouseName="";
        String warehouseType="";
        Long warehouseId=0L;
        List<ErpWarehouse> warehouseList = warehouseService.list(new LambdaQueryWrapper<ErpWarehouse>().eq(ErpWarehouse::getWarehouseNo, bo.getWarehouseNo()));
        if(!warehouseList.isEmpty()){
            warehouseNo = warehouseList.get(0).getWarehouseNo();
            warehouseType = warehouseList.get(0).getWarehouseType();
            warehouseId = warehouseList.get(0).getId();
            warehouseName = warehouseList.get(0).getWarehouseName();
        }
        Long warehouseGoodsId=0L;
        List<ErpWarehouseGoods> erpWarehouseGoods = new ArrayList<ErpWarehouseGoods>();
        // 查云仓商品唯一id
        erpWarehouseGoods = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(bo.getMerchantId()!=null, ErpWarehouseGoods::getMerchantId,bo.getMerchantId())
                .eq(ErpWarehouseGoods::getGoodsNo,bo.getGoodsNo()));
        if(erpWarehouseGoods.isEmpty()){
            // 查erp系统内部编码
            erpWarehouseGoods = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoods>()
                    .eq(bo.getMerchantId()!=null, ErpWarehouseGoods::getMerchantId,bo.getMerchantId())
                    .eq(ErpWarehouseGoods::getErpGoodsNo,bo.getErpGoodsNo()));
        }
        if(erpWarehouseGoods.size()>0){
            // 存在，更新
            bo.setErpGoodsSkuId(bo.getErpGoodsSkuId()==null||bo.getErpGoodsSkuId()==0?null:bo.getErpGoodsSkuId());
            bo.setErpGoodsId(bo.getErpGoodsId()==null||bo.getErpGoodsId()==0?null:bo.getErpGoodsId());
            bo.setWarehouseId(warehouseId);
            bo.setWarehouseNo(warehouseNo);
            bo.setWarehouseType(warehouseType);
            bo.setUpdateTime(new Date());
            if(!StringUtils.hasText(bo.getUpdateBy())) {
                bo.setUpdateBy("同步更新");
            }
            bo.setId(erpWarehouseGoods.get(0).getId());
            this.baseMapper.updateById(bo);
            warehouseGoodsId = erpWarehouseGoods.get(0).getId();
        }else {
            // 不存在，新增
            bo.setWarehouseId(warehouseId);
            bo.setWarehouseNo(warehouseNo);
            bo.setWarehouseType(warehouseType);
            bo.setCreateTime(new Date());
            if(!StringUtils.hasText(bo.getCreateBy())){
                bo.setCreateBy("手动同步");
            }
            this.baseMapper.insert(bo);
            warehouseGoodsId = bo.getId();
        }

        // 初始化库存
        List<ErpWarehouseGoodsStock> erpWarehouseGoodsStocks = warehouseGoodsStockMapper.selectList(
                new LambdaQueryWrapper<ErpWarehouseGoodsStock>().eq(ErpWarehouseGoodsStock::getGoodsId, warehouseGoodsId));

        if(erpWarehouseGoodsStocks==null||erpWarehouseGoodsStocks.isEmpty()){
            // 新增库存初始化
            ErpWarehouseGoodsStock stock = new ErpWarehouseGoodsStock();
            stock.setGoodsId(warehouseGoodsId);
            stock.setGoodsNo(bo.getGoodsNo());
            stock.setGoodsName(bo.getGoodsName());
            stock.setErpGoodsNo(bo.getErpGoodsNo());
            stock.setErpGoodsSign(bo.getErpGoodsSign());
            stock.setOwnerNo(bo.getOwnerNo());
            stock.setWarehouseId(warehouseId);
            stock.setWarehouseNo(warehouseNo);
            stock.setWarehouseType(warehouseType);
            stock.setWarehouseName(warehouseName);
            stock.setStockStatus(1);
            stock.setStockType(1);
            stock.setMerchantId(bo.getMerchantId());
            stock.setTotalNum(0);
            stock.setTotalNumValue(0.0);
            stock.setUsableNum(0);
            stock.setUsableNumValue(0.0);
            stock.setCreateTime(new Date());
            warehouseGoodsStockMapper.insert(stock);
        }

        return ResultVo.success();
    }

    @Override
    public ResultVo saveUpdate(ErpWarehouseGoods bo) {
        ErpWarehouse warehouse = warehouseService.getById(bo.getWarehouseId());
        if(warehouse==null) return ResultVo.error("仓库不存在");


        LambdaQueryWrapper<ErpWarehouseGoods> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(bo.getMerchantId()!=null, ErpWarehouseGoods::getMerchantId,bo.getMerchantId())
                .eq(ErpWarehouseGoods::getGoodsNo,bo.getGoodsNo());
        List<ErpWarehouseGoods> erpWarehouseGoods = this.baseMapper.selectList(queryWrapper);
        if(erpWarehouseGoods.size()>0){
            // 存在，更新
            bo.setUpdateTime(new Date());
            bo.setId(erpWarehouseGoods.get(0).getId());

            this.baseMapper.updateById(bo);
        }else {
            // 不存在，新增
            bo.setWarehouseNo(warehouse.getWarehouseNo());
            bo.setWarehouseType(warehouse.getWarehouseType());
            bo.setCreateTime(new Date());
            this.baseMapper.insert(bo);
        }
        return ResultVo.success();
    }

    @Override
    public ErpWarehouseGoods queryByErpGoodsSkuId(Long erpGoodsSkuId, Long warehouseId) {
        List<ErpWarehouseGoods> erpWarehouseGoods = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoods>()
                .eq(ErpWarehouseGoods::getErpGoodsSkuId, erpGoodsSkuId)
                .eq(ErpWarehouseGoods::getWarehouseId, warehouseId)
        );
        if (erpWarehouseGoods.isEmpty()) return null;
        else return erpWarehouseGoods.get(0);

    }

    @Override
    public List<ErpWarehouseGoods> getVendorGoodsSpecByCode(Long merchantId,Long warehouseId, String skuCode) {
        return this.baseMapper.getVendorGoodsByCode(merchantId,warehouseId,skuCode);
    }

    @Override
    public List<ErpWarehouseGoods> getVendorGoodsSpecStockByCode(Long merchantId, Long warehouseId, String skuCode) {
        return this.baseMapper.getVendorGoodsStockByCode(merchantId,warehouseId,skuCode);
    }

    @Override
    public ErpWarehouseGoods queryByErpGoodsNoAndWarehouse(String erpGoodsNo, Long warehouseId) {
        List<ErpWarehouseGoods> erpWarehouseGoods = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoods>().eq(ErpWarehouseGoods::getErpGoodsNo, erpGoodsNo).eq(ErpWarehouseGoods::getWarehouseId, warehouseId));
        if(erpWarehouseGoods.isEmpty()) return null;
        else return erpWarehouseGoods.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteGoodsByIds(Long[] ids) {
        // 有业务关联的商品
        for (Long id:ids){
            List<ErpWarehouseGoodsStock> erpWarehouseGoodsStocks = warehouseGoodsStockMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoodsStock>().eq(ErpWarehouseGoodsStock::getGoodsId, id));
            if(!erpWarehouseGoodsStocks.isEmpty()) {
                return -100;
            }
            // 删除商品
            this.baseMapper.deleteById(id);
            // 删除库存
//            warehouseGoodsStockMapper.delete(new LambdaQueryWrapper<ErpWarehouseGoodsStock>().eq(ErpWarehouseGoodsStock::getGoodsId, id));
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo updateGoods(ErpWarehouseGoods bo) {
        ErpWarehouseGoods updateBo = new ErpWarehouseGoods();
        updateBo.setId(bo.getId());
        updateBo.setErpGoodsNo(bo.getErpGoodsNo());
        updateBo.setStandard(bo.getStandard());
        updateBo.setGoodsNo(bo.getGoodsNo());
        updateBo.setStandard(bo.getStandard());
        updateBo.setGoodsName(bo.getGoodsName());
        updateBo.setColor(bo.getColor());
        updateBo.setSize(bo.getSize());
        this.baseMapper.updateById(updateBo);
        // 更新库存表数据
        List<ErpWarehouseGoodsStock> erpWarehouseGoodsStocks = warehouseGoodsStockMapper.selectList(new LambdaQueryWrapper<ErpWarehouseGoodsStock>().eq(ErpWarehouseGoodsStock::getGoodsId, updateBo.getId()));
        if(erpWarehouseGoodsStocks!=null && erpWarehouseGoodsStocks.size()>0){
            ErpWarehouseGoodsStock stock = new ErpWarehouseGoodsStock();
            stock.setId(erpWarehouseGoodsStocks.get(0).getId());
            stock.setErpGoodsNo(bo.getErpGoodsNo());
            stock.setGoodsNo(bo.getGoodsNo());
            stock.setGoodsName(bo.getGoodsName());
            stock.setUpdateBy("手动修改商品更新");
            stock.setUpdateTime(new Date());
            warehouseGoodsStockMapper.updateById(stock);
        }

        return ResultVo.success();
    }

    @Override
    public ResultVo<Long> addGoods(WarehouseGoodsAddRequest bo) {
        ErpWarehouse warehouse = warehouseService.getById(bo.getWarehouseId());
        if(warehouse==null) return ResultVo.error("仓库数据不存在");

        OGoodsSku oGoodsSku = oGoodsSkuService.getById(bo.getErpGoodsSkuId());
        if(oGoodsSku==null) return ResultVo.error("商品sku不存在");
        ErpWarehouseGoods  warehouseGoods = new ErpWarehouseGoods();
        warehouseGoods.setWarehouseId(warehouse.getId());
        warehouseGoods.setWarehouseNo(warehouse.getWarehouseNo());
        warehouseGoods.setWarehouseType(warehouse.getWarehouseType());
        warehouseGoods.setOwnerNo(warehouse.getOwnerNo());
        warehouseGoods.setMerchantId(warehouse.getMerchantId());
        warehouseGoods.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        warehouseGoods.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
        warehouseGoods.setGoodsName(oGoodsSku.getGoodsName());
        warehouseGoods.setStandard(oGoodsSku.getSkuName());
        warehouseGoods.setUnitName(bo.getUnitName());
        warehouseGoods.setErpGoodsNo(oGoodsSku.getSkuCode());
        warehouseGoods.setErpGoodsSign(oGoodsSku.getId());
        warehouseGoods.setGoodsNo(oGoodsSku.getSkuCode());
        warehouseGoods.setImageUrl(oGoodsSku.getColorImage());
        warehouseGoods.setBarCode(bo.getBarCode());
        warehouseGoods.setLength(0.0);
        warehouseGoods.setWidth(0.0);
        warehouseGoods.setHeight(0.0);
        warehouseGoods.setVolume(0.0);
        warehouseGoods.setGrossWeight(0.0);
        warehouseGoods.setNetWeight(0.0);
        warehouseGoods.setColor(oGoodsSku.getColorValue());
        warehouseGoods.setSize(oGoodsSku.getSizeValue());
        this.baseMapper.insert(warehouseGoods);
        return ResultVo.success(warehouseGoods.getId());
    }
}




