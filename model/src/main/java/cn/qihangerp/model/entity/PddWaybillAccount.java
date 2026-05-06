package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 抖店电子面单账户信息表
 * @TableName oms_pdd_waybill_account
 */
@TableName(value ="oms_pdd_waybill_account")
@Data
public class PddWaybillAccount implements Serializable {
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
     * 商家ID
     */
    private String sellerId;

    /**
     * 快递公司ID
     */
    private String wpCode;

    /**
     * 物流服务商业务类型
     */
    private Integer wpType;

    /**
     * 网点Code
     */
    private String branchCode;

    /**
     * 网点名称
     */
    private String branchName;

    /**
     * 电子面单余额数量，-1表示没有额度限制
     */
    private Long quantity;

    /**
     * 已用面单数量
     */
    private Long allocatedQuantity;

    /**
     * 取消的面单总数
     */
    private Long cancelQuantity;

    /**
     * 已回收用面单数量
     */
    private Long recycledQuantity;

    /**
     * 
     */
    private String country;

    /**
     * 省名称（一级地址）
     */
    private String province;

    /**
     * 市名称（二级地址）
     */
    private String city;

    /**
     * 区名称（三级地址）
     */
    private String district;

    /**
     * 
     */
    private String street;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 发货人
     */
    private String name;

    /**
     * 发货手机号
     */
    private String mobile;

    /**
     * 发货固定电话
     */
    private String phone;

    /**
     * 供应商id集合
     */
    private String supplierIds;

    /**
     * 打印模版url
     */
    private String templateUrl;

    /**
     * 商户id
     */
    private Long merchantId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}