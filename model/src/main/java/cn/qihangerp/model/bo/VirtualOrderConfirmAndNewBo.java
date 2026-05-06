package cn.qihangerp.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VirtualOrderConfirmAndNewBo {
    private Long shopOrderId;//店铺订单ID
    private String name;//收货人
    private String phone;//联系电话
    private String province;
    private String city;
    private String county;
    private String address;
    private String remark;
    private List<Item> itemList;
    @Data
    public static class Item{
        private Long id;
        private Long goodsId;
        private String goodsNum;
        private String goodsName;
        private String colorImage;
        private Integer quantity;
        private BigDecimal purPrice = BigDecimal.ZERO;
        private BigDecimal retailPrice = BigDecimal.ZERO;;
        private String skuName;
        private String skuCode;
    }
}
