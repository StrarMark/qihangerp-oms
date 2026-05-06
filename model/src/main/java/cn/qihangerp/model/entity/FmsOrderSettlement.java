package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("fms_order_settlement")
public class FmsOrderSettlement {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String settlementNo;

    private Long merchantId;

    private Long shopId;

    private Long orderId;

    private String orderNo;

    private BigDecimal salesAmount;

    private BigDecimal purchaseCost;

    private BigDecimal shippingFee;

    private BigDecimal platformFee;

    private BigDecimal marketingFee;

    private BigDecimal otherFee;

    private BigDecimal totalCost;

    private BigDecimal profit;

    private BigDecimal profitRate;

    private Integer version;

    private String remark;

    private Integer status;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    @TableField(exist = false)
    private List<FmsOrderSettlementItem> items;
}
