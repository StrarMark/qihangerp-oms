package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频号小店退款
 * @TableName oms_wei_refund
 */
@Data
public class WeiRefundSearchBo implements Serializable {


    /**
     * 店铺id
     */
    private Long shopId;
    private Long merchantId;

    /**
     * 售后单号
     */
    private String afterSaleOrderId;

    /**
     * 售后单当前状态，参考：
USER_CANCELD	用户取消申请
MERCHANT_PROCESSING	商家受理中
MERCHANT_REJECT_REFUND	商家拒绝退款
MERCHANT_REJECT_RETURN	商家拒绝退货退款
USER_WAIT_RETURN	待买家退货
RETURN_CLOSED	退货退款关闭
MERCHANT_WAIT_RECEIPT	待商家收货
MERCHANT_OVERDUE_REFUND	商家逾期未退款
MERCHANT_REFUND_SUCCESS	退款完成
MERCHANT_RETURN_SUCCESS	退货退款完成
PLATFORM_REFUNDING	平台退款中
PLATFORM_REFUND_FAIL	平台退款失败
USER_WAIT_CONFIRM	待用户确认
MERCHANT_REFUND_RETRY_FAIL	商家打款失败，客服关闭售后
MERCHANT_FAIL	售后关闭
USER_WAIT_CONFIRM_UPDATE	待用户处理商家协商
USER_WAIT_HANDLE_MERCHANT_AFTER_SALE	待用户处理商家代发起的售后申请
     */
    private String status;


    /**
     * 订单号，该字段可用于获取订单
     */
    private String orderId;
    private String type;


    private static final long serialVersionUID = 1L;
}