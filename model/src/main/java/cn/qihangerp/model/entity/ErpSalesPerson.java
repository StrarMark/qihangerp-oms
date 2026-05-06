package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售人员表
 * @TableName erp_sales_person
 */
@TableName(value ="erp_sales_person")
@Data
public class ErpSalesPerson {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 销售人员姓名
     */
    private String name;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 所属商户ID
     */
    private Long merchantId;

    /**
     * 所属店铺ID
     */
    private Long shopId;
    private String shopName;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 提成比例（%），可为空，后续业绩计算用
     */
    private BigDecimal commissionRate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}