package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分账记录表
 * @TableName erp_sales_share_record
 */
@Data
@TableName(value = "erp_sales_order_share_record")
public class ErpSalesShareRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNum;

    private String orderId;

    private Long orderItemId;

    private Integer shopType;

    private Long goodsId;

    private Long goodsSkuId;

    private String goodsName;

    private String skuName;

    private Long ruleId;

    private Long sharePartyId;

    private String sharePartyName;

    private Integer shareWay;

    private BigDecimal shareRatio;

    private BigDecimal shareAmount;

    private BigDecimal basePrice;

    private BigDecimal baseAmount;

    private BigDecimal actualAmount;

    private Integer quantity;

    private Integer status;

    private String failReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
