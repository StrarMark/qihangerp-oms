package cn.qihangerp.open.request;

import cn.qihangerp.common.PageQuery;
import lombok.Data;

/**
 * 商品库
 *
 */
@Data
public class ShopQueryRequest extends PageQuery {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 店铺名称
     */
    private String name;
    /**
     * 对应第三方平台Id
     */
    private Integer type;

    /**
     * 卖家ID
     */
    private String sellerId;

    /**
     * 商户id
     */
    private Long merchantId;

    private static final long serialVersionUID = 1L;


}