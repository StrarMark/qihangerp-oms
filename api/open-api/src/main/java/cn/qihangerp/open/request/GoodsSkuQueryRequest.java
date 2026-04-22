package cn.qihangerp.open.request;

import cn.qihangerp.common.PageQuery;
import lombok.Data;

import java.util.List;

/**
 * 商品SKU
 *
 */

@Data
public class GoodsSkuQueryRequest extends PageQuery {
//    /**
//     * 主键id
//     */
//    private Long id;
    private Long merchantId;
    private String sellerId;//卖家ID(外部系统使用)
    private String sellerBrandId;//卖家品牌ID(外部系统使用)

    /**
     * 外键（o_goods）
     */
    private Long goodsId;

    /**
     * 外部erp系统商品id
     */
//    private String outerErpGoodsId;

    /**
     * 外部erp系统skuId(唯一)
     */
    private String outerErpSkuId;


    /**
     * 规格编码
     */
    private String skuCode;
    private String barCode;//产品条形码

    /**
     * sku名称
     */
    private String skuName;
    private String goodsName;

    /**
     * 状态：1销售中2已下架
     */
    private Integer status;
    /**
     * ids
     */
    private List<Long> ids;


    private static final long serialVersionUID = 1L;
}