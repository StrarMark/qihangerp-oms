package cn.qihangerp.open.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * OMS商品SKU
 *
 */
@Data
public class GoodsSkuResponse implements Serializable {
    /**
     * 主键id
     */
    private String id;

    /**
     * 外键（o_goods）
     */
    private String goodsId;

    /**
     * 外部erp系统商品id
     */
//    private String outerErpGoodsId;

    /**
     * 外部erp系统skuId(唯一)
     */
    private String outerErpSkuId;
    private String sellerId;//卖家ID(外部系统使用)
    private String sellerBrandId;//卖家品牌ID(外部系统使用)
    /**
     * 商品名
     */
    private String goodsName;

    /**
     * 商品编码
     */
    private String goodsNum;

    /**
     * 规格名
     */
    private String skuName;

    /**
     * 规格编码
     */
    private String skuCode;


    /**
     * 颜色值
     */
    private String colorValue;

    /**
     * 颜色图片
     */
    private String colorImage;

    /**
     * 尺码值(材质)
     */
    private String sizeValue;


    /**
     * 款式值
     */
    private String styleValue;

    /**
     * 库存条形码
     */
    private String barCode;

    /**
     * 预计采购价格
     */
    private BigDecimal purPrice;

    /**
     * 建议零售价
     */
    private BigDecimal retailPrice;


    /**
     * 备注
     */
//    private String remark;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 商户ID
     */
    private Long merchantId;
    private String unit;

    private static final long serialVersionUID = 1L;
}