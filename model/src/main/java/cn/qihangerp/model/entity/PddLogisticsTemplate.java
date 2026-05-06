package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName oms_pdd_logistics_template
 */
@TableName(value ="oms_pdd_logistics_template")
@Data
public class PddLogisticsTemplate implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型0标准模版1自定义模版
     */
    private Integer type;

    /**
     * 物流公司code
     */
    private String wpCode;

    /**
     * 
     */
    private String templateCode;

    /**
     * 模板id
     */
    private Long templateId;

    /**
     * 
     */
    private String templateName;

    /**
     * 
     */
    private String waybillType;

    /**
     * 
     */
    private String templateUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}