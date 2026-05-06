package cn.qihangerp.model.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class DouGoodsQuery implements Serializable {
    /**
     * 商品数字id
     */
    private Long productId;
    private Long skuId;
    private String erpGoodsId;
    private String title;
    private String outerProductId;
    private String code;
    private Integer shopId;
    private Integer hasLink;//是否关联
    private Long merchantId;
}
