package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分账规则表
 * @TableName erp_sales_share_rule
 */
@Data
@TableName(value = "erp_sales_order_share_rule")
public class ErpSalesShareRule implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleName;

    private Long goodsId;

    private Long goodsSkuId;

    private String goodsName;

    private String skuName;

    private String skuCode;

    private Long categoryId;

    private Integer shopType;

    private Long sharePartyId;

    private Integer shareWay;

    private BigDecimal shareRatio;

    private BigDecimal shareAmount;

    private Integer priority;

    private Integer status;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
