package cn.qihangerp.module.service.impl;


import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.utils.StringUtils;
import cn.qihangerp.model.entity.OGoods;
import cn.qihangerp.model.entity.OGoodsInventory;
import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.mapper.OGoodsInventoryMapper;
import cn.qihangerp.mapper.OGoodsMapper;
import cn.qihangerp.mapper.OGoodsSkuMapper;
import cn.qihangerp.model.entity.PddGoods;
import cn.qihangerp.model.entity.PddGoodsSku;
import cn.qihangerp.model.bo.PddGoodsBo;
import cn.qihangerp.mapper.PddGoodsMapper;
import cn.qihangerp.mapper.PddGoodsSkuMapper;
import cn.qihangerp.module.service.PddGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author TW
* @description 针对表【pdd_goods(pdd商品表)】的数据库操作Service实现
* @createDate 2024-06-04 17:11:49
*/
@AllArgsConstructor
@Service
public class PddGoodsServiceImpl extends ServiceImpl<PddGoodsMapper, PddGoods>
    implements PddGoodsService {
    private final PddGoodsMapper mapper;
    private final PddGoodsSkuMapper skuMapper;
    private final OGoodsSkuMapper goodsSkuMapper;
    private final OGoodsMapper goodsMapper;
    private final OGoodsInventoryMapper inventoryMapper;
    @Override
    public PageResult<PddGoods> queryPageList(PddGoodsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PddGoods> queryWrapper = new LambdaQueryWrapper<PddGoods>()
                .eq(bo.getShopId()!=null,PddGoods::getShopId,bo.getShopId());

        Page<PddGoods> goodsPage = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(goodsPage.getRecords()!=null&&goodsPage.getRecords().size()>0){
            for(PddGoods goods : goodsPage.getRecords()){
                goods.setSkuList(skuMapper.selectList(new LambdaQueryWrapper<PddGoodsSku>().eq(PddGoodsSku::getGoodsId,goods.getGoodsId())));
            }
        }
        return PageResult.build(goodsPage);
    }

    @Transactional
    @Override
    public ResultVo<Integer> saveGoods(Long shopId, PddGoods goods) {
        List<PddGoods> jdGoods = mapper.selectList(new LambdaQueryWrapper<PddGoods>().eq(PddGoods::getGoodsId, goods.getGoodsId()));
        if(jdGoods== null || jdGoods.isEmpty()){
            // 新增
            goods.setShopId(shopId);
            goods.setCreateTime(new Date());
            mapper.insert(goods);
        }else{
            // 修改
            goods.setId(jdGoods.get(0).getId());
//            goods.setShopId(shopId);
            goods.setUpdateTime(new Date());
            mapper.updateById(goods);
            // 删除sku
//            skuMapper.delete(new LambdaQueryWrapper<PddGoodsSku>().eq(PddGoodsSku::getGoodsId,goods.getGoodsId()));
        }
        Long erpGoodsId=0L;
        String erpGoodsNum="";
        // 添加sku
        if(goods.getSkuList()!=null && !goods.getSkuList().isEmpty()){
            for (var item : goods.getSkuList()){
                item.setGoodsId(goods.getGoodsId());
                item.setGoodsName(goods.getGoodsName());
                item.setThumbUrl(goods.getThumbUrl());
                item.setShopId(shopId);


                // 根据OuterId查找ERP系统中的skuid
                if(StringUtils.isNotEmpty(item.getOuterId())) {
                    List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, item.getOuterId()));
                    if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
                        erpGoodsId = oGoodsSkus.get(0).getGoodsId();
                        erpGoodsNum = oGoodsSkus.get(0).getGoodsNum();
                        item.setErpGoodsId(oGoodsSkus.get(0).getGoodsId());
                        item.setErpGoodsSkuId(oGoodsSkus.get(0).getId());
                    }
                }

                item.setCreateTime(new Date());
                List<PddGoodsSku> pddGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<PddGoodsSku>().eq(PddGoodsSku::getSkuId, item.getSkuId()));
                if(pddGoodsSkus!=null && !pddGoodsSkus.isEmpty()){
                    // 存在更新
                    item.setUpdateTime(new Date());
                    skuMapper.updateById(item);
                }else {
                    item.setCreateTime(new Date());
                    skuMapper.insert(item);
                }
            }
        }
        if(erpGoodsId>0){
            PddGoods updateGoods = new PddGoods();
            updateGoods.setId(goods.getId());
            updateGoods.setErpGoodsId(erpGoodsId);
            updateGoods.setOuterGoodsId(erpGoodsNum);
            mapper.updateById(goods);
        }
        return ResultVo.success();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo pushToOms(Long taoGoodsId) {
        PddGoods shopGoods = mapper.selectById(taoGoodsId);
        if(shopGoods==null) return ResultVo.error("店铺商品数据不存在");

        List<PddGoodsSku> shopGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<PddGoodsSku>().eq(PddGoodsSku::getGoodsId, shopGoods.getGoodsId()));
        if(shopGoodsSkus==null || shopGoodsSkus.isEmpty()) return ResultVo.error("店铺商品Sku数据不存在");

        String goodsNum ="";
        if(org.springframework.util.StringUtils.hasText(shopGoods.getOuterGoodsId())){
            goodsNum = shopGoods.getOuterGoodsId();
            // 用商家编码查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                // 存在=======关联
                //更新shopGoods
                PddGoods shopGoodsUpdate = new PddGoods();
                shopGoodsUpdate.setId(shopGoods.getId());
                shopGoodsUpdate.setErpGoodsId(erpGoodsList.get(0).getId());
                mapper.updateById(shopGoodsUpdate);

                List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>()
                        .eq(OGoodsSku::getGoodsId, erpGoodsList.get(0).getId())
                );
                //更新skus
                for (var sku:shopGoodsSkus){
                    if(org.springframework.util.StringUtils.hasText(sku.getOuterId())){
                        List<OGoodsSku> oGoodsSkuList = oGoodsSkus.stream().filter(x -> x.getSkuCode().equals(sku.getOuterId())).collect(Collectors.toList());
                        if(oGoodsSkuList!=null && !oGoodsSkuList.isEmpty()){
                            //更新ShopGoodsSku
                            PddGoodsSku shopGoodsSkuUpdate = new PddGoodsSku();
                            shopGoodsSkuUpdate.setId(sku.getId());
                            shopGoodsSkuUpdate.setErpGoodsId(oGoodsSkuList.get(0).getGoodsId());
                            shopGoodsSkuUpdate.setErpGoodsSkuId(oGoodsSkuList.get(0).getId());
                            skuMapper.updateById(shopGoodsSkuUpdate);
                        }
                    }
                }

                return ResultVo.success("商品已存在，更新关联");
            }

        }else {
            goodsNum = shopGoods.getGoodsId().toString();
            // 用商品ID查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                return ResultVo.error(ResultVoEnum.DataExist.getIndex(),"商品已存在");
            }
        }


        // 添加商品
        OGoods erpGoods = new OGoods();
        erpGoods.setName(shopGoods.getGoodsName());
        erpGoods.setImage(shopGoods.getThumbUrl());
        erpGoods.setGoodsNum(goodsNum);
        erpGoods.setCategoryId(0L);
        erpGoods.setRemark("PDD店铺商品同步");
        erpGoods.setStatus(1);
        erpGoods.setDisable(1);
        if (shopGoods.getMarketPrice() != null) {
            erpGoods.setRetailPrice(BigDecimal.valueOf(shopGoods.getMarketPrice()/100));
        }
        erpGoods.setCreateBy("PDD店铺商品同步");
        erpGoods.setCreateTime(new Date());
        goodsMapper.insert(erpGoods);

        //更新shopGoods
        PddGoods shopGoodsUpdate = new PddGoods();
        shopGoodsUpdate.setId(shopGoods.getId());
        shopGoodsUpdate.setErpGoodsId(erpGoods.getId());
        mapper.updateById(shopGoodsUpdate);

        // 添加商品SKU
        for (var sku:shopGoodsSkus){
            OGoodsSku erpGoodsSku = new OGoodsSku();
            erpGoodsSku.setGoodsId(erpGoods.getId());
            erpGoodsSku.setGoodsName(erpGoods.getName());
            erpGoodsSku.setGoodsNum(erpGoods.getGoodsNum());
            //122216927:77835123:家具结构:框架结构;1627207:25326567650:颜色分类:奶油白【进口荔枝纹头层牛皮+碳素钢木排骨架】;21433:50753444:尺寸:1500mm*2000mm
            // 组合规格
            String colorLabel="";
            String colorValue="";
            String sizeLabel="";
            String sizeValue="";
            String styleLabel="";
            String styleValue="";
            //组合属性
            if(org.springframework.util.StringUtils.hasText(sku.getSpec())) {
                String[] specArr = sku.getSpec().split(" ");
                if(specArr.length>0){
                    colorLabel="颜色分类";
                    colorValue=specArr[0];
                }
                if(specArr.length>1){
                    sizeLabel = "尺寸";
                    sizeValue=specArr[1];
                }
                if(specArr.length>2){
                    styleLabel="款式";
                    styleValue=specArr[2];
                }
            }else{
                colorValue="默认";
                colorLabel="颜色分类";
            }

            erpGoodsSku.setColorId(0L);
            erpGoodsSku.setColorLabel(colorLabel);
            erpGoodsSku.setColorValue(colorValue);
            erpGoodsSku.setSizeId(0L);
            erpGoodsSku.setSizeLabel(sizeLabel);
            erpGoodsSku.setSizeValue(sizeValue);
            erpGoodsSku.setStyleId(0L);
            erpGoodsSku.setStyleLabel(styleLabel);
            erpGoodsSku.setStyleValue(styleValue);
            String skuName="";
            if(org.springframework.util.StringUtils.hasText(colorValue)){
                skuName += colorValue+" ";
            }
            if(org.springframework.util.StringUtils.hasText(sizeValue)){
                skuName += sizeValue+" ";
            }
            if(org.springframework.util.StringUtils.hasText(styleValue)){
                skuName += styleValue+" ";
            }
            if(!org.springframework.util.StringUtils.hasText(skuName)){
                skuName = "默认";
            }
            erpGoodsSku.setSkuName(skuName);
            erpGoodsSku.setSkuCode(sku.getOuterId());
            erpGoodsSku.setColorImage(erpGoods.getImage());

            if(sku.getPrice()!=null){
                erpGoodsSku.setRetailPrice(BigDecimal.valueOf(sku.getPrice()/100));
            }
            erpGoodsSku.setStatus(1);
            goodsSkuMapper.insert(erpGoodsSku);

            // 初始化商品库存
            OGoodsInventory inventory = new OGoodsInventory();

            inventory.setGoodsId(erpGoods.getId());
            inventory.setGoodsNum(erpGoods.getGoodsNum());
            inventory.setGoodsName(erpGoods.getName());
            inventory.setGoodsImg(erpGoods.getImage());
            inventory.setSkuId(erpGoodsSku.getId());
            inventory.setSkuCode(erpGoodsSku.getSkuCode());
            inventory.setSkuName(erpGoodsSku.getSkuName());
            inventory.setQuantity(0L);
            inventory.setIsDelete(0);
            inventory.setCreateTime(new Date());
            inventory.setCreateBy("同步店铺商品初始化商品 sku 库存");
            inventoryMapper.insert(inventory);

            //更新ShopGoodsSku
            PddGoodsSku shopGoodsSkuUpdate = new PddGoodsSku();
            shopGoodsSkuUpdate.setId(sku.getId());
            shopGoodsSkuUpdate.setErpGoodsId(erpGoods.getId());
            shopGoodsSkuUpdate.setErpGoodsSkuId(erpGoodsSku.getId());
            skuMapper.updateById(shopGoodsSkuUpdate);

        }

        return ResultVo.success();
    }
}




