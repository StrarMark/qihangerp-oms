package cn.qihangerp.open.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品SKU
 *
 */

@Data
public class WarehouseQueryRequest implements Serializable {
    /**
     * 主键id
     */
    private String id;
    private Long merchantId;

    /**
     * 用有人id（shopid是用有人之一）
     */
    private String ownerId;


    private static final long serialVersionUID = 1L;
}