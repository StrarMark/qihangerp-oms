package cn.qihangerp.open.request;

import cn.qihangerp.common.PageQuery;
import lombok.Data;

/**
 * 商品库
 *
 */
@Data
public class GoodsQueryRequest extends PageQuery {
    /**
     * 主键id
     */
    private String id;

    /**
     * 商品名称
     */
    private String name;


    /**
     * 商品唯一ID
     */
    private String outerErpGoodsId;
    private String sellerId;//卖家ID(外部系统使用)
    private String sellerBrandId;//卖家品牌ID(外部系统使用)
    /**
     * 商品编号
     */
    private String goodsNum;


    /**
     * 条码
     */
    private String barCode;

    /**
     * 发货方式
     */
//    private Integer shipType;
    /**
     * 状态：1销售中2已下架
     */
    private Integer status;
    /**
     * 商户id
     */
    private Long merchantId;

    private static final long serialVersionUID = 1L;


}