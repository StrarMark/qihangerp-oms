package cn.qihangerp.open.request;

import cn.qihangerp.common.PageQuery;
import lombok.Data;

/**
 * 商品
 *
 */
@Data
public class ShopGoodsQueryRequest extends PageQuery {

    private Long id;//主键id
    /**
     * 平台id
     */
    private String productId;


    /**
     * 商品编号
     */
    private String goodsNum;


    /**
     * 商户id
     */
    private Long merchantId;

    private Long shopId;

    private static final long serialVersionUID = 1L;


}