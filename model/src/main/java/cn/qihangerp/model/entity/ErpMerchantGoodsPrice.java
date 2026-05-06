package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 商户-商品价格历史表（数据来源：总部定价）
 * @TableName erp_merchant_goods_price
 */
@TableName(value ="erp_merchant_goods_price")
@Data
public class ErpMerchantGoodsPrice {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 渠道id（店铺平台id）
     */
    private Integer shopPlatformId;

    /**
     * 商品库商品ID
     */
    private Long goodsId;
    /**
     * 商品库商品SkuId
     */
    private Long goodsSkuId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品规格名称
     */
    private String skuName;

    /**
     * 商品SKU编码
     */
    private String skuCode;

    /**
     * 采购价
     */
    private Double purPrice;

    /**
     * 零售价
     */
    private Double retailPrice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态0过期1在用
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;
}