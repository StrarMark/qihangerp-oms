package cn.qihangerp.open.request;

import lombok.Data;

@Data
public class ErpOrderCancelRequest {
    private Long id;
    private String cancelReason;
}
