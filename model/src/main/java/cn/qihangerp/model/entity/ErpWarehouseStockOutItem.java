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
 * 出库单明细
 * @TableName erp_warehouse_stock_out_item
 */
@TableName(value ="erp_warehouse_stock_out_item")
@Data
public class ErpWarehouseStockOutItem implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 出库类型1订单拣货出库2采购退货出库3盘点出库4报损出库
     */
    private Integer type;

    /**
     * 出库单id（外键）
     */
    private Long entryId;

    /**
     * 来源订单id
     */
    private Long sourceOrderId;

    /**
     * 来源订单itemId出库对应的itemId，如：order_item表id、invoice_info表id
     */
    private Long sourceOrderItemId;

    /**
     * 来源订单号
     */
    private String sourceOrderNum;

    /**
     * 总数量
     */
    private Integer originalQuantity;

    /**
     * 已出库数量
     */
    private Integer outQuantity;
    @TableField(exist = false)
    private int outQty;
    /**
     * 出库批次
     */
    private String outBatch;

    /**
     * 完成出库时间
     */
    private Date completeTime;

    /**
     * 完成拣货时间
     */
    private Date pickedTime;

    /**
     * 状态：0待出库1部分出库2全部出库
     */
    private Integer status;

    /**
     * 库存批次id
     */
//    private Long batchId;

    /**
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 仓位id
     */
//    @TableField(exist = false)
//    private Long positionId;


    /**
     * 仓位
     */
//    private String positionNum;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品编码
     */
    private String goodsNum;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String goodsImage;

    private String skuName;


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



    /**
     * 云仓ID
     */
    private Long vendorId;
    //商户ID
    private Long merchantId;
    // 出库的批次ID
    @TableField(exist = false)
    private Long batchId;
    /**
     * 库存批次list
     */
    @TableField(exist = false)
    private List<ErpWarehouseGoodsStockBatch> batchList;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}