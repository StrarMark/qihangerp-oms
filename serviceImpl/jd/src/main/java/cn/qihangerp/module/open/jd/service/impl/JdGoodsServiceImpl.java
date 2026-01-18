package cn.qihangerp.module.open.jd.service.impl;


import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.model.entity.OGoods;
import cn.qihangerp.model.entity.OGoodsInventory;
import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.mapper.OGoodsInventoryMapper;
import cn.qihangerp.mapper.OGoodsMapper;
import cn.qihangerp.mapper.OGoodsSkuMapper;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.utils.StringUtils;
import cn.qihangerp.model.entity.JdGoods;
import cn.qihangerp.model.entity.JdGoodsSku;

import cn.qihangerp.model.bo.JdGoodsBo;
import cn.qihangerp.module.open.jd.mapper.JdGoodsSkuMapper;

import cn.qihangerp.module.open.jd.service.JdGoodsService;
import cn.qihangerp.module.open.jd.mapper.JdGoodsMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author qilip
* @description 针对表【jd_goods】的数据库操作Service实现
* @createDate 2024-03-09 11:29:59
*/
@AllArgsConstructor
@Service
public class JdGoodsServiceImpl extends ServiceImpl<JdGoodsMapper, JdGoods>
    implements JdGoodsService{
    private final JdGoodsMapper mapper;
    private final JdGoodsSkuMapper skuMapper;
    private final OGoodsSkuMapper goodsSkuMapper;
    private final OGoodsMapper goodsMapper;
    private final OGoodsInventoryMapper inventoryMapper;

    @Override
    public PageResult<JdGoods> queryPageList(JdGoodsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<JdGoods> queryWrapper = new LambdaQueryWrapper<JdGoods>()
                .eq(bo.getShopId()!=null,JdGoods::getShopId,bo.getShopId());

        Page<JdGoods> goodsPage = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(goodsPage.getRecords()!=null && goodsPage.getRecords().size()>0){
            for(JdGoods goods : goodsPage.getRecords()){
                goods.setSkuList(skuMapper.selectList(new LambdaQueryWrapper<JdGoodsSku>().eq(JdGoodsSku::getWareId,goods.getWareId())));
            }
        }
        return PageResult.build(goodsPage);
    }

    @Transactional
    @Override
    public ResultVo<Integer> saveGoods(Long shopId, JdGoods goods) {
        List<JdGoods> jdGoods = mapper.selectList(new LambdaQueryWrapper<JdGoods>().eq(JdGoods::getWareId, goods.getWareId()));
        if(jdGoods== null || jdGoods.isEmpty()){
            // 新增
            goods.setCreateTime(new Date());
            goods.setShopId(shopId);
            mapper.insert(goods);
        }else{
            // 修改
            goods.setId(jdGoods.get(0).getId());
            goods.setShopId(shopId);
            goods.setUpdateTime(new Date());
            mapper.updateById(goods);
            // 删除sku
//            skuMapper.delete(new LambdaQueryWrapper<JdGoodsSku>().eq(JdGoodsSku::getWareId,goods.getWareId()));
        }

        Long erpGoodsId=0L;
        String erpGoodsNum="";
        // 添加sku
        if(goods.getSkuList()!=null && !goods.getSkuList().isEmpty()){
            for (var item : goods.getSkuList()){

                item.setShopId(shopId);
//                item.setGoodsId(goods.getId());
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
                List<JdGoodsSku> jdGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<JdGoodsSku>().eq(JdGoodsSku::getSkuId, item.getSkuId()));
                if(jdGoodsSkus!=null && jdGoodsSkus.size()>0){
                    // 存在更新
                    item.setUpdateTime(new Date());
                    skuMapper.updateById(item);
                }else {
                    // 新增
                    item.setCreateTime(new Date());
                    skuMapper.insert(item);
                }
            }
        }
        if(erpGoodsId>0){
            JdGoods jdGoodsUpdate = new JdGoods();
            jdGoodsUpdate.setId(goods.getId());
            jdGoodsUpdate.setItemNum(erpGoodsNum);
            jdGoodsUpdate.setErpGoodsId(erpGoodsId);
            mapper.updateById(jdGoodsUpdate);
        }

        return ResultVo.success();
    }

    @Transactional
    @Override
    public ResultVo<Integer> saveGoodsSku(Long shopId, JdGoodsSku goodsSku) {
        List<JdGoodsSku> jdGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<JdGoodsSku>().eq(JdGoodsSku::getSkuId, goodsSku.getSkuId()));
        if(jdGoodsSkus== null || jdGoodsSkus.isEmpty()){
            // 新增
            goodsSku.setShopId(shopId);
            // 根据OuterId查找ERP系统中的skuid
            if(StringUtils.isNotEmpty(goodsSku.getOuterId())) {
                List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, goodsSku.getOuterId()));
                if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
//                    erpGoodsId = oGoodsSkus.get(0).getGoodsId();
//                    erpGoodsNum = oGoodsSkus.get(0).getGoodsNum();
                    goodsSku.setErpGoodsId(oGoodsSkus.get(0).getGoodsId());
                    goodsSku.setErpGoodsSkuId(oGoodsSkus.get(0).getId());
                }
            }
            skuMapper.insert(goodsSku);
        }else{
            // 修改
            goodsSku.setId(jdGoodsSkus.get(0).getId());
            // 根据OuterId查找ERP系统中的skuid
            if(StringUtils.isNotEmpty(goodsSku.getOuterId())) {
                List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, goodsSku.getOuterId()));
                if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
//                    erpGoodsId = oGoodsSkus.get(0).getGoodsId();
//                    erpGoodsNum = oGoodsSkus.get(0).getGoodsNum();
                    goodsSku.setErpGoodsId(oGoodsSkus.get(0).getGoodsId());
                    goodsSku.setErpGoodsSkuId(oGoodsSkus.get(0).getId());
                }
            }
            goodsSku.setShopId(shopId);
            skuMapper.updateById(goodsSku);
        }
        return ResultVo.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo pushToOms(Long taoGoodsId) {
        JdGoods shopGoods = mapper.selectById(taoGoodsId);
        if(shopGoods==null) return ResultVo.error("店铺商品数据不存在");

        List<JdGoodsSku> shopGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<JdGoodsSku>().eq(JdGoodsSku::getWareId, shopGoods.getWareId()));
        if(shopGoodsSkus==null || shopGoodsSkus.isEmpty()) return ResultVo.error("店铺商品Sku数据不存在");

        String goodsNum ="";
        if(org.springframework.util.StringUtils.hasText(shopGoods.getItemNum())){
            goodsNum = shopGoods.getItemNum();
            // 用商家编码查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                // 存在=======关联
                //更新shopGoods
                JdGoods shopGoodsUpdate = new JdGoods();
                shopGoodsUpdate.setId(shopGoods.getId());
                shopGoodsUpdate.setLogo(erpGoodsList.get(0).getImage());
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
                            JdGoodsSku shopGoodsSkuUpdate = new JdGoodsSku();
                            shopGoodsSkuUpdate.setId(sku.getId());
                            shopGoodsSkuUpdate.setErpGoodsId(oGoodsSkuList.get(0).getGoodsId());
                            shopGoodsSkuUpdate.setErpGoodsSkuId(oGoodsSkuList.get(0).getId());
                            shopGoodsSkuUpdate.setLogo(oGoodsSkuList.get(0).getColorImage());
                            skuMapper.updateById(shopGoodsSkuUpdate);
                        }
                    }
                }

                return ResultVo.success("商品已存在，更新关联");
            }

        }else {
            goodsNum = shopGoods.getWareId().toString();
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
        erpGoods.setImage(shopGoods.getLogo());
        erpGoods.setGoodsNum(goodsNum);
        erpGoods.setCategoryId(0L);
        erpGoods.setRemark("JD店铺商品同步");
        erpGoods.setStatus(1);
        erpGoods.setDisable(1);
        if (shopGoods.getJdPrice() != null) {
            erpGoods.setRetailPrice(shopGoods.getJdPrice());
        }
        erpGoods.setCreateBy("JD店铺商品同步");
        erpGoods.setCreateTime(new Date());
        goodsMapper.insert(erpGoods);

        //更新shopGoods
        JdGoods shopGoodsUpdate = new JdGoods();
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
            if(org.springframework.util.StringUtils.hasText(sku.getSaleAttrs())) {
                // 解析 JSON 字符串
                JSONArray jsonArray = JSONArray.parseArray(sku.getSaleAttrs());
                // 提取 attrValueAlias
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    JSONArray attrValueAlias = item.getJSONArray("attrValueAlias");
                    String val="";
                    try {
                        JSONArray jarr =JSONArray.parseArray(attrValueAlias.getString(0));
                        for (int j = 0; j < jarr.size(); j++) {
                            val += jarr.getJSONObject(j).getString("value");
                            if(jarr.getJSONObject(j).containsKey("unit")){
                                val+= jarr.getJSONObject(j).getString("unit");
                            }
                        }
                    }catch (Exception e){
                        val = attrValueAlias.getString(0);
                    }
                    if (item.getInteger("index") == 1) {
                        colorLabel = "颜色分类";
                        colorValue = val;
                    } else if (item.getInteger("index") == 2) {
                        sizeLabel = "尺寸";
                        sizeValue =val;
                    }else if (item.getInteger("index") == 3) {
                        styleLabel = "款式";
                        styleValue = val;
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
            erpGoodsSku.setSkuCode(sku.getOuterId());
            erpGoodsSku.setColorImage(erpGoods.getImage());

            if(sku.getJdPrice()!=null){
                erpGoodsSku.setRetailPrice(sku.getJdPrice());
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
            JdGoodsSku shopGoodsSkuUpdate = new JdGoodsSku();
            shopGoodsSkuUpdate.setId(sku.getId());
            shopGoodsSkuUpdate.setErpGoodsId(erpGoods.getId());
            shopGoodsSkuUpdate.setErpGoodsSkuId(erpGoodsSku.getId());
            skuMapper.updateById(shopGoodsSkuUpdate);

        }

        return ResultVo.success();
    }
}




