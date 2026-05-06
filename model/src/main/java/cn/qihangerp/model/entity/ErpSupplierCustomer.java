package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商客户表（自动创建）
 * @TableName erp_supplier_customer
 */
@Data
public class ErpSupplierCustomer implements Serializable {
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
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 所属商户ID
     */
    private Long merchantId;

    /**
     * 所属商户名称
     */
    private String merchantName;

    /**
     * 累计订单数
     */
    private Integer totalOrders;

    /**
     * 累计金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态(0禁用1启用)
     */
    private Integer status;

    /**
     * 首次下单时间
     */
    private Date firstOrderTime;

    /**
     * 最近下单时间
     */
    private Date lastOrderTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}