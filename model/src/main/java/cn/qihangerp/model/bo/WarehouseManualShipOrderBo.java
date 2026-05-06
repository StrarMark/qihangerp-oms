package cn.qihangerp.model.bo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WarehouseManualShipOrderBo {
    private Long id;//订单id
    private String logisticsCompany;
    private String logisticsCode;
    private Date shipTime;
    private List<Item> itemList;

    @Data
    public static class Item{
        private Long id;
        private Integer shipQuantity;//发货数量
    }
}
