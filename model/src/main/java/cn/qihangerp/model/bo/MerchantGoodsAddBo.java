package cn.qihangerp.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品管理对象 erp_goods
 *
 * @author qihang
 * @date 2023-12-29
 */
@Data
public class MerchantGoodsAddBo
{
    private static final long serialVersionUID = 1L;

    /** 商品名称 */
    private String name;

    /** 商品图片地址 */
    private String image;

    /** 商品编号 */
    private String number;
    /** 外部商品id */
    private String outerErpGoodsId;
    /**发货地*/
    private String province;
    private String city;
    private String town;


    /** 状态1销售中2已下架 */
    private Integer status;
    private Integer shipType;

    /** 预计采购价格 */
    private BigDecimal purPrice;

    /** 建议批发价 */
    private BigDecimal wholePrice;

    /** 建议零售价 */
    private BigDecimal retailPrice;

    /** 单位成本 */
    private BigDecimal unitCost;



    private String[] colorValues;
    private Map<Long,String> colorImages;
//    private Map<Long,String> colorNames;
    private String[] sizeValues;
    private String[] styleValues;
    private List<GoodsAddSkuBo> specList;

}
