package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 内销商品套餐主表
 * 用于维护总部销售给商户/店铺的商品套餐，套餐由多个SKU商品组成
 */
@Data
@TableName("erp_sales_goods_package")
public class ErpSalesGoodsPackage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 套餐编号
     * 格式：PKG+日期+序号，如PKG202605050001
     */
    private String packageNo;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 商户ID
     * 0表示全局套餐（所有商户可见），其他值表示指定商户的套餐
     */
    private Long merchantId;

    /**
     * 状态
     * 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 备注说明
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

    /**
     * 套餐商品列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<ErpSalesGoodsPackageItem> items;
}