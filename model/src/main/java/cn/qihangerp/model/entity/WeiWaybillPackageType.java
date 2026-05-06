package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频号小店电子面单取号包裹类型
 * @TableName oms_wei_waybill_package_type
 */
@TableName(value ="oms_wei_waybill_package_type")
@Data
public class WeiWaybillPackageType implements Serializable {
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
     * 快递公司编码
     */
    private String deliveryId;

    /**
     * 枚举值
     */
    private String value;

    /**
     * 枚举描述
     */
    private String label;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}