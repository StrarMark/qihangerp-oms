package cn.qihangerp.module.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 发货-发货记录明细表
 * @TableName o_shipment_item
 */
@TableName(value ="o_shipment_item")
@Data
public class OShipmentItem implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发货表id
     */
    private Long shipmentId;

    /**
     * o_order表id
     */
    private Long orderId;

    /**
     * o_order_item表id
     */
    private Long orderItemId;

    /**
     * 订单编号（第三方平台）
     */
    private String orderNum;

    /**
     * 子订单号（第三方平台）
     */
    private String subOrderNum;

    /**
     * erp系统商品id
     */
    private Long goodsId;

    /**
     * erp系统商品规格id
     */
    private Long skuId;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品图片
     */
    private String goodsImg;

    /**
     * 商品编码
     */
    private String goodsNum;

    /**
     * 商品规格
     */
    private String skuName;

    /**
     * 商品规格编码
     */
    private String skuNum;

    /**
     * 商品数量
     */
    private Integer quantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}