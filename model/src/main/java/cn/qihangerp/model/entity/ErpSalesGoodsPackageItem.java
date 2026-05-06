package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 内销商品套餐商品明细表
 * 记录套餐内的商品SKU及数量，包含冗余的商品基本信息方便展示
 */
@Data
@TableName("erp_sales_goods_package_item")
public class ErpSalesGoodsPackageItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 套餐ID
     * 关联 erp_sales_goods_package.id
     */
    private Long packageId;

    /**
     * 商品ID
     * 关联 o_goods.id，总部商品库的商品ID
     */
    private Long goodsId;

    /**
     * 商品SKU ID
     * 关联 o_goods_sku.id，总部商品库的SKU ID
     */
    private Long goodsSkuId;

    /**
     * 商品名称（冗余）
     * 对应 o_goods.goods_name
     */
    private String goodsName;

    /**
     * SKU规格名称（冗余）
     * 对应 o_goods_sku.sku_name
     */
    private String skuName;

    /**
     * SKU编码（冗余）
     * 对应 o_goods_sku.sku_code
     */
    private String skuCode;

    /**
     * SKU图片（冗余）
     * 对应 o_goods_sku.color_image
     */
    private String skuImage;

    /**
     * 数量
     * 该SKU商品的数量
     */
    private Integer quantity;

    /**
     * 创建时间
     */
    private Date createTime;
}