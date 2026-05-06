package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 三方系统交互配置表
 * @TableName sys_third_system_config
 */
@TableName(value ="sys_third_system_config")
@Data
public class SysThirdSystemConfig implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * ERP接口服务器名
     */
    private String name;

    /**
     * ERP接口服务器url
     */
    private String apiUrl;
    private String callbackUrl;

    /**
     * ERP接口登录用户名
     */
    private String appKey;

    /**
     * ERP接口登录密码
     */
    private String appSecret;

    /**
     * ERP接口Token
     */
    private String accountToken;

    /**
     * 
     */
    private String refreshToken;

    /**
     * 
     */
    private String bizPin;

    /**
     * 
     */
    private String bizId;

    /**
     * 是否开启
     */
    private Integer isOn;

    /**
     * 固定系统id（100内部系统）
     */
    private Integer systemId;
    private String systemType;

    /**
     * 
     */
    private String type;

    /**
     * 
     */
    private String orderPath;

    /**
     * 
     */
    private String refundPath;

    /**
     * 
     */
    private String remark;
    private String isvSource;

    private Long merchantId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}