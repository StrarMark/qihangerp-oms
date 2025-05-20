package cn.qihangerp.module.open.tao.service.impl;


import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.utils.StringUtils;
import cn.qihangerp.module.goods.domain.OGoods;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryMapper;
import cn.qihangerp.module.goods.mapper.OGoodsMapper;
import cn.qihangerp.module.goods.mapper.OGoodsSkuMapper;
import cn.qihangerp.module.open.tao.domain.TaoGoods;
import cn.qihangerp.module.open.tao.domain.TaoGoodsSku;
import cn.qihangerp.module.open.tao.domain.bo.TaoGoodsBo;
import cn.qihangerp.module.open.tao.mapper.TaoGoodsMapper;
import cn.qihangerp.module.open.tao.mapper.TaoGoodsSkuMapper;
import cn.qihangerp.module.open.tao.service.TaoGoodsService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
* @author TW
* @description 针对表【tao_goods】的数据库操作Service实现
* @createDate 2024-02-29 09:28:38
*/
@AllArgsConstructor
@Service
public class TaoGoodsServiceImpl extends ServiceImpl<TaoGoodsMapper, TaoGoods>
    implements TaoGoodsService {

    private final TaoGoodsMapper mapper;
    private final TaoGoodsSkuMapper skuMapper;
    private final OGoodsSkuMapper goodsSkuMapper;
    private final OGoodsMapper goodsMapper;
    private final OGoodsInventoryMapper inventoryMapper;

    @Override
    public PageResult<TaoGoods> queryPageList(TaoGoodsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TaoGoods> queryWrapper = new LambdaQueryWrapper<TaoGoods>()
                .eq(bo.getShopId()!=null,TaoGoods::getShopId,bo.getShopId());

        Page<TaoGoods> taoGoodsPage = mapper.selectPage(pageQuery.build(), queryWrapper);
        if(taoGoodsPage.getRecords()!=null && taoGoodsPage.getRecords().size()>0){
            for(TaoGoods taoGoods : taoGoodsPage.getRecords()){
                taoGoods.setSkus(skuMapper.selectList(new LambdaQueryWrapper<TaoGoodsSku>().eq(TaoGoodsSku::getTaoGoodsId,taoGoods.getId())));
            }
        }
        return PageResult.build(taoGoodsPage);
    }

    @Transactional
    @Override
    public int saveAndUpdateGoods(Long shopId, TaoGoods goods) {
        List<TaoGoods> goodsList = mapper.selectList(new LambdaQueryWrapper<TaoGoods>().eq(TaoGoods::getNumIid, goods.getNumIid()));
        if(goodsList!=null && goodsList.size()>0){
            // 存在，更新
            goods.setShopId(shopId);
            goods.setId(goodsList.get(0).getId());
            mapper.updateById(goods);

            // 删除sku
            skuMapper.delete(new LambdaQueryWrapper<TaoGoodsSku>().eq(TaoGoodsSku::getTaoGoodsId,goods.getId()));

            // 重新插入sku
            if(goods.getSkus()!=null) {
                for (var sku : goods.getSkus()) {
                    sku.setTaoGoodsId(goods.getId());
                    // 根据OuterId查找ERP系统中的skuid
                    if(StringUtils.isNotEmpty(sku.getOuterId())) {
                        List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, sku.getOuterId()));
                        if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
                            sku.setErpGoodsId(oGoodsSkus.get(0).getGoodsId());
                            sku.setErpGoodsSkuId(oGoodsSkus.get(0).getId());
                        }
                    }
                    skuMapper.insert(sku);
                }
            }
            return ResultVoEnum.DataExist.getIndex();
        }else {
            // 不存在，新增
            goods.setShopId(shopId);
            goods.setCreateTime(new Date());
            mapper.insert(goods);
            // 插入sku
            if(goods.getSkus()!=null) {
                for (var sku : goods.getSkus()) {
                    sku.setTaoGoodsId(goods.getId());
                    // 根据OuterId查找ERP系统中的skuid
                    if(StringUtils.isNotEmpty(sku.getOuterId())) {
                        List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, sku.getOuterId()));
                        if(oGoodsSkus!=null && !oGoodsSkus.isEmpty()){
                            sku.setErpGoodsId(oGoodsSkus.get(0).getGoodsId());
                            sku.setErpGoodsSkuId(oGoodsSkus.get(0).getId());
                        }
                    }

                    skuMapper.insert(sku);
                }
            }
            return 0;
        }
    }

    /**
     * 推送商品到商品库
     * @param taoGoodsId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo pushToOms(Long taoGoodsId) {
        TaoGoods shopGoods = mapper.selectById(taoGoodsId);
        if(shopGoods==null) return ResultVo.error("店铺商品数据不存在");

        List<TaoGoodsSku> shopGoodsSkus = skuMapper.selectList(new LambdaQueryWrapper<TaoGoodsSku>().eq(TaoGoodsSku::getTaoGoodsId, taoGoodsId));
        if(shopGoodsSkus==null || shopGoodsSkus.isEmpty()) return ResultVo.error("店铺商品Sku数据不存在");

        String goodsNum ="";
        if(org.springframework.util.StringUtils.hasText(shopGoods.getOuterId())){
            goodsNum = shopGoods.getOuterId();
            // 用商家编码查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                // 存在=======关联
                //更新shopGoods
                TaoGoods shopGoodsUpdate = new TaoGoods();
                shopGoodsUpdate.setId(shopGoods.getId());
                shopGoodsUpdate.setErpGoodsId(erpGoodsList.get(0).getId());
                mapper.updateById(shopGoodsUpdate);


                List<OGoodsSku> oGoodsSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<OGoodsSku>()
                        .eq(OGoodsSku::getGoodsId, erpGoodsList.get(0).getId())
                );
                //更新skus
                for (var sku:shopGoodsSkus){
                    //更新ShopGoodsSku
                    TaoGoodsSku shopGoodsSkuUpdate = new TaoGoodsSku();
                    shopGoodsSkuUpdate.setId(sku.getId());
                    shopGoodsSkuUpdate.setErpGoodsId(oGoodsSkus.get(0).getGoodsId());
                    shopGoodsSkuUpdate.setErpGoodsSkuId(oGoodsSkus.get(0).getId());
                    skuMapper.updateById(shopGoodsSkuUpdate);
                }
                return ResultVo.success("商家编码已存在!更新成功");
            }

        }else {
            goodsNum = shopGoods.getNumIid().toString();
            // 用商品ID查询
            List<OGoods> erpGoodsList = goodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                    .eq(OGoods::getGoodsNum, goodsNum));
            if(erpGoodsList!=null && !erpGoodsList.isEmpty()){
                return ResultVo.error(ResultVoEnum.DataExist.getIndex(),"商家编码已存在");
            }
        }



        // 添加商品
        OGoods erpGoods = new OGoods();

        erpGoods.setName(shopGoods.getTitle());
        erpGoods.setImage(shopGoods.getPicUrl());
        erpGoods.setGoodsNum(goodsNum);
        erpGoods.setCategoryId(0L);
        erpGoods.setRemark("TAO店铺商品同步");
        erpGoods.setStatus(1);
        erpGoods.setDisable(1);
        if (StringUtils.isNotEmpty(shopGoods.getPrice() )) {
            erpGoods.setRetailPrice(new BigDecimal(shopGoods.getPrice()));
        }
        erpGoods.setCreateBy("TAO店铺商品同步");
        erpGoods.setCreateTime(new Date());
        goodsMapper.insert(erpGoods);

        //更新shopGoods
        TaoGoods shopGoodsUpdate = new TaoGoods();
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
            // 规格数组，最多取3个
            String[] specArray = sku.getPropertiesName().split(";");
            List<String> specList = Arrays.stream(specArray).toList();
            List<String> specList1 = new ArrayList<>();

            for(int i=0;i<specList.size();i++){
                String[] specVal = specList.get(i).split(":");
                if(specVal[2].indexOf("颜色")>-1){
                    colorLabel = specVal[2];
                    colorValue = specVal[3];
                }else if(specVal[2].indexOf("尺寸")>-1){
                    sizeLabel = specVal[2];
                    sizeValue = specVal[3];
                }else{
                    specList1.add( specList.get(i));
                }
            }
            int index=0;
            for(int i=0;i<specList1.size();i++) {
                String[] specVal = specList.get(i).split(":");
                if (org.springframework.util.StringUtils.isEmpty(colorValue)) {
                    colorLabel = specVal[2];
                    colorValue = specVal[3];
                } else if (org.springframework.util.StringUtils.isEmpty(sizeValue)) {
                    sizeLabel = specVal[2];
                    sizeValue = specVal[3];
                } else if (org.springframework.util.StringUtils.isEmpty(styleValue)) {
                    styleLabel = specVal[2];
                    styleValue = specVal[3];
                }
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
                erpGoodsSku.setRetailPrice(BigDecimal.valueOf(sku.getPrice()));
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
            inventory.setCreateBy("同步TAO店铺商品初始化商品 sku 库存");
            inventoryMapper.insert(inventory);

            //更新ShopGoodsSku
            TaoGoodsSku shopGoodsSkuUpdate = new TaoGoodsSku();
            shopGoodsSkuUpdate.setId(sku.getId());
            shopGoodsSkuUpdate.setErpGoodsId(erpGoods.getId());
            shopGoodsSkuUpdate.setErpGoodsSkuId(erpGoodsSku.getId());
            skuMapper.updateById(shopGoodsSkuUpdate);
        }

        return ResultVo.success();
    }
}




