package cn.qihangerp.open.request;

import lombok.Data;

@Data
public class ShopAddRequest {
    private Long id;//店铺ID（修改时使用）
    private String name;//店铺名称
    /**
     * 第三方平台店铺id
     */
    private String sellerId;
    private Long merchantId;// 商户id
    private Integer platformId; //平台
    private String province;//省
    private String city;//市
    private String district;//区
    private String address;//地址
//    private Boolean createWarehouse;//是否创建门店仓库

}
