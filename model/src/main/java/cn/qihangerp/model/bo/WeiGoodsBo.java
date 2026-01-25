package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeiGoodsBo implements Serializable {

    private String productId;
    private String outProductId;
    private String title;
    private Long shopId;
    private Integer status;
}
