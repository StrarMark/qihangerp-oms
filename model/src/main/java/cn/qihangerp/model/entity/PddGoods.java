package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * pdd商品表
 * @TableName oms_pdd_goods
 */
@TableName(value ="oms_pdd_goods")
@Data
public class PddGoods implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品编码
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品总数量
     */
    private Integer goodsQuantity;

    /**
     * 是否多sku，0-单sku，1-多sku
     */
    private Integer isMoreSku;

    /**
     * 是否在架上，0-下架中，1-架上
     */
    private Integer isOnsale;


    /**
     * 商品缩略图
     */
    private String thumbUrl;

    /**
     * 商品图片
     */
    private String imageUrl;

    /**
     * 商品创建时间的时间戳
     */
    private Long createdAt;

    /**
     * erp商品id
     */
    private Long erpGoodsId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商家外部编码
     */
    private String outerGoodsId;

    @TableField(exist = false)
    private List<PddGoodsSku> skuList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}