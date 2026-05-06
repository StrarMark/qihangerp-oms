package cn.qihangerp.model.bo;

import lombok.Data;

import java.util.List;

/**
 * H5内销订单创建BO
 * 支持套餐+商品混合下单
 */
@Data
public class ErpSalesOrderH5CreateBo {
    /**
     * 订单所属商户id
     */
    private Long ownerMerchantId;

    /**
     * 订单类型 0销售订单 1代发订单
     */
    private Integer orderType;

    /**
     * 订单编号（可选，不填则自动生成）
     */
    private String orderNum;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 客户类型 0消费者 20集团商户 100外部2B客户
     */
    private Integer customerType;

    /**
     * 买家留言信息
     */
    private String buyerMemo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品金额
     */
    private Double goodsAmount;

    /**
     * 卖家优惠金额
     */
    private Double sellerDiscount;

    /**
     * 平台优惠金额
     */
    private Double platformDiscount;

    /**
     * 运费
     */
    private Double postFee;

    /**
     * 实付金额
     */
    private Double payment;

    /**
     * 订单金额
     */
    private Double amount;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人手机号
     */
    private String receiverPhone;

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
     * 订单商品明细（直接添加的商品）
     */
    private List<ErpSalesOrderCreateItemBo> itemList;

    /**
     * 套餐ID列表（从套餐添加到订单）
     */
    private List<Long> packageIds;
}