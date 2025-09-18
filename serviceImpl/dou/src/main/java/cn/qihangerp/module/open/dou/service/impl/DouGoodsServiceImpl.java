package cn.qihangerp.module.open.dou.service.impl;

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
import cn.qihangerp.model.entity.DouGoods;
import cn.qihangerp.model.entity.DouGoodsSku;
import cn.qihangerp.model.bo.DouGoodsBo;
import cn.qihangerp.module.open.dou.mapper.DouGoodsMapper;
import cn.qihangerp.module.open.dou.mapper.DouGoodsSkuMapper;
import cn.qihangerp.module.open.dou.service.DouGoodsService;
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
* @description 针对表【dou_goods(抖店商品表)】的数据库操作Service实现
* @createDate 2024-05-31 17:23:21
*/
@AllArgsConstructor
@Service
public class DouGoodsServiceImpl extends ServiceImpl<DouGoodsMapper, DouGoods>
    implements DouGoodsService {
    private final DouGoodsMapper mapper;
    private final DouGoodsSkuMapper skuMapper;
    private final OGoodsSkuMapper goodsSkuMapper;
    private final OGoodsMapper goodsMapper;
    private final OGoodsInventoryMapper inventoryMapper;

    @Override
    public PageResult<DouGoods> queryPageList(DouGoodsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DouGoods> queryWrapper = new LambdaQueryWrapper<DouGoods>()
                .eq(bo.getShopId()!=null,DouGoods::getShopId,bo.getShopId())

                ;

        Page<DouGoods> goodsPage = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(goodsPage.getRecords()!=null && goodsPage.getRecords().size()>0){
            for(DouGoods goods : goodsPage.getRecords()){
                goods.setSkuList(skuMapper.selectList(new LambdaQueryWrapper<DouGoodsSku>().eq(DouGoodsSku::getProductId,goods.getProductId())));
            }
        }
        return PageResult.build(goodsPage);
    }

    @Transactional
    @Override
    public ResultVo<Integer> saveGoods(Long shopId, DouGoods goods) {
        List<DouGoods> jdGoods = mapper.selectList(new LambdaQueryWrapper<DouGoods>().eq(DouGoods::getProductId, goods.getProductId()));
        if(jdGoods== null || jdGoods.isEmpty()){
            // 新增
            goods.setShopId(shopId);
            goods.setPullTime(new Date());
            mapper.insert(goods);
        }else{
            // 修改
            goods.setId(jdGoods.get(0).getId());
            goods.setModifyTime(new Date());

            mapper.updateById(goods);
            // 删除sku
//            skuMapper.delete(new LambdaQueryWrapper<DouGoodsSku>().eq(DouGoodsSku::getProductId,goods.getProductId()));
        }

        Long erpGoodsId=0L;
        String erpGoodsNum="";
        // 添加sku
        if(goods.getSkuList()!=null && !goods.getSkuList().isEmpty()){
            for (var item : goods.getSkuList()){
                item.setName(goods.getName());
                item.setImg(goods.getImg());
                item.setShopId(shopId);
                // 根据OuterId查找ERP系统中的skuid
                if(StringUtils.isNotEmpty(item.getCode())) {
                    List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, item.getCode()));
                    if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
                        erpGoodsId = oGoodsSkus.get(0).getGoodsId();
                        erpGoodsNum = oGoodsSkus.get(0).getGoodsNum();
                        item.setErpGoodsId(oGoodsSkus.get(0).getGoodsId().toString());
                        item.setErpGoodsSkuId(oGoodsSkus.get(0).getId().toString());
                    }
                }
                List<DouGoodsSku> pddGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<DouGoodsSku>().eq(DouGoodsSku::getSpecId, item.getSpecId()));
                if(pddGoodsSkus!=null && !pddGoodsSkus.isEmpty()){
                    item.setModifyTime(new Date());
                    skuMapper.updateById(item);
                }else {
                    item.setPullTime(new Date());
                    skuMapper.insert(item);
                }
            }
        }

        if(erpGoodsId>0){
            DouGoods updateGoods = new DouGoods();
            updateGoods.setId(goods.getId());
            updateGoods.setErpGoodsId(erpGoodsId);
            updateGoods.setOuterProductId(erpGoodsNum);
            mapper.updateById(goods);
        }

        return ResultVo.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo pushToOms(Long taoGoodsId) {
        DouGoods shopGoods = mapper.selectById(taoGoodsId);
        if(shopGoods==null) return ResultVo.error("店铺商品数据不存在");

        List<DouGoodsSku> shopGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<DouGoodsSku>().eq(DouGoodsSku::getProductId, shopGoods.getProductId()));
        if(shopGoodsSkus==null || shopGoodsSkus.isEmpty()) return ResultVo.error("店铺商品Sku数据不存在");

        String goodsNum ="";
        if(org.springframework.util.StringUtils.hasText(shopGoods.getOuterProductId())){
            goodsNum = shopGoods.getOuterProductId();
            // 用商家编码查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                // 存在=======关联
                //更新shopGoods
                DouGoods shopGoodsUpdate = new DouGoods();
                shopGoodsUpdate.setId(shopGoods.getId());
                shopGoodsUpdate.setErpGoodsId(erpGoodsList.get(0).getId());
                mapper.updateById(shopGoodsUpdate);

                List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>()
                        .eq(OGoodsSku::getGoodsId, erpGoodsList.get(0).getId())
                );
                //更新skus
                for (var sku:shopGoodsSkus){
                    if(org.springframework.util.StringUtils.hasText(sku.getCode())){
                        List<OGoodsSku> oGoodsSkuList = oGoodsSkus.stream().filter(x -> x.getSkuCode().equals(sku.getCode())).collect(Collectors.toList());
                        if(oGoodsSkuList!=null && !oGoodsSkuList.isEmpty()){
                            //更新ShopGoodsSku
                            DouGoodsSku shopGoodsSkuUpdate = new DouGoodsSku();
                            shopGoodsSkuUpdate.setId(sku.getId());
                            shopGoodsSkuUpdate.setErpGoodsId(oGoodsSkuList.get(0).getGoodsId().toString());
                            shopGoodsSkuUpdate.setErpGoodsSkuId(oGoodsSkuList.get(0).getId().toString());

                            skuMapper.updateById(shopGoodsSkuUpdate);
                        }
                    }
                }

                return ResultVo.success("商品已存在，更新关联");
            }

        }else {
            goodsNum = shopGoods.getProductId().toString();
            // 用商品ID查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                return ResultVo.error(ResultVoEnum.DataExist.getIndex(),"商品已存在");
            }
        }


        // 添加商品
        OGoods erpGoods = new OGoods();
        erpGoods.setName(shopGoods.getName());
        erpGoods.setImage(shopGoods.getImg());
        erpGoods.setGoodsNum(goodsNum);
        erpGoods.setCategoryId(0L);
        erpGoods.setRemark("DOU店铺商品同步");
        erpGoods.setStatus(1);
        erpGoods.setDisable(1);
        if (shopGoods.getMarketPrice() != null) {
            erpGoods.setRetailPrice(BigDecimal.valueOf(shopGoods.getMarketPrice()/100));
        }
        erpGoods.setCreateBy("DOU店铺商品同步");
        erpGoods.setCreateTime(new Date());
        goodsMapper.insert(erpGoods);

        //更新shopGoods
        DouGoods shopGoodsUpdate = new DouGoods();
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
            if(org.springframework.util.StringUtils.hasText(sku.getSpecDetailName1())) {
                colorValue=sku.getSpecDetailName1();
                colorLabel="颜色分类";
            }else{
                colorValue="默认";
                colorLabel="颜色分类";
            }
            if(org.springframework.util.StringUtils.hasText(sku.getSpecDetailName2())) {
                sizeValue=sku.getSpecDetailName2();
                sizeLabel="尺寸";
            }
            if(org.springframework.util.StringUtils.hasText(sku.getSpecDetailName3())) {
                styleValue=sku.getSpecDetailName3();
                styleLabel="款式";
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
            erpGoodsSku.setSkuCode(sku.getCode());
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
            DouGoodsSku shopGoodsSkuUpdate = new DouGoodsSku();
            shopGoodsSkuUpdate.setId(sku.getId());
            shopGoodsSkuUpdate.setErpGoodsId(erpGoods.getId().toString());
            shopGoodsSkuUpdate.setErpGoodsSkuId(erpGoodsSku.getId().toString());
            skuMapper.updateById(shopGoodsSkuUpdate);

        }

        return ResultVo.success();
    }
}




