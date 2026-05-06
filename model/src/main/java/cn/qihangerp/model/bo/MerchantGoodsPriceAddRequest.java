package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商户商品定价请求对象
 *
 * @author qihang
 * @date 2023-12-29
 */
@Data
public class MerchantGoodsPriceAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 商品库skuid */
    private Long goodsSkuId;
    private Long merchantId;//商户id
    private Double price;

}
