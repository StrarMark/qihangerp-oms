package cn.qihangerp.open.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderQueryRequest {
    private Long merchantId;
    private Long shopId;
    private String orderNum;
    private Integer orderStatus;
    private Integer refundStatus;
    private String startTime;
    private String endTime;
//    private Integer shipStatus;
    private String receiverName;
    private String receiverMobile;
    private String shippingNumber;
    List<Long> orderIds;
}
