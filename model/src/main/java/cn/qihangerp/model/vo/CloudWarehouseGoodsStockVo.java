package cn.qihangerp.model.vo;

import cn.qihangerp.utils.poi.Excel;
import lombok.Data;

/**
 * 云仓商品库存
 * @TableName
 */

@Data
public class CloudWarehouseGoodsStockVo {
    /**
     *  id
     */
    @Excel(name = "仓库商品ID",sort = 1)
    private String goodsId;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称",sort = 2)
    private String goodsName;
    /**
     * CLPS商品编码（事业部商品）
     */
    @Excel(name = "商品编码",sort = 3)
    private String goodsNo;
    /**
     * 商家商品编码
     */
    @Excel(name = "商家商品编码",sort = 4)
    private String erpGoodsNo;

    private String sellerGoodsSign;

    @Excel(name = "外部SKU编码",sort = 5)
    private String outerErpSkuId;
    private String erpGoodsSkuId;

    /**
     * 云仓仓库名称
     */
    @Excel(name = "仓库",sort = 7)
    private String warehouseName;


    /**
     * 商品总库存
     */
    @Excel(name = "总库存",sort = 6)
    private Integer totalNum;
    private Integer usableNum;


}