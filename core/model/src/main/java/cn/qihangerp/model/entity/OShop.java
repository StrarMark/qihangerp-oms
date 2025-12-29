package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺表
 * @TableName o_shop
 */
@TableName(value ="o_shop")
@Data
public class OShop implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺名
     */
    private String name;

    /**
     * 对应第三方平台Id
     */
    private Integer type;

    /**
     * 店铺url
     */
    private String url;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（1正常2已删除）
     */
    private Integer status;

    /**
     * 描述
     */
    private String remark;

    /**
     * 第三方平台店铺id，淘宝天猫开放平台使用
     */
    private Long sellerId;

    /**
     * Appkey
     */
    private String appKey;

    /**
     * Appsercet
     */
    private String appSecret;

    /**
     * 第三方平台sessionKey（access_token）
     */
    private String accessToken;

    /**
     * 到期
     */
    private Long expiresIn;

    /**
     * access_token开始时间
     */
    private Long accessTokenBegin;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 刷新token过期时间
     */
    private Long refreshTokenTimeout;

    /**
     * 请求url
     */
    private String apiRequestUrl;

    /**
     * 回调url
     */
    private String apiRedirectUrl;

    private Integer apiStatus;

    /**
     * 负责人id
     */
    private Long manageUserId;

    /**
     * 负责人部门id
     */
    private Long manageDeptId;

    /**
     * 国家/地区
     */
    private Long regionId;

    /**
     * 更新时间
     */
    private Long modifyOn;

    /**
     * 创建时间
     */
    private Long createOn;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}