package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频号小店退款
 * @TableName oms_wei_refund
 */
@Data
public class XhsRefundSearchBo implements Serializable {


    /**
     * 店铺id
     */
    private Long shopId;
    private Long merchantId;

    /**
     * 售后单号
     */
    private String returnsId;

    /**
     * 售后单当前状态，参考：
     */
    private String status;


    /**
     * 订单号，该字段可用于获取订单
     */
    private String orderId;
    private String type;


    private static final long serialVersionUID = 1L;
}