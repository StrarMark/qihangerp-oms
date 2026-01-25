package cn.qihangerp.model.request;

import lombok.Data;

@Data
public class RefundPullRequest {
    private Long shopId;//店铺Id
    private String orderId;
    private String refundId;
    private String createTime;
    private String updateTime;
}
