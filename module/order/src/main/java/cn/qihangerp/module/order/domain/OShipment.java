package cn.qihangerp.module.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 发货-发货记录表
 * @TableName o_shipment
 */
@TableName(value ="o_shipment")
@Data
public class OShipment implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 发货类型（1订单发货2商品补发3商品换货）
     */
    private Integer shipmentType;

    /**
     * 发货的所有订单号，以逗号隔开
     */
    private String orderNums;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人手机号
     */
    private String receiverMobile;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String town;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 物流公司编码
     */
    private String logisticsCompanyCode;

    /**
     * 物流单号
     */
    private String logisticsCode;

    /**
     * 物流费用
     */
    private BigDecimal shipmentFee;

    /**
     * 发货时间
     */
    private Date shipmentTime;

    /**
     * 发货操作人
     */
    private String shipmentOperator;

    /**
     * 物流状态（1运输中2已完成）
     */
    private Integer shipmentStatus;

    /**
     * 包裹重量
     */
    private Double packageWeight;

    /**
     * 包裹长度
     */
    private Double packageLength;

    /**
     * 包裹宽度
     */
    private Double packageWidth;

    /**
     * 包裹高度
     */
    private Double packageHeight;

    /**
     * 打包操作人
     */
    private String packageOperator;

    /**
     * 打包时间
     */
    private Date packageTime;

    /**
     * 包裹内容JSON
     */
    private String packages;

    /**
     * 备注
     */
    private String remark;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private String createBy;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Date updateBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}