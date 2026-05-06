package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName oms_wei_logistics_template
 */
@TableName(value ="oms_wei_logistics_template")
@Data
public class WeiLogisticsTemplate implements Serializable {
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
    private String type;

    /**
     * 
     */
    private String desc1;

    /**
     * 
     */
    private Integer width;

    /**
     * 
     */
    private Integer height;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private String customConfig;

    private int isCustomize;

    @TableField(exist = false)
    private Long templateId;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}