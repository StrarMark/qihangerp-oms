package cn.qihangerp.model.request;

import lombok.Data;

/**
 * 云仓商品库存
 * @TableName erp_cloud_warehouse_goods_stock
 */

@Data
public class CloudWarehouseGoodsStockRequest {


    /**
     * CLPS商品编码（事业部商品）
     */
    private String goodsNo;

    /**
     * 商品名称
     */
    private String goodsName;


    /**
     * 云仓仓库编码
     */
    private String warehouseNo;
//    private String warehouseType;
    private Long warehouseId;



    /**
     * 商家商品编码
     */
    private String sellerGoodsSign;

    /**
     * 库存状态：1-良品；2-残品
     */
    private Integer stockStatus;

    /**
     * 库存类型：1-可销售；2-可退品；3-商家预留；4-仓库锁定；5-临期锁定；6-盘点锁定；7-内配出库锁定；8-在途库存；9-质押；10-VMI锁定；11-过期锁定；13-在途差异
     */
    private Integer stockType;

    /**
     * 商户ID
     */
    private Long merchantId;
}