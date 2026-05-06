package cn.qihangerp.model.query;

import lombok.Data;

import java.util.Date;

/**
 * 库存锁定查询对象
 * @author qihang
 * @date 2026-04-20
 */
@Data
public class GoodsInventoryLockQuery {

    /**
     * 锁定单号
     */
    private String lockNo;

    /**
     * 来源：1-调拨申请，2-临时锁库
     */
    private Integer sourceType;

    /**
     * 关联调拨申请ID或锁库申请ID
     */
    private Long sourceId;

    /**
     * 锁定的批次ID
     */
    private Long batchId;

    /**
     * 状态：1-锁定中，2-已释放（出库完成），3-已取消
     */
    private Integer status;

    /**
     * 创建时间开始
     */
    private Date createdTimeStart;

    /**
     * 创建时间结束
     */
    private Date createdTimeEnd;
}
