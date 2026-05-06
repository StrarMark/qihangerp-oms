package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("fms_order_settlement_item")
public class FmsOrderSettlementItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long settlementId;

    private Integer itemType;

    private String itemName;

    private BigDecimal amount;

    private Long relatedId;

    private String remark;

    private Date createTime;
}
