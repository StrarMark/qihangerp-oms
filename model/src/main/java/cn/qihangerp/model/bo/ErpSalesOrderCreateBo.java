package cn.qihangerp.model.bo;

import lombok.Data;

import java.util.List;

/**
 * 订单对象 erp_order
 *
 * @author qihang
 * @date 2024-01-05
 */
@Data
public class ErpSalesOrderCreateBo
{
    /**
     * 订单所属商户id
     */
    private Long ownerMerchantId;
    /** 订单类型 0销售订单1代发订单*/
    private Integer orderType;
    /** 订单编号 */
    private String orderNum;

    /** 店铺ID */
    private Long shopId;
    private Long merchantId;

    /**
     * 客户类型 0消费者 20集团商户 100外部2B客户
     */
    private Integer customerType;


    /** 买家留言信息 */
    private String buyerMemo;

    /** 备注 */
    private String remark;


    /** 商品金额 */
    private Double goodsAmount;

    /** 卖家优惠金额 */
    private Double sellerDiscount;

    /** 平台优惠金额 */
    private Double platformDiscount;

    /** 运费 */
    private Double postFee;

    private Double payment;
    //订单金额
    private Double amount;


    /** 收件人姓名 */
    private String receiverName;

    /** 收件人手机号 */
    private String receiverPhone;

    /** 收件人地址 */
    private String address;


    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String town;


    /** 订单明细信息 */
    private List<ErpSalesOrderCreateItemBo> itemList;

    /**
     * 销售员ID（后台下单指定）
     */
    private String salesmanId;

    /**
     * 销售员名称（后台下单指定）
     */
    private String salesmanName;

}
