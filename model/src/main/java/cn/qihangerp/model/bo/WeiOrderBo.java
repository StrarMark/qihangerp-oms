package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeiOrderBo implements Serializable {
    private String orderId;
    private Integer shopId;
    private Integer status;
    private String startTime;
    private String endTime;
    private Long merchantId;
}
