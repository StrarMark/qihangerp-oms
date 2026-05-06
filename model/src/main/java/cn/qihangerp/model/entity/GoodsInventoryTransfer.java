package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 跨门店调拨申请实体类
 * 记录门店间调拨申请
 * @author qihang
 * @date 2026-04-20
 */
@Data
@TableName("o_goods_inventory_transfer")
public class GoodsInventoryTransfer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 申请方门店ID（调入方）
     */
    private Long fromShopId;

    /**
     * 目标门店ID（调出方）
     */
    private Long toShopId;

    /**
     * 申请调拨的批次ID
     */
    private Long batchId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 申请数量
     */
    private Integer applyQuantity;

    /**
     * 状态：1-待审批，2-已批准，3-已驳回，4-已完成，5-已取消
     */
    private Integer status;

    /**
     * 期望完成时间
     */
    private Date expectedTime;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 审批备注
     */
    private String approvalRemark;

    /**
     * 审批人
     */
    private String approvedBy;

    /**
     * 审批时间
     */
    private Date approvedTime;

    /**
     * 调出方出库时间
     */
    private Date outboundTime;

    /**
     * 调入方入库时间
     */
    private Date inboundTime;

    /**
     * 出库单ID（关联erp_warehouse_stock_out）
     */
    private Long outboundId;

    /**
     * 入库单ID（关联erp_warehouse_stock_in）
     */
    private Long inboundId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
