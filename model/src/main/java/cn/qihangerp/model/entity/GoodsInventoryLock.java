package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 库存锁定实体类
 * 用于调拨申请审批通过后锁定库存，以及临时锁库
 * @author qihang
 * @date 2026-04-20
 */
@Data
@TableName("o_goods_inventory_lock")
public class GoodsInventoryLock {

    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 锁定的批次ID（关联o_goods_inventory_batch）
     */
    private Long batchId;

    /**
     * 锁定数量
     */
    private Integer lockQuantity;

    /**
     * 状态：1-锁定中，2-已释放（出库完成），3-已取消
     */
    private Integer status;

    /**
     * 锁定过期时间（临时锁库）
     */
    private Date expireTime;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
