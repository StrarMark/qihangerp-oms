package cn.qihangerp.model.bo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ErpSalesOrderCreateItemBo {
    /** skuId */
    private String id;
    private String goodsId;
    private String skuId;
    private String goodsSkuId;

    /** skuCode */
    private String skuCode;
    private String goodsName;
    private String skuName;
//    private String colorValue;
//    private String sizeValue;
//    private String styleValue;
    private String colorImage;
    private BigDecimal retailPrice;
    private BigDecimal itemAmount;
    private Integer quantity;
    private Integer isGift;
}
