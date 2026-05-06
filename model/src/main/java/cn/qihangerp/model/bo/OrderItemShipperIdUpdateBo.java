package cn.qihangerp.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class OrderItemShipperIdUpdateBo {
    private List<Item> orderItemList;
    private Integer shipType;
    private Long shipperId;

    @Data
    public static class Item {
        private Long id;
    }
}
