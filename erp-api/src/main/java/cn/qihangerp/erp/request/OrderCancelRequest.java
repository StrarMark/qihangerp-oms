package cn.qihangerp.erp.request;

import lombok.Data;

@Data
public class OrderCancelRequest {
    private Long id;
    private Long orderItemId;
    private String cancelReason;
}
