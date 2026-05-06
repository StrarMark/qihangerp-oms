package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName oms_dou_logistics_template
 */
@TableName(value ="oms_dou_logistics_template")
@Data
public class DouLogisticsTemplate implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String logisticsCode;

    /**
     * 
     */
    private String perviewUrl;

    /**
     * 
     */
    private String templateCode;

    /**
     * 
     */
    private Long templateId;

    /**
     * 
     */
    private String templateName;

    /**
     * 
     */
    private Integer templateType;

    /**
     * 
     */
    private String templateUrl;

    /**
     * 
     */
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}