package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * OMS商品SKU表
 * @TableName o_goods_sku
 */
@TableName(value ="o_goods_sku")
@Data
public class OGoodsSku implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 外键（o_goods）
     */
    private Long goodsId;

    /**
     * 外部erp系统商品id
     */
    private String outerErpGoodsId;

    /**
     * 外部erp系统skuId(唯一)
     */
    private String outerErpSkuId;

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
     * 颜色label
     */
    private String colorLabel;

    /**
     * 颜色id
     */
    private Long colorId;

    /**
     * 颜色值
     */
    private String colorValue;

    /**
     * 颜色图片
     */
    private String colorImage;

    /**
     * 尺码label
     */
    private String sizeLabel;

    /**
     * 尺码id
     */
    private Long sizeId;

    /**
     * 尺码值(材质)
     */
    private String sizeValue;

    /**
     * 款式label
     */
    private String styleLabel;

    /**
     * 款式id
     */
    private Long styleId;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.##")
    private BigDecimal purPrice;

    /**
     * 建议零售价
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.##")
    private BigDecimal retailPrice;

    /**
     * 单位成本
     */
    private BigDecimal unitCost;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最低库存（预警）
     */
    private Integer lowQty;

    /**
     * 最高库存（预警）
     */
    private Integer highQty;

    /**
     * erp商品体积
     */
    private String volume;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}