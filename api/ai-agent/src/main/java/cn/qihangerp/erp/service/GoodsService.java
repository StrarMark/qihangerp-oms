package cn.qihangerp.erp.service;

import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.module.service.OGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务类，用于AI查询商品信息
 * 调用已有的 OGoodsSkuService 进行数据库查询
 */
@Service
public class GoodsService {
    
    @Autowired
    private OGoodsSkuService oGoodsSkuService;
    
    /**
     * 根据ID查询商品SKU
     * @param id 商品SKU ID
     * @return 商品信息
     */
    public Goods getGoodsSkuById(Long id) {
        OGoodsSku sku = oGoodsSkuService.getById(id);
        if (sku == null) {
            return null;
        }
        return convertToGoods(sku);
    }
    
    /**
     * 根据商品名称模糊查询商品SKU
     * @param keyword 关键词
     * @return 商品SKU列表
     */
    public List<Goods> searchGoodsByName(String keyword) {
        LambdaQueryWrapper<OGoodsSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(OGoodsSku::getGoodsName, keyword)
               .or()
               .like(OGoodsSku::getSkuName, keyword);
        List<OGoodsSku> list = oGoodsSkuService.list(wrapper);
        return list.stream().map(this::convertToGoods).collect(Collectors.toList());
    }
    
    /**
     * 根据商品编码查询商品SKU
     * @param goodsNum 商品编码
     * @return 商品SKU列表
     */
    public List<Goods> searchGoodsByGoodsNum(String goodsNum) {
        LambdaQueryWrapper<OGoodsSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OGoodsSku::getGoodsNum, goodsNum);
        List<OGoodsSku> list = oGoodsSkuService.list(wrapper);
        return list.stream().map(this::convertToGoods).collect(Collectors.toList());
    }
    
    /**
     * 根据SKU编码查询商品SKU
     * @param skuCode SKU编码
     * @return 商品SKU信息
     */
    public Goods getGoodsSkuBySkuCode(String skuCode) {
        LambdaQueryWrapper<OGoodsSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OGoodsSku::getSkuCode, skuCode);
        OGoodsSku sku = oGoodsSkuService.getOne(wrapper);
        if (sku == null) {
            return null;
        }
        return convertToGoods(sku);
    }
    
    /**
     * 根据商品ID查询商品SKU
     * @param goodsId 商品ID
     * @return 商品SKU列表
     */
    public List<Goods> searchGoodsByGoodsId(Long goodsId) {
        LambdaQueryWrapper<OGoodsSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OGoodsSku::getGoodsId, goodsId);
        List<OGoodsSku> list = oGoodsSkuService.list(wrapper);
        return list.stream().map(this::convertToGoods).collect(Collectors.toList());
    }
    
    /**
     * 获取所有商品SKU
     * @return 商品SKU列表
     */
    public List<Goods> getAllGoods() {
        List<OGoodsSku> list = oGoodsSkuService.list();
        return list.stream().map(this::convertToGoods).collect(Collectors.toList());
    }
    
    /**
     * 通用搜索商品SKU
     * @param keyword 关键词（可搜索名称、编码、SKU编码等）
     * @return 商品SKU列表
     */
    public List<Goods> searchGoods(String keyword) {
        LambdaQueryWrapper<OGoodsSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(OGoodsSku::getGoodsName, keyword)
               .or().like(OGoodsSku::getGoodsNum, keyword)
               .or().like(OGoodsSku::getSkuCode, keyword)
               .or().like(OGoodsSku::getSkuName, keyword)
               .or().like(OGoodsSku::getBarCode, keyword);
        List<OGoodsSku> list = oGoodsSkuService.list(wrapper);
        return list.stream().map(this::convertToGoods).collect(Collectors.toList());
    }
    
    /**
     * 转换为商品对象
     */
    private Goods convertToGoods(OGoodsSku sku) {
        return new Goods(
            sku.getId(),
            sku.getGoodsId(),
            sku.getGoodsName(),
            sku.getGoodsNum(),
            sku.getSkuName(),
            sku.getSkuCode(),
            sku.getColorValue(),
            sku.getSizeValue(),
            sku.getStyleValue(),
            sku.getBarCode(),
            sku.getPurPrice(),
            sku.getRetailPrice(),
            sku.getUnitCost(),
            sku.getStatus(),
            sku.getLowQty(),
            sku.getHighQty()
        );
    }
    
    /**
     * 商品SKU实体类
     */
    public static class Goods {
        private Long id;
        private Long goodsId;
        private String goodsName;
        private String goodsNum;
        private String skuName;
        private String skuCode;
        private String colorValue;
        private String sizeValue;
        private String styleValue;
        private String barCode;
        private java.math.BigDecimal purPrice;
        private java.math.BigDecimal retailPrice;
        private java.math.BigDecimal unitCost;
        private Integer status;
        private Integer lowQty;
        private Integer highQty;
        
        public Goods(Long id, Long goodsId, String goodsName, String goodsNum, String skuName,
                    String skuCode, String colorValue, String sizeValue, String styleValue,
                    String barCode, java.math.BigDecimal purPrice, java.math.BigDecimal retailPrice,
                    java.math.BigDecimal unitCost, Integer status, Integer lowQty, Integer highQty) {
            this.id = id;
            this.goodsId = goodsId;
            this.goodsName = goodsName;
            this.goodsNum = goodsNum;
            this.skuName = skuName;
            this.skuCode = skuCode;
            this.colorValue = colorValue;
            this.sizeValue = sizeValue;
            this.styleValue = styleValue;
            this.barCode = barCode;
            this.purPrice = purPrice;
            this.retailPrice = retailPrice;
            this.unitCost = unitCost;
            this.status = status;
            this.lowQty = lowQty;
            this.highQty = highQty;
        }
        
        public Long getId() { return id; }
        public Long getGoodsId() { return goodsId; }
        public String getGoodsName() { return goodsName; }
        public String getGoodsNum() { return goodsNum; }
        public String getSkuName() { return skuName; }
        public String getSkuCode() { return skuCode; }
        public String getColorValue() { return colorValue; }
        public String getSizeValue() { return sizeValue; }
        public String getStyleValue() { return styleValue; }
        public String getBarCode() { return barCode; }
        public java.math.BigDecimal getPurPrice() { return purPrice; }
        public java.math.BigDecimal getRetailPrice() { return retailPrice; }
        public java.math.BigDecimal getUnitCost() { return unitCost; }
        public Integer getStatus() { return status; }
        public Integer getLowQty() { return lowQty; }
        public Integer getHighQty() { return highQty; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("商品ID: ").append(id).append(", ");
            sb.append("商品名称: ").append(goodsName != null ? goodsName : "无").append(", ");
            sb.append("商品编码: ").append(goodsNum != null ? goodsNum : "无").append(", ");
            sb.append("SKU名称: ").append(skuName != null ? skuName : "无").append(", ");
            sb.append("SKU编码: ").append(skuCode != null ? skuCode : "无").append(", ");
            if (colorValue != null) sb.append("颜色: ").append(colorValue).append(", ");
            if (sizeValue != null) sb.append("尺寸: ").append(sizeValue).append(", ");
            if (styleValue != null) sb.append("款式: ").append(styleValue).append(", ");
            if (barCode != null) sb.append("条码: ").append(barCode).append(", ");
            sb.append("采购价: ").append(purPrice).append(", ");
            sb.append("零售价: ").append(retailPrice).append(", ");
            sb.append("成本价: ").append(unitCost);
            return sb.toString();
        }
    }
}
