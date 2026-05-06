package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 外部WMS推送记录
 * @TableName erp_outer_system_push
 */
@TableName(value ="sys_third_system_push")
@Data
public class SysThirdSystemPush implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 推送类型10订单推送20取消订单推送
     */
    private Integer pushType;

    /**
     * 订单库id
     */
    private Long orderId;

    /**
     * 推送次数（重试次数）
     */
    private Integer pushCount;

    /**
     * 推送日期
     */
    private Date pushDate;

    /**
     * 推送时间
     */
    private Date pushTime;

    /**
     * 推送参数JSON
     */
    private String pushParams;

    /**
     * 推送返回结果
     */
    private String pushResult;

    /**
     * 目标系统id（云仓id或三方系统id）
     */
    private Long targetId;

    /**
     * 目标系统名称（用于展示）
     */
    private String targetName;

    /**
     * 外部系统类型100云仓200其他系统
     */
    private Integer targetType;
    private Long merchantId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}