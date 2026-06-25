package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分账方表
 * @TableName erp_sales_share_party
 */
@Data
@TableName(value = "erp_sales_order_share_party")
public class ErpSalesShareParty implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String partyName;

    private Integer partyType;

    private Long relatedId;

    private String relatedName;

    private String accountNo;

    private String accountName;

    private String bankName;

    private String contactPerson;

    private String contactMobile;

    private Integer status;

    private String remark;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}
