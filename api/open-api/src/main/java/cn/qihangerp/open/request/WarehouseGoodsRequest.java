package cn.qihangerp.open.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 仓库商品添加、更新
 */

@Data
public class WarehouseGoodsRequest implements Serializable {
    /**
     *  主键id
     */
    private Long id;

    /**
     * 商家商品编码
     */
    private String erpGoodsNo;

    /**
     * 商品商家标识
     */
    private String erpGoodsSign;

    /**
     * 仓库商品编码
     */
//    private String goodsNo;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品简称
     */
    private String abbreviation;

    /**
     * 商品条码
     */
    private String barCode;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 颜色
     */
    private String color;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 商品规格
     */
    private String skuName;


    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 店铺Id
     */
    private Long shopId;


    /**
     * 仓库ID
     */
    private Long warehouseId;

}