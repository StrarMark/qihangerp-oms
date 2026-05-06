package cn.qihangerp.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class PushOrderToCloudWarehouseBo {
    private Long warehouseId;
    private Long shopId;
    private Long shipperId;
    private List<PushOrderToCloudWarehouseBoOrder> orderList;

}
