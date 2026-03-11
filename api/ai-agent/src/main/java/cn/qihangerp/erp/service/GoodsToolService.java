package cn.qihangerp.erp.service;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商品工具服务，用于AI查询商品信息
 */
@Component
public class GoodsToolService {
    
    @Autowired
    private GoodsService goodsService;
    
    /**
     * 根据商品SKU ID查询商品信息
     * @param id 商品SKU ID
     * @return 商品信息
     */
    @Tool("根据商品SKU ID查询商品信息")
    public String getGoodsById(String id) {
        try {
            Long skuId = Long.parseLong(id);
            GoodsService.Goods goods = goodsService.getGoodsSkuById(skuId);
            if (goods == null) {
                return "未找到ID为" + id + "的商品";
            }
            return goods.toString().replace("\n", "<br>");
        } catch (NumberFormatException e) {
            return "无效的商品ID格式：" + id;
        }
    }
    
    /**
     * 根据商品名称搜索商品
     * @param keyword 商品名称关键词
     * @return 商品列表
     */
    @Tool("根据商品名称搜索商品信息")
    public String searchGoodsByName(String keyword) {
        List<GoodsService.Goods> goodsList = goodsService.searchGoodsByName(keyword);
        if (goodsList.isEmpty()) {
            return "未找到名称包含'" + keyword + "'的商品";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("找到").append(goodsList.size()).append("个商品：<br>");
        for (GoodsService.Goods goods : goodsList) {
            sb.append(goods.toString()).append("<br><br>");
        }
        return sb.toString();
    }
    
    /**
     * 根据商品编码搜索商品
     * @param goodsNum 商品编码
     * @return 商品列表
     */
    @Tool("根据商品编码查询商品信息")
    public String searchGoodsByGoodsNum(String goodsNum) {
        List<GoodsService.Goods> goodsList = goodsService.searchGoodsByGoodsNum(goodsNum);
        if (goodsList.isEmpty()) {
            return "未找到编码为'" + goodsNum + "'的商品";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("找到").append(goodsList.size()).append("个商品：<br>");
        for (GoodsService.Goods goods : goodsList) {
            sb.append(goods.toString()).append("<br><br>");
        }
        return sb.toString();
    }
    
    /**
     * 根据SKU编码查询商品
     * @param skuCode SKU编码
     * @return 商品信息
     */
    @Tool("根据SKU编码查询商品信息")
    public String getGoodsBySkuCode(String skuCode) {
        GoodsService.Goods goods = goodsService.getGoodsSkuBySkuCode(skuCode);
        if (goods == null) {
            return "未找到SKU编码为'" + skuCode + "'的商品";
        }
        return goods.toString().replace("\n", "<br>");
    }
    
    /**
     * 通用搜索商品（支持名称、编码、SKU编码、条码等）
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    @Tool("通用搜索商品信息，支持名称、编码、SKU编码、条码搜索")
    public String searchGoods(String keyword) {
        List<GoodsService.Goods> goodsList = goodsService.searchGoods(keyword);
        if (goodsList.isEmpty()) {
            return "未找到与'" + keyword + "'相关的商品";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("找到").append(goodsList.size()).append("个商品：<br>");
        for (GoodsService.Goods goods : goodsList) {
            sb.append(goods.toString()).append("<br><br>");
        }
        return sb.toString();
    }
    
    /**
     * 获取所有商品
     * @return 商品列表
     */
    @Tool("获取所有商品信息")
    public String getAllGoods() {
        List<GoodsService.Goods> goodsList = goodsService.getAllGoods();
        if (goodsList.isEmpty()) {
            return "暂无商品数据";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("所有商品（共").append(goodsList.size()).append("个）：<br>");
        for (GoodsService.Goods goods : goodsList) {
            sb.append(goods.toString()).append("<br><br>");
        }
        return sb.toString();
    }
    
    /**
     * 根据商品ID查询所有SKU
     * @param goodsId 商品ID
     * @return SKU列表
     */
    @Tool("根据商品ID查询该商品的所有SKU规格")
    public String getGoodsSkusByGoodsId(String goodsId) {
        try {
            Long id = Long.parseLong(goodsId);
            List<GoodsService.Goods> goodsList = goodsService.searchGoodsByGoodsId(id);
            if (goodsList.isEmpty()) {
                return "未找到商品ID为" + goodsId + "的SKU";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("商品ID ").append(goodsId).append(" 共有").append(goodsList.size()).append("个SKU：<br>");
            for (GoodsService.Goods goods : goodsList) {
                sb.append(goods.toString()).append("<br><br>");
            }
            return sb.toString();
        } catch (NumberFormatException e) {
            return "无效的商品ID格式：" + goodsId;
        }
    }
}
