package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 发货物流轨迹
 * @TableName o_shipment_trace
 */
@TableName(value ="o_shipment_trace")
@Data
public class OShipmentTrace {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long shopId;

    /**
     * 
     */
    private Integer shopType;

    /**
     * 发货id
     */
    private Long shipmentId;

    /**
     * 物流id
     */
    private String logisticsId;

    /**
     * 物流单号
     */
    private String logisticsCode;

    /**
     * 节点说明 ，指明当前节点揽收、派送，签收
     */
    private String action;

    /**
     * 状态发生的时间
     */
    private String statusTime;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 轨迹详细信息
     */
    private String descInfo;

    /**
     * 扫描时间
     */
    private String time;
}