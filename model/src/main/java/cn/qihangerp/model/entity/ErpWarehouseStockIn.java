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
 * 入库单
 * @TableName erp_vendor_stock_in
 */
@TableName(value ="erp_warehouse_stock_in")
@Data
public class ErpWarehouseStockIn implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 入库单据编号
     */
    private String stockInNum;
    /**
     * 入库类型（1采购入库2退货入库）
     */
    private Integer stockInType;


    /**
     * 来源（0自己入库1商户申请入库）
     */
    private Integer sourceType;

    /**
     * 来源单号
     */
    private String sourceNo;

    /**
     * 商品数
     */
    private Integer goodsUnit;

    /**
     * 商品sku数
     */
    private Integer goodsSkuUnit;

    /**
     * 总件数
     */
    private Integer total;

    /**
     * 备注
     */
    private String remark;

    /**
     * 申请人id
     */
    private Long applyId;

    /**
     * 申请人
     */
    private String applyMan;

    /**
     * 联系电话
     */
    private String applyMobile;

    /**
     * 操作入库人id
     */
    private Long stockInOperatorId;

    /**
     * 操作入库人
     */
    private String stockInOperator;

    /**
     * 入库时间
     */
    private Date stockInTime;

    /**
     * 状态（0申请中1待入库2已入库）
     */
    private Integer status;

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
     * 供应商（云仓）ID
     */
    private Long vendorId;

    /**
     * 供应商（云仓）名
     */
    private String vendorName;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名
     */
    private String merchantName;



    @TableField(exist = false)
    private List<ErpWarehouseStockInItem> itemList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}