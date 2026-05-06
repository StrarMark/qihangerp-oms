package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName erp_cloud_warehouse_goods
 */
@TableName(value ="erp_warehouse_goods")
@Data
public class ErpWarehouseGoods implements Serializable {
    /**
     *  主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商家商品编码
     */
    private String erpGoodsNo;

    /**
     * 商品商家标识
     */
    private String erpGoodsSign;

    /**
     * CLPS商品编码（事业部商品）
     */
    private String goodsNo;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     *销售平台商品编码
     */
    private String salesPlatformGoodsNo;

    /**
     * 商品简称
     */
    private String abbreviation;

    /**
     * 商品条码，多个条码用”,“分隔,
     */
    private String barCode;

    /**
     * 服装图片地址
     */
    private String imageUrl;

    /**
     * 长 (毫米)，长度限制1~9位，保留小数点后两位
     */
    private Double length;

    /**
     * 宽 (毫米)，长度限制1~9位，保留小数点后两位
     */
    private Double width;

    /**
     * 高 (毫米)，长度限制1~9位，保留小数点后两位
     */
    private Double height;

    /**
     * 体积 (立方毫米)，长度限制1~9位，保留小数点后三位
     */
    private Double volume;

    /**
     * 毛重 (千克)，长度限制1~9位，保留小数点后三位
     */
    private Double grossWeight;

    /**
     * 净重(单位：kg)
     */
    private Double netWeight;

    /**
     * 颜色
     */
    private String color;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 商品规格
     */
    private String standard;

    /**
     * 品牌编码 
     */
    private String brandNo;

    /**
     * 品牌名称
     */
    private String brandName;
    private String unitName;
    private String cateName;
    private String cateId;

    /**
     * 商品id(o_goods外键)
     */
    private Long erpGoodsId;

    /**
     * 商品skuid(o_goods_sku外键)
     */
    private Long erpGoodsSkuId;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 商户ID
     */
    private Long merchantId;
    /**
     * 商户店铺id，0代表商户自己
     */
    private Long shopId;

    /**
     * 店铺编码
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * CLPS事业部编号
     */
    private String ownerNo;
    private String warehouseNo;
    private Long warehouseId;
    private String warehouseType;
    @TableField(exist = false)
    private Integer stock;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}