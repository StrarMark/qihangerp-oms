package cn.qihangerp.open.request;

import lombok.Data;

/**
 * 云仓商品库存
 *
 */
@Data
public class WarehouseGoodsStockRequest {
    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商家商品编码
     */
    private String erpGoodsNo;

    /**
     * 库存状态：1-良品；2-残品
     */
    private Integer stockStatus;

    /**
     * 库存类型：1-可销售；2-可退品；3-商家预留；4-仓库锁定；5-临期锁定；6-盘点锁定；7-内配出库锁定；8-在途库存；9-质押；10-VMI锁定；11-过期锁定；13-在途差异
     */
    private Integer stockType;

    /**
     * 商品总库存
     */
    private Integer totalNum;

    /**
     * 商品可用库存
     */
    private Integer usableNum;

    /**
     * 仓库ID
     */
    private Long warehouseId;

}