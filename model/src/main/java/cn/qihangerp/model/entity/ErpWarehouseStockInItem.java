package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 入库单明细
 * @TableName erp_vendor_stock_in_item
 */
@TableName(value ="erp_warehouse_stock_in_item")
@Data
public class ErpWarehouseStockInItem implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 入库单id
     */
    private Long stockInId;



    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品编码
     */
    private String goodsNo;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String goodsImage;


    /**
     * 颜色
     */
    private String skuName;

    /**
     * 原始数量
     */
    private Integer quantity;


    /**
     * 入库数量
     */
    private Integer inQuantity;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0待入库2已入库）
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
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 仓位id
     */
    private Long positionId;

    /**
     * 仓位编码
     */
    private String positionNum;

    /**
     * 商户ID
     */
    private Long vendorId;

    /**
     * 商户ID
     */
    private Long merchantId;
    /**
     * 入库操作数量
     */
    @TableField(exist = false)
    private Integer intoQuantity=0;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}