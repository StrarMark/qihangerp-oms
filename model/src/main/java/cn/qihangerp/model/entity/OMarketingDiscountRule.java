package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单折扣规则表（营销模块-手动订单折扣）
 * @TableName o_marketing_discount_rule
 */
@TableName(value ="o_marketing_discount_rule")
@Data
public class OMarketingDiscountRule {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 折扣规则名称，便于识别
     */
    private String ruleName;

    /**
     * 折扣类型：1-百分比折扣，2-固定金额折扣
     */
    private Integer discountType;

    /**
     * 折扣值，百分比时如10表示10%，固定金额时如50.00表示50元
     */
    private BigDecimal discountValue;

    /**
     * 适用范围：1-全部（所有商户/门店），2-商户，3-门店
     */
    private Integer applyScope;

    /**
     * 适用目标ID，
     */
    private Long applyMerchantId;
    private String applyMerchantName;
    private Long applyShopId;
    private String applyShopName;

    /**
     * 总可用次数，0表示不限次数
     */
    private Integer totalQuota;

    /**
     * 已使用次数
     */
    private Integer usedQuota;

    /**
     * 创建来源：1-总部，2-商户，3-店铺
     */
    private Integer sourceType;

    /**
     * 来源ID（商户ID或店铺ID）
     */
    private Long sourceId;

    /**
     * 订单金额下限，满足此金额才可使用该折扣，0表示无限制
     */
    private BigDecimal minOrderAmount;

    /**
     * 生效开始时间
     */
    private Long startTime;

    /**
     * 生效结束时间
     */
    private Long endTime;

    /**
     * 状态：0待审核，1-启用，2-审核拒绝
     */
    private Integer status;

    /**
     * 优先级，数字越大优先级越高，当多个折扣同时适用时，可优先展示或自动选用
     */
    private Integer priority;

    /**
     * 创建人标识（用户ID或用户名）
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

    /**
     * 备注说明
     */
    private String remark;
}