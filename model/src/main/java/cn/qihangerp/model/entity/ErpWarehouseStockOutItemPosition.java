package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 出库仓位详情
 * @TableName erp_warehouse_stock_out_item_position
 */
@TableName(value ="erp_warehouse_stock_out_item_position")
@Data
public class ErpWarehouseStockOutItemPosition implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 出库单ID
     */
    private Long entryId;

    /**
     * 出库单ItemID
     */
    private Long entryItemId;

    /**
     * 库存ID
     */
    private Long goodsInventoryId;

    /**
     * 库存详情ID
     */
    private Long goodsInventoryDetailId;

    /**
     * 出库数量
     */
    private Long quantity;

    /**
     * 出库仓位ID
     */
    private Integer locationId;

    /**
     * 出库操作人userid
     */
    private Integer operatorId;

    /**
     * 出库操作人
     */
    private String operatorName;
    private String outBatch;

    /**
     * 出库时间
     */
    private Date outTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}