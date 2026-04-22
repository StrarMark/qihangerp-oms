package cn.qihangerp.open.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品库存管理
 * @TableName o_goods
 */
@Data
public class GoodsResponse implements Serializable {
    /**
     * 主键id
     */
    private String id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片地址
     */
    private String image;

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
     * 单位名称
     */
    private String unitName;


    /**
     * 条码
     */
    private String barCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态1销售中2已下架
     */
    private Integer status;



    /**
     * 保质期
     */
    private String period;

    /**
     * 预计采购价格
     */
    private BigDecimal purPrice;

    /**
     * 建议批发价
     */
//    private BigDecimal wholePrice;

    /**
     * 建议零售价
     */
    private BigDecimal retailPrice;

    private Long merchantId;

    private List<GoodsSkuResponse> skuList;

    private static final long serialVersionUID = 1L;


}