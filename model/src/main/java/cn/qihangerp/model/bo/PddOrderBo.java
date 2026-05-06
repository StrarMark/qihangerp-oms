package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PddOrderBo implements Serializable {
    private String orderSn;
    private Long skuId;
    private Long erpGoodsSkuId;
    private Long shopId;
    private Integer orderStatus;
    private Integer refundStatus;
    private String startTime;
    private String endTime;
    private Long merchantId;
}
