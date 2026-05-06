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
 * @TableName erp_outer_system_feedback
 */
@TableName(value ="sys_third_system_feedback")
@Data
public class SysThirdSystemFeedback implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 推送类型10运单号回传20单据取消结果回传30物流轨迹信息回传
     */
    private Integer type;

    /**
     * 订单号
     */
    private String orderNum;

    /**
     * feedback日期
     */
    private Date date;

    /**
     * feedback时间
     */
    private Date time;

    /**
     * 推送参数JSON
     */
    private String params;

    /**
     * 推送返回结果
     */
    private String result;

    /**
     * 商户ID
     */
    private Long merchantId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}