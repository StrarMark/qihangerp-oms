package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 发货常用快递公司表
 * @TableName erp_ship_logistics
 */
@Data
@TableName("erp_ship_logistics")
public class ErpShipLogistics implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体类型：SALES_HEADQUARTERS-总部，SALES_MERCHANT-商户，SALES_SHOP-店铺，CLOUD_WAREHOUSE-云仓，SUPPLIER-供应商
     */
    private String entityType;

    /**
     * 实体ID（总部/商户/店铺/云仓/供应商ID）
     */
    private Long entityId;

    /**
     * 快递公司ID（关联erp_logistics_company.id）
     */
    private Long logisticsId;

    /**
     * 平台类型（shopType）：200-淘宝，220-京东，240-拼多多等
     */
    private Integer shopType;

    /**
     * 是否默认：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 排序号
     */
    private Integer sort;

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

    /**
     * 快递公司名称（关联查询用）
     */
    @TableField(exist = false)
    private String logisticsName;

    /**
     * 快递公司编码（关联查询用）
     */
    @TableField(exist = false)
    private String logisticsCode;

    /**
     * 平台ID（关联查询用）
     */
    @TableField(exist = false)
    private Integer platformId;
}