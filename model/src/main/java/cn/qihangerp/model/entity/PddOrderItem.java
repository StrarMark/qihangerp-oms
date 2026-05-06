package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 拼多多订单明细表
 * @TableName oms_pdd_order_item
 */
@TableName(value ="oms_pdd_order_item")
@Data
public class PddOrderItem implements Serializable {
    /**
     * id，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 拼多多商品id
     */
    private String goodsId;

    /**
     * 拼多多商品skuid
     */
    private String skuId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String goodsImg;

    /**
     * 商品规格
     */
    private String goodsSpec;

    /**
     * 商品单价
     */
    private Double goodsPrice;

    /**
     * 商家外部编码（商品）
     */
    private String outerGoodsId;

    /**
     * 商家外部编码（sku）
     */
    private String outerId;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品id(o_goods外键)
     */
    private Long erpGoodsId;

    /**
     * 商品skuid(o_goods_sku外键)
     */
    private Long erpGoodsSkuId;

    /**
     * 	退款状态，枚举值：1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 11已取消
     */
    private Integer refundStatus;

    /**
     * 内部店铺ID
     */
    private Long shopId;

    /**
     * 商户id
     */
    private Long merchantId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}