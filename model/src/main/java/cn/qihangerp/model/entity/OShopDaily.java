package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 店铺日报
 * @TableName o_shop_daily
 */
@TableName(value ="o_shop_daily")
@Data
public class OShopDaily implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报表日期
     */
    private String date;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 平台id
     */
    private Long platformId;

    /**
     * 国家/地区
     */
    private Long regionId;

    /**
     * 订单总数
     */
    private Integer orderTotal;

    /**
     * 订单总金额（当前货币）
     */
    private BigDecimal orderAmount;

    /**
     * 刷单数量
     */
    private Integer falseOrderTotal;

    /**
     * 刷单金额（当前货币）
     */
    private BigDecimal falseOrderAmount;

    /**
     * 刷单金额（人民币）
     */
    private BigDecimal falseOrderAmount1;

    /**
     * 真实订单数
     */
    private Integer trueOrderTotal;

    /**
     * 真实订单金额（当前货币）
     */
    private BigDecimal trueOrderAmount;

    /**
     * 广告支出
     */
    private BigDecimal adFee;

    /**
     * 广告点击
     */
    private Integer adClick;

    /**
     * 广告点击成本
     */
    private BigDecimal adClickFee;

    /**
     * ROI
     */
    private BigDecimal adRoi;

    /**
     * 平均客单价
     */
    private BigDecimal unitPrice;

    /**
     * 提现金额（当前货币）
     */
    private BigDecimal withdrawalAmount;

    /**
     * 备注
     */
    private String remark;

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
    private Double exchangeRate;
    @TableField(exist = false)
    private Long manageUserId;
    @TableField(exist = false)
    private Long shopGroupId;

    @TableField(exist = false)
    private List<OShopDailyDetail> itemList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}