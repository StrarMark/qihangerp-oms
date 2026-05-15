package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.bo.BindErpGoodsSkuBo;
import cn.qihangerp.model.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.model.query.ShopGoodsSkuBo;
import cn.qihangerp.mapper.OGoodsSkuMapper;
import cn.qihangerp.mapper.ShopGoodsMapper;
import cn.qihangerp.mapper.ShopGoodsSkuMapper;
import cn.qihangerp.mapper.ShopGoodsSkuShipItemMapper;
import cn.qihangerp.service.ShopGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shop_goods_sku(其他渠道店铺商品SKU)】的数据库操作Service实现
* @createDate 2025-07-15 08:29:21
*/
@Slf4j
@AllArgsConstructor
@Service
public class ShopGoodsSkuServiceImpl extends ServiceImpl<ShopGoodsSkuMapper, ShopGoodsSku>
    implements ShopGoodsSkuService {
    private final ShopGoodsMapper shopGoodsMapper;
    private final OGoodsSkuMapper oGoodsSkuMapper;
    private final ShopGoodsSkuShipItemMapper shopGoodsSkuShipItemMapper;
//    private final ShopGoodsSkuMappingService shopGoodsSkuMappingService;
    @Override
    public PageResult<ShopGoodsSku> queryPageList(ShopGoodsSkuBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopGoodsSku> queryWrapper = new LambdaQueryWrapper<ShopGoodsSku>()
                .eq(StringUtils.hasText(bo.getProductId()),ShopGoodsSku::getProductId,bo.getProductId())
                .eq(StringUtils.hasText(bo.getOuterProductId()),ShopGoodsSku::getOuterProductId,bo.getOuterProductId())
                .eq(StringUtils.hasText(bo.getOuterSkuId()),ShopGoodsSku::getOuterSkuId,bo.getOuterSkuId())
                .eq(StringUtils.hasText(bo.getSkuId()),ShopGoodsSku::getSkuId,bo.getSkuId())
                .eq(StringUtils.hasText(bo.getSkuCode()),ShopGoodsSku::getSkuCode,bo.getSkuCode())
                .eq(bo.getShopId()!=null,ShopGoodsSku::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,ShopGoodsSku::getShopType,bo.getShopType())
                .eq(bo.getMerchantId()!=null,ShopGoodsSku::getMerchantId,bo.getMerchantId())
                .eq(bo.getGoodsId()!=null,ShopGoodsSku::getShopGoodsId,bo.getGoodsId())
                .eq(bo.getHasLink()!=null&&bo.getHasLink()==0,ShopGoodsSku::getErpGoodsSkuId,0)
                .gt(bo.getHasLink()!=null&&bo.getHasLink()==1,ShopGoodsSku::getErpGoodsSkuId,0)
                ;

        Page<ShopGoodsSku> goodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(goodsPage);
    }

    @Override
    public PageResult<ShopGoodsSku> queryBenShuPageList(ShopGoodsSkuBo bo, PageQuery pageQuery) {
        try {
            pageQuery.setOrderByColumn("bs.goods_id");
            pageQuery.setIsAsc("desc");
            Page<ShopGoodsSku> pages = this.baseMapper.selectBenshuPageVo(pageQuery.build(),bo);
            return PageResult.build(pages);
        }catch (Exception e){
            e.printStackTrace();
            log.error("=========查询本数线下店铺库存出错：{}",e.getMessage());
            return PageResult.build(new Page<>(0,0));
        }

    }

    @Override
    public List<ShopGoodsSku> querySkuList(Long shopGoodsId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<ShopGoodsSku>().eq(ShopGoodsSku::getShopGoodsId,shopGoodsId));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo) {
        ShopGoodsSku shopGoodsSku = this.baseMapper.selectById(bo.getId());
        if(shopGoodsSku == null) {
            return ResultVo.error("店铺商品sku数据不存在");
        }
        ShopGoods shopGoods = shopGoodsMapper.selectById(shopGoodsSku.getShopGoodsId());
        if(shopGoods==null){
            return ResultVo.error("店铺商品数据不存在");
        }


        if(bo.getErpGoodsSkuId()==0){
            // 取消原来的关联
            ShopGoodsSku sku = new ShopGoodsSku();
            sku.setId(Long.parseLong(bo.getId()));
            sku.setErpGoodsId(0l);
            sku.setErpGoodsSkuId(0l);
            this.baseMapper.updateById(sku);

            ShopGoods goodsUp=new ShopGoods();
            goodsUp.setId(shopGoods.getId());
            goodsUp.setErpGoodsId(0L);
            shopGoodsMapper.updateById(goodsUp);
            // 删除原来的绑定关系
//            shopGoodsSkuMappingService.deleteByPlatformSkuId(shopGoodsSku.getSkuId().toString(),shopGoodsSku.getShopId());

            return ResultVo.success();
        }

        OGoodsSku oGoodsSku = oGoodsSkuMapper.selectById(bo.getErpGoodsSkuId());
        if(oGoodsSku == null) return ResultVo.error("未找到系统商品sku");

        Long erpGoodsId =Long.parseLong(oGoodsSku.getGoodsId());
        Long erpGoodsSkuId = Long.parseLong(oGoodsSku.getId());

        ShopGoodsSku sku = new ShopGoodsSku();
        sku.setId(Long.parseLong(bo.getId()));
        sku.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        sku.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
        this.baseMapper.updateById(sku);

        ShopGoods goodsUp=new ShopGoods();
        goodsUp.setId(shopGoods.getId());
        goodsUp.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        shopGoodsMapper.updateById(goodsUp);

        // 添加绑定关联
//        ShopGoodsSkuMapping shopGoodsSkuMapping = shopGoodsSkuMappingService.selectByPlatformSkuId(shopGoodsSku.getSkuId().toString(),shopGoodsSku.getShopId());
//        if (shopGoodsSkuMapping != null) {
//            // 更新
//            shopGoodsSkuMapping.setErpGoodsSkuId(erpGoodsSkuId);
//            shopGoodsSkuMapping.setErpGoodsId(erpGoodsId);
//            shopGoodsSkuMapping.setPlatformProductId(shopGoodsSku.getProductId());
//            shopGoodsSkuMapping.setPlatformSkuId(shopGoodsSku.getSkuId());
//            shopGoodsSkuMapping.setShopGoodsId(shopGoods.getId());
//            shopGoodsSkuMapping.setShopGoodsSkuId(shopGoodsSku.getId());
//            shopGoodsSkuMapping.setModifyOn(new Date());
//            shopGoodsSkuMappingService.updateById(shopGoodsSkuMapping);
//        } else {
//            // 新增
//            shopGoodsSkuMapping = new ShopGoodsSkuMapping();
//            shopGoodsSkuMapping.setErpGoodsId(erpGoodsId);
//            shopGoodsSkuMapping.setErpGoodsSkuId(erpGoodsSkuId);
//            shopGoodsSkuMapping.setCreateOn(new Date());
//            shopGoodsSkuMapping.setShopId(shopGoodsSku.getShopId());
//            shopGoodsSkuMapping.setShopType(shopGoodsSku.getShopType());
//            shopGoodsSkuMapping.setPlatformProductId(shopGoodsSku.getProductId());
//            shopGoodsSkuMapping.setPlatformSkuId(shopGoodsSku.getSkuId());
//            shopGoodsSkuMapping.setShopGoodsId(shopGoods.getId());
//            shopGoodsSkuMapping.setShopGoodsSkuId(shopGoodsSku.getId());
//            shopGoodsSkuMappingService.save(shopGoodsSkuMapping);
//        }

        return ResultVo.success();

    }

    @Override
    public ShopGoodsSku selectByPlatformSkuId(String platformSkuId,Long shopId) {
        ShopGoodsSku shopGoodsSkuMapping = this.baseMapper.selectOne(new LambdaQueryWrapper<ShopGoodsSku>()
                .eq(ShopGoodsSku::getSkuId, platformSkuId)
                .eq(ShopGoodsSku::getShopId, shopId));

        return shopGoodsSkuMapping;
    }

    /**
     * 绑定虚拟商品发货实物商品
     * @param bo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo bindErpGoodsSku(BindErpGoodsSkuBo bo) {
        ShopGoodsSku shopGoodsSku = this.baseMapper.selectById(bo.getId());
        if(shopGoodsSku!=null){
            // 绑定sku（正常都是绑定sku）
            //1 删除
            shopGoodsSkuShipItemMapper.delete(new LambdaQueryWrapper<ShopGoodsSkuShipItem>
                    ().eq(ShopGoodsSkuShipItem::getShopGoodsSkuId,shopGoodsSku.getId()));
            //2 添加新的
            for (var sku:bo.getSkuList()){
                ShopGoodsSkuShipItem shipItem = new ShopGoodsSkuShipItem();
                shipItem.setShopGoodsId(shopGoodsSku.getShopGoodsId());
                shipItem.setShopGoodsSkuId(shopGoodsSku.getId());
                shipItem.setProductId(shopGoodsSku.getProductId());
                shipItem.setProductSkuId(shopGoodsSku.getSkuId());
                shipItem.setGoodsName(sku.getGoodsName());
                shipItem.setGoodsNum(sku.getGoodsNum());
                shipItem.setSkuCode(sku.getSkuCode());
                shipItem.setSkuName(sku.getSkuName());
                shipItem.setImg(sku.getColorImage());
                shipItem.setErpGoodsId(Long.parseLong(sku.getGoodsId()));
                shipItem.setErpGoodsSkuId(Long.parseLong(sku.getId()));
                shipItem.setCreateOn(new Date());
                shipItem.setQuantity(sku.getQuantity());
                shopGoodsSkuShipItemMapper.insert(shipItem);
            }
            // 3 修改shopGoodsSku表 bind_ship_sku
            ShopGoodsSku update = new ShopGoodsSku();
            update.setId(shopGoodsSku.getId());
            update.setBindShipSku(1);
            update.setUpdateOn(new Date());
            this.baseMapper.updateById(update);
        }else{
            // 绑定goods（螳螂系统专用）
            //1 删除
            shopGoodsSkuShipItemMapper.delete(new LambdaQueryWrapper<ShopGoodsSkuShipItem>().eq(ShopGoodsSkuShipItem::getShopGoodsSkuId,bo.getId()));
            //2 添加新的
            ShopGoods shopGoods = shopGoodsMapper.selectById(bo.getId());
            if(shopGoods!=null){
                for (var sku:bo.getSkuList()){
                    ShopGoodsSkuShipItem shipItem = new ShopGoodsSkuShipItem();
                    shipItem.setShopGoodsId(shopGoods.getId());
                    shipItem.setShopGoodsSkuId(shopGoods.getId());
                    shipItem.setProductId(shopGoods.getProductId());
                    shipItem.setProductSkuId(shopGoods.getProductId());
                    shipItem.setGoodsName(sku.getGoodsName());
                    shipItem.setGoodsNum(sku.getGoodsNum());
                    shipItem.setSkuCode(sku.getSkuCode());
                    shipItem.setSkuName(sku.getSkuName());
                    shipItem.setImg(sku.getColorImage());
                    shipItem.setErpGoodsId(Long.parseLong(sku.getGoodsId()));
                    shipItem.setErpGoodsSkuId(Long.parseLong(sku.getId()));
                    shipItem.setCreateOn(new Date());
                    shipItem.setQuantity(sku.getQuantity());
                    shopGoodsSkuShipItemMapper.insert(shipItem);
                }
                // 3 修改shopGoods表 bind_ship_sku
                ShopGoods update = new ShopGoods();
                update.setId(Long.parseLong(bo.getId()));
                update.setBindShipSku(1);
                update.setUpdateOn(new Date());
                shopGoodsMapper.updateById(update);
            }
        }
        return ResultVo.success();
    }
}




