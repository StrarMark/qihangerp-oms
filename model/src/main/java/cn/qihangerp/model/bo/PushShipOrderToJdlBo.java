package cn.qihangerp.model.bo;

import lombok.Data;

@Data
public class PushShipOrderToJdlBo {
//    private Long[] ids;
    private Long shipOrderId;//发货订单id
    private Long warehouseId;
    private Long shopId;
    private Long shipperId;
}

