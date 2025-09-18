package cn.qihangerp.module.stock.request;

import lombok.Data;

@Data
public class StockInCreateItem {
    private Long skuId;
    private Long goodsId;
    private Integer quantity;
    private String skuCode;
    private String goodsName;
    private String goodsImg;
    private String skuName;
}
