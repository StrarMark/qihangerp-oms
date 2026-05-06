package cn.qihangerp.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author qihang
 * @date 2026-2-5
 */
@Data
public class SupplierGoodsItemAddBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;
    private Long supplierId;
    private String productName;
    private String standard;
    private String brandName;
    private String skuCode;
    private String barCode;
    private String imageUrl;
    private String unit;
    private Double price;
    private String color;
    private String size;
    private String style;
}
