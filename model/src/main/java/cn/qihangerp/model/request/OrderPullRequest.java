package cn.qihangerp.model.request;

import lombok.Data;

@Data
public class OrderPullRequest {
    private Long shopId;//店铺Id

    private String orderId;
    private String startTime;
    private String endTime;
}
