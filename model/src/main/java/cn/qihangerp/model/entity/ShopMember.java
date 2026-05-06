package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName oms_shop_member
 */
@TableName(value ="oms_shop_member")
@Data
public class ShopMember implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺类型
     */
    private Integer shopType;

    /**
     * 平台用户id
     */
    private String platformUserId;

    /**
     * 平台用户账号、手机号
     */
    private String platformAccount;

    /**
     * 平台openid
     */
    private String platformOpenid;

    /**
     * 收件人姓名。订单状态为待发货状态，且订单未在审核中的情况下返回密文数据；
     */
    private String name;

    /**
     * 收件人电话。订单状态为待发货状态，且订单未在审核中的情况下返回密文数据；
     */
    private String phone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区
     */
    private String county;

    /**
     * 街道
     */
    private String town;

    /**
     * 收件人地址，不拼接省市区。加密
     */
    private String address;

    /**
     * 确认状态（0未确认1已确认）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 系统创建时间
     */
    private Date createOn;

    /**
     * 系统更新时间
     */
    private Date updateOn;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}