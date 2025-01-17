package cn.qihangerp.open.jd.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 京东商品SKU表
 * @TableName oms_jd_goods_sku
 */
@Data
public class OmsJdGoodsSku implements Serializable {
    /**
     * 
     */
    private String id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long wareId;

    /**
     * skuid
     */
    private Long skuId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 
     */
    private BigDecimal jdPrice;

    /**
     * 
     */
    private String outerId;

    /**
     * 
     */
    private String barCode;

    /**
     * 
     */
    private String logo;

    /**
     * 
     */
    private String saleAttrs;

    /**
     * sku属性名称
     */
    private String attrs;

    /**
     * 
     */
    private String skuName;

    /**
     * 
     */
    private Integer stockNum;

    /**
     * 
     */
    private Date modified;

    /**
     * 
     */
    private Date created;

    /**
     * 
     */
    private String currencySpuId;

    /**
     * erp商品id
     */
    private Long erpGoodsId;

    /**
     * erp商品sku id
     */
    private Long erpGoodsSkuId;

    private static final long serialVersionUID = 1L;
}