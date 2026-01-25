package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeiGoodsSkuBo implements Serializable {

    private String productId;
    private String skuId;
    private String title;
    private String skuCode;
    private Long shopId;
    private Long erpSkuId;
    private Integer status;
}
