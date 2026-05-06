package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class XhsOrderBo implements Serializable {
    private String orderId;
    private Long shopId;
    private Integer status;
    private String startTime;
    private String endTime;
    private Long merchantId;
}
