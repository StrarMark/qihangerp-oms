package cn.qihangerp.model.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class KsOrderSearchBo implements Serializable {
    private String oid;
    private Long shopId;
    private Integer status;
    private String startTime;
    private String endTime;
    private Long merchantId;
}
