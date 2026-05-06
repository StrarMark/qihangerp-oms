package cn.qihangerp.model.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单折扣规则表
 *
 */

@Data
public class OMarketingDiscountRuleAdd {


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
    private String startTime;

    /**
     * 生效结束时间
     */
    private String endTime;

    /**
     * 状态：0待审核，1-启用，2-审核拒绝
     */
    private Integer status;

    /**
     * 备注说明
     */
    private String remark;
}