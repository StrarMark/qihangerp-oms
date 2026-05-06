package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 线下渠道订单表
 * @TableName offline_order
 */
@Data
public class ErpSalesOrder implements Serializable {
    /**
     * 订单id，自增
     */
    @TableId(type = IdType.AUTO)
    private String id;
    /**
     * 订单类型（0销售订单1代发订单）
     */
    private Integer orderType;
    /**
     * 订单编号（第三方平台订单号）
     */
    private String orderNum;

    /**
     * 店铺ID
     */
    private Long shopId;
    /**
     * 商品ID
     */
    private Long merchantId;
    /**
     * 订单所属商户id
     */
    private Long ownerMerchantId;

    /**
     * 客户类型 0消费者 20集团商户 100外部2B客户
     */
    private Integer customerType;


    /**
     * 订单备注
     */
    private String remark;

    /**
     * 买家留言信息
     */
    private String buyerMemo;

    /**
     * 卖家留言信息
     */
    private String sellerMemo;

    /**
     * 标签
     */
    private String tag;

    /**
     * 售后状态 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 
     */
    private Integer refundStatus;

    /**
     * 订单状态0：新订单，1：待发货，2：已发货，3：已完成，11已取消；12退款中；21待付款；22锁定，29删除，101部分发货
     */
    private Integer orderStatus;

    /**
     * 订单商品金额
     */
    private Double goodsAmount;

    /**
     * 订单运费
     */
    private Double postFee;

    /**
     * 订单实际金额
     */
    private Double amount;

    /**
     * 商家优惠金额，单位：元
     */
    private Double sellerDiscount;

    /**
     * 平台优惠金额，单位：元
     */
    private Double platformDiscount;

    /**
     * 实付金额
     */
    private Double payment;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人手机号
     */
    private String receiverMobile;

    /**
     * 收件人地址
     */
    private String address;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String town;

    /**
     * 订单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    /**
     * 发货类型（0仓库发货；1供应商代发）
     */
    private Integer shipType;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shippingTime;

    /**
     * 快递单号
     */
    private String shippingNumber;

    /**
     * 物流公司
     */
    private String shippingCompany;

    /**
     * 发货人
     */
    private String shippingMan;

    /**
     * 发货费用
     */
    private BigDecimal shippingCost;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 销售员ID
     */
    private String salesmanId;

    /**
     * 销售员名称
     */
    private String salesmanName;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;
    private String cancelReason;
    private Integer omsPushStatus;
    private Integer hasGift;//是否有礼品0没有，大于0表示有，-1表示全是礼品

    @TableField(exist = false)
    private List<ErpSalesOrderItem> itemList;
    private static final long serialVersionUID = 1L;
}