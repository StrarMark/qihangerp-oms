package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 门店共享实体类
 * 记录门店之间共享关系（查看库存、调拨申请）
 * @author qihang
 * @date 2026-04-21
 */
@Data
@TableName("o_shop_share")
public class ShopShare {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 发起门店ID
     */
    private Long fromShopId;
    
    /**
     * 目标门店ID
     */
    private Long toShopId;
    
    /**
     * 授权类型：1-查看库存
     */
    private Integer authType;
    
    /**
     * 可见范围：1-仅汇总，2-批次明细
     */
    private Integer visibleScope;
    
    /**
     * 状态：状态：1: 待审批, 2: 已通过, 3: 已驳回, 4: 已取消,
     */
    private Integer status;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 创建时间
     */
    private Date createdTime;
    
    /**
     * 更新时间
     */
    private Date updatedTime;
}