package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商商品报价表
 * @TableName erp_supplier_goods_price
 */
@TableName(value ="erp_supplier_goods_price")
@Data
public class ErpSupplierGoodsPrice {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商SPU ID
     */
    private Long supplierProductId;

    /**
     * 供应商SKU ID
     */
    private Long supplierProductItemId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 报价价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 有效期开始时间
     */
    private Date validStartTime;

    /**
     * 有效期结束时间
     */
    private Date validEndTime;

    /**
     * 所属商户0总部，大于0就是商户
     */
    private Long merchantId;

    /**
     * 状态(0无效1有效)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
}