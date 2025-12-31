package cn.qihangerp.module.open.wei.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.utils.StringUtils;
import cn.qihangerp.model.entity.OGoods;
import cn.qihangerp.model.entity.OGoodsInventory;
import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryMapper;
import cn.qihangerp.module.goods.mapper.OGoodsMapper;
import cn.qihangerp.module.goods.mapper.OGoodsSkuMapper;
import cn.qihangerp.model.entity.WeiGoods;
import cn.qihangerp.model.entity.WeiGoodsSku;
import cn.qihangerp.module.open.wei.mapper.WeiGoodsMapper;
import cn.qihangerp.module.open.wei.mapper.WeiGoodsSkuMapper;
import cn.qihangerp.module.open.wei.service.WeiGoodsService;
import com.alibaba.fastjson2.JSONArray;
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
* @author qilip
* @description 针对表【oms_wei_goods】的数据库操作Service实现
* @createDate 2024-09-21 15:09:54
*/
@AllArgsConstructor
@Service
public class WeiGoodsServiceImpl extends ServiceImpl<WeiGoodsMapper, WeiGoods>
    implements WeiGoodsService {
    private final WeiGoodsMapper mapper;
    private final WeiGoodsSkuMapper skuMapper;
    private final OGoodsSkuMapper goodsSkuMapper;
    private final OGoodsMapper goodsMapper;
    private final OGoodsInventoryMapper inventoryMapper;

    @Override
    public PageResult<WeiGoods> queryPageList(WeiGoods bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WeiGoods> queryWrapper = new LambdaQueryWrapper<WeiGoods>()
                .eq(bo.getShopId()!=null,WeiGoods::getShopId,bo.getShopId())
                ;

        Page<WeiGoods> page = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(page.getRecords()!=null&&page.getRecords().size()>0){
            for(WeiGoods goods : page.getRecords()){
                goods.setSkuList(skuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getProductId,goods.getProductId())));
            }
        }
        return PageResult.build(page);
    }

    @Override
    public int saveAndUpdateGoods(Long shopId, WeiGoods goods) {
        List<WeiGoods> goodsList = mapper.selectList(new LambdaQueryWrapper<WeiGoods>().eq(WeiGoods::getProductId, goods.getProductId()));
        if (goodsList != null && goodsList.size() > 0) {
            // 更新
            // 存在，更新
            goods.setShopId(shopId);
            goods.setId(goodsList.get(0).getId());
            goods.setUpdateTime(new Date());
            mapper.updateById(goods);

            // 删除sku
//            skuMapper.delete(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getProductId,goods.getProductId()));
            // 重新插入sku
//            if(goods.getSkus()!=null) {
//                for (var sku : goods.getSkus()) {
//                    sku.setTitle(goods.getTitle());
//                    sku.setShopId(shopId);
//                    // 根据OuterId查找ERP系统中的skuid
////                    if(StringUtils.isNotEmpty(sku.getSkuCode())) {
////                        List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuNum, sku.getSkuCode()));
////                        if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
////                            sku.setErpGoodsId(oGoodsSkus.get(0).getErpGoodsId());
////                            sku.setErpGoodsSkuId(oGoodsSkus.get(0).getErpSkuId());
////                        }
////                    }
//                    skuMapper.insert(sku);
//                }
//            }

//            return ResultVoEnum.DataExist.getIndex();
        } else {
            // 不存在，新增return 0;
            // 不存在，新增
            goods.setCreateTime(new Date());
            goods.setShopId(shopId);
            mapper.insert(goods);
        }
        Long erpGoodsId=0L;
        String erpGoodsNum="";
        // 插入sku
        if(goods.getSkuList()!=null) {
            for (var sku : goods.getSkuList()) {
                sku.setShopId(shopId);
                sku.setTitle(goods.getTitle());
//                    sku.setWeiGoodsId(Long.parseLong(goods.getId()));
                // 根据OuterId查找ERP系统中的skuid
                // 根据OuterId查找ERP系统中的skuid
                if(StringUtils.isNotEmpty(sku.getOutSkuId())) {
                    List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, sku.getOutSkuId()));
                    if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
                        erpGoodsId = oGoodsSkus.get(0).getGoodsId();
                        erpGoodsNum = oGoodsSkus.get(0).getGoodsNum();
                        sku.setErpGoodsId(oGoodsSkus.get(0).getGoodsId());
                        sku.setErpGoodsSkuId(oGoodsSkus.get(0).getId());
                    }
                }
                List<WeiGoodsSku> pddGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getSkuId, sku.getSkuId()));
                if(pddGoodsSkus!=null && !pddGoodsSkus.isEmpty()){
                    sku.setUpdateTime(new Date());
                    skuMapper.updateById(sku);
                }else {
                    sku.setCreateTime(new Date());
                    skuMapper.insert(sku);
                }

            }
        }
        if(erpGoodsId>0){
            WeiGoods updateGoods = new WeiGoods();
            updateGoods.setId(goods.getId());
            updateGoods.setErpGoodsId(erpGoodsId);
            updateGoods.setOutProductId(erpGoodsNum);
            mapper.updateById(goods);
        }


        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo pushToOms(Long taoGoodsId) {
        WeiGoods shopGoods = mapper.selectById(taoGoodsId);
        if(shopGoods==null) return ResultVo.error("店铺商品数据不存在");

        List<WeiGoodsSku> shopGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<WeiGoodsSku>().eq(WeiGoodsSku::getProductId, shopGoods.getProductId()));
        if(shopGoodsSkus==null || shopGoodsSkus.isEmpty()) return ResultVo.error("店铺商品Sku数据不存在");

        String goodsNum ="";
        if(org.springframework.util.StringUtils.hasText(shopGoods.getOutProductId())){
            goodsNum = shopGoods.getOutProductId();
            // 用商家编码查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                // 存在=======关联
                //更新shopGoods
                WeiGoods shopGoodsUpdate = new WeiGoods();
                shopGoodsUpdate.setId(shopGoods.getId());
                shopGoodsUpdate.setErpGoodsId(erpGoodsList.get(0).getId());
                mapper.updateById(shopGoodsUpdate);

                List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>()
                        .eq(OGoodsSku::getGoodsId, erpGoodsList.get(0).getId())
                );
                //更新skus
                for (var sku:shopGoodsSkus){
                    if(org.springframework.util.StringUtils.hasText(sku.getOutSkuId())){
                        List<OGoodsSku> oGoodsSkuList = oGoodsSkus.stream().filter(x -> x.getSkuCode().equals(sku.getOutSkuId())).collect(Collectors.toList());
                        if(oGoodsSkuList!=null && !oGoodsSkuList.isEmpty()){
                            //更新ShopGoodsSku
                            WeiGoodsSku shopGoodsSkuUpdate = new WeiGoodsSku();
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
            goodsNum = shopGoods.getProductId();
            // 用商品ID查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                return ResultVo.error(ResultVoEnum.DataExist.getIndex(),"商品已存在");
            }
        }


        // 添加商品
        OGoods erpGoods = new OGoods();
        erpGoods.setName(shopGoods.getTitle());
        erpGoods.setImage(shopGoods.getHeadImg());
        erpGoods.setGoodsNum(goodsNum);
        erpGoods.setCategoryId(0L);
        erpGoods.setRemark("WEI店铺商品同步");
        erpGoods.setStatus(1);
        erpGoods.setDisable(1);
        if (shopGoods.getMinPrice() != null) {
            erpGoods.setRetailPrice(BigDecimal.valueOf(shopGoods.getMinPrice()/100));
        }
        erpGoods.setCreateBy("WEI店铺商品同步");
        erpGoods.setCreateTime(new Date());
        goodsMapper.insert(erpGoods);

        //更新shopGoods
        WeiGoods shopGoodsUpdate = new WeiGoods();
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
            if(org.springframework.util.StringUtils.hasText(sku.getSkuAttrs())) {
                // [{"attr_key":"净含量","attr_value":"买2斤送1斤到手3斤"}]
                // 解析 JSON 字符串
                JSONArray jsonArray = JSONArray.parseArray(sku.getSkuAttrs());

                // 提取 attrValueAlias
                for (int i = 0; i < jsonArray.size(); i++) {
                    if(StringUtils.isNotEmpty(colorValue)){
                        colorLabel = jsonArray.getJSONObject(i).getString("attr_key");
                        colorValue = jsonArray.getJSONObject(i).getString("attr_value");
                    }else if(StringUtils.isNotEmpty(sizeValue)){
                        sizeLabel = jsonArray.getJSONObject(i).getString("attr_key");
                        sizeValue = jsonArray.getJSONObject(i).getString("attr_value");
                    }else if(StringUtils.isNotEmpty(styleValue)){
                        styleLabel = jsonArray.getJSONObject(i).getString("attr_key");
                        styleValue = jsonArray.getJSONObject(i).getString("attr_value");
                    }

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
            erpGoodsSku.setSkuCode(sku.getOutSkuId());
            erpGoodsSku.setColorImage(erpGoods.getImage());

            if(sku.getSalePrice()!=null){
                erpGoodsSku.setRetailPrice(BigDecimal.valueOf(sku.getSalePrice()/100));
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
            WeiGoodsSku shopGoodsSkuUpdate = new WeiGoodsSku();
            shopGoodsSkuUpdate.setId(sku.getId());
            shopGoodsSkuUpdate.setErpGoodsId(erpGoods.getId());
            shopGoodsSkuUpdate.setErpGoodsSkuId(erpGoodsSku.getId());
            skuMapper.updateById(shopGoodsSkuUpdate);

        }

        return ResultVo.success();
    }
}




