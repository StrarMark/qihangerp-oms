package cn.qihangerp.model.open.request;


import lombok.Data;

@Data
public class ApiAddShopRequest {
    private Long shopId;// 店铺ID
    private Long warehouseId;// 仓库ID
    private String name;//店铺名称
    private Long merchantId;// 商户id
    private Integer platformId; //平台id
    /**
     * 第三方平台店铺id
     */
    private String sellerId;
    private String province;//省
    private String city;//市
    private String district;//区
    private String address;//地址
    private Boolean createWarehouse;//是否创建门店仓库
}
