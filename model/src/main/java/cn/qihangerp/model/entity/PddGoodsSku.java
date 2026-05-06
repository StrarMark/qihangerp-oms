package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * pdd商品SKU表
 * @TableName oms_pdd_goods_sku
 */
@TableName(value ="oms_pdd_goods_sku")
@Data
public class PddGoodsSku implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * sku编码
     */
    private Long skuId;

    /**
     * pdd商品编码
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品缩略图
     */
    private String thumbUrl;

    /**
     * 商家外部编码
     */
    private String outerGoodsId;

    /**
     * 商家外部编码（sku）
     */
    private String outerId;

    /**
     * sku库存
     */
    private Integer skuQuantity;

    /**
     * 规格名称
     */
    private String spec;

    /**
     * 
     */
    private String specDetails;

    /**
     * sku是否在架上，0-下架中，1-架上
     */
    private Integer isSkuOnsale;

    /**
     * 商品id(o_goods外键)
     */
    private Long erpGoodsId;

    /**
     * 商品skuid(o_goods_sku外键)
     */
    private Long erpGoodsSkuId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商户ID
     */
    private Long merchantId;
    /**
     * 拼团价格（元）
     */
    private Double groupPrice;
    /**
     * 单买价格（元）
     */
    private Double singlePrice;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}