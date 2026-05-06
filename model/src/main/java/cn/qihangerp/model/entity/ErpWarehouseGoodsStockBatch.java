package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品库存批次
 * @TableName erp_vendor_inventory_batch
 */
@TableName(value ="erp_warehouse_goods_stock_batch")
@Data
public class ErpWarehouseGoodsStockBatch implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 库存主键id
     */
    private Long inventoryId;

    /**
     * 批次号
     */
    private String batchNum;

    /**
     * 初始数量
     */
    private Integer originQty;

    /**
     * 当前数量
     */
    private Integer currentQty;



    /**
     * 备注
     */
    private String remark;

    /**
     * 库存模式：0-传统SKU模式，1-一物一码模式（珠宝）
     */
    private Integer inventoryMode;

    /**
     * 条码（仅 mode=1 时使用）
     */
    private String barcode;

    /**
     * 实际金重（克）
     */
    private Float actualGoldWeight;
    /**
     * 实际银重（克）
     */
    private Float actualSilverWeight;
    /**
     * 工费（元）
     */
    private Float laborCost;
    /**
     * 鉴定证书号
     */
    private String certificateNo;

    /**
     * 采购单id
     */
    private Long purId;

    /**
     * 采购单itemId
     */
    private Long purItemId;


    /**
     * 采购价
     */
    private Double purPrice;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品编码
     */
    private String goodsNo;

    /**
     * 仓库id
     */
    private Long warehouseId;
    /**
     * 云仓ID
     */
    private Long vendorId;

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
    private Long merchantId;
    /**
     * 商户店铺id，0代表商户自己
     */
    private Long shopId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}