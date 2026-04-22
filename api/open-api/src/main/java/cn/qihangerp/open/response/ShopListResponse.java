package cn.qihangerp.open.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺
 * @TableName sys_shop
 */
@Data
public class ShopListResponse implements Serializable {
    /**
     * 主键
     */
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
    private String status;


    /**
     * 描述
     */
    private String remark;

    /**
     * 第三方平台店铺id，淘宝天猫开放平台使用
     */
    private String sellerId;



    private String province;
    private String city;
    private String district;
    private String address;
    private Long merchantId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;


    private static final long serialVersionUID = 1L;


}