package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 开放接口授权
 * @TableName sys_open_auth
 */
@TableName(value ="sys_open_auth")
@Data
public class SysOpenAuth implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * AppKey
     */
    private String appKey;

    /**
     * 密钥
     */
    private String appSecret;

    /**
     * 最后一次请求ip
     */
    private String requestIp;

    /**
     * 最后一次请求时间
     */
    private Date requestTime;

    /**
     * 请求总次数
     */
    private Integer requestCount;

    /**
     * 白名单
     */
    private String whiteList;

    /**
     * 状态1启用0禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
    /*
    * 类型：10回传配置；20开放平台；99其他appkey
    */
    private Integer type;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}