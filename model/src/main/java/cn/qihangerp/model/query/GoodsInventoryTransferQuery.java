package cn.qihangerp.model.query;

import lombok.Data;

import java.util.Date;

/**
 * 跨门店调拨申请查询对象
 * @author qihang
 * @date 2026-04-20
 */
@Data
public class GoodsInventoryTransferQuery {

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
     * 状态：1-待审批，2-已批准，3-已驳回，4-已完成，5-已取消
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间开始
     */
    private Date createdTimeStart;

    /**
     * 创建时间结束
     */
    private Date createdTimeEnd;
}
