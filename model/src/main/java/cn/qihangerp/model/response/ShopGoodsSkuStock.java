package cn.qihangerp.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopGoodsSkuStock {
    private String skuId;
    private String goodsName;
    private Integer quantity;
    private BigDecimal price;//价格
    private String skuName;
    private String skuCode;
    private Integer inventoryMode;
    private List<Batch> batchList;

    @Data
    public static class Batch {
        private String barCode;
        private Integer quantity;
        private String batchNum;
        private Float goldWeight;
        private Double goldPrice;
        private Float silverWeight;
        private Double silverPrice;
        private Float laborCost;
        private Double laborPrice;
        private BigDecimal price;//sku价格

    }
}
