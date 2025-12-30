package cn.qihangerp.module.erp.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 采购物流公司表
 * @TableName erp_logistics
 */
@TableName(value ="erp_logistics")
@Data
public class ErpLogistics {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 物流公司编码
     */
    private String code;

    /**
     * 物流公司名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0禁用1启用）
     */
    private Integer status;
}