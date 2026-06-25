package cn.qihangerp.model.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarehouseManualShipOrderBo {
    private Long id;//订单id
    private String logisticsCompany;
    private String logisticsCode;
    private LocalDateTime shipTime;
    private List<Item> itemList;

    @Data
    public static class Item{
        private Long id;
        private Integer shipQuantity;//发货数量
    }
}
