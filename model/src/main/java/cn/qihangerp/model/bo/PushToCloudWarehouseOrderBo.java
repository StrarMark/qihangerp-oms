package cn.qihangerp.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class PushToCloudWarehouseOrderBo {
    private Long orderId;//订单ID
    private String shippingErpOrderCode;//推送到云仓的ERP发货订单号
    private String shippingOrderCode;//推送到云仓的发货订单号
    private Integer pushStatus;//推送状态0未推送1已推送

    // 仓库id
    private Long warehouseId;

    /**
     * 云仓编码
     */
    private String warehouseNo;
    // 仓库类型
//    private String warehouseType;

    /**
     * 云仓名称
     */
    private String warehouseName;

    private String shipperNo;
    private String shipperName;

    private String shopNo;
    private String shopName;
    private String shopPlatformCode;

    private List<Item> itemList;


    @Data
    public static class Item {
         private Long orderItemId;
         //平台skuId
         private String platformSkuId;
//         private Long erpGoodsSkuId;
//         private Long erpGoodsId;
    }
}
