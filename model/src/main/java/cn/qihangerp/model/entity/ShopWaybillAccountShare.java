/*
 * @Deprecated
 * @description 该类已废弃，不再使用
 * @see cn.qihangerp.model.entity.OmsShopWaybillAccount
 * @see cn.qihangerp.model.entity.OmsShopWaybillAccountShare
 */
package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
  * 店铺电子面单账户共享表（共享给云仓、供应商）
  * @TableName oms_shop_waybill_account_share
  */
 @TableName(value ="oms_shop_waybill_account_share")
 @Data
 public class ShopWaybillAccountShare implements Serializable {
     /**
      *
      */
     @TableId(type = IdType.AUTO)
     private Long id;

     /**
      * 供应商（云仓）id
      */
     private Long shipperId;
     private String shipperName;
     private Integer shipperType;//类型10系统云仓
     private Integer type;//类型0自有1商户共享

     /**
      * 店铺id
      */
     private Long shopId;

     /**
      * 店铺类型
      */
     private Integer shopType;

     /**
      * 平台店铺id，全局唯一，一个店铺分配一个shop_id
      */
     private String sellerShopId;

     /**
      * 快递公司编码
      */
     private String deliveryId;

     /**
      * 快递公司类型1：加盟型 2：直营型
      */
     private Integer companyType;

     /**
      * 网点编码
      */
     private String siteCode;

     /**
      * 网点名称
      */
     private String siteName;

     /**
      * 电子面单账号id，每绑定一个网点分配一个acct_id
      */
     private Long acctId;

     /**
      * 面单账号类型0：普通账号 1：共享账号
      */
     private Integer acctType;

     /**
      * 面单账号状态
      */
     private Integer status;

     /**
      * 面单余额
      */
     private Long available;

     /**
      * 累积已取单
      */
     private Long allocated;

     /**
      * 累计已取消
      */
     private Long cancel;

     /**
      * 累积已回收
      */
     private Long recycled;

     /**
      * 月结账号，company_type 为直营型时有效
      */
     private String monthlyCard;

     /**
      * 网点信息JSON
      */
     private String siteInfo;

     /**
      * 省名称（一级地址）
      */
     private String senderProvince;

     /**
      * 市名称（二级地址）
      */
     private String senderCity;

     /**
      *
      */
     private String senderCounty;

     /**
      *
      */
     private String senderStreet;

     /**
      * 详细地址
      */
     private String senderAddress;

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
      * 是否前台显示1显示0不显示
      */
     private Integer isShow;

     /**
      * 商户id
      */
     private Long merchantId;
     private Long originAccountId;

     /**
      * 打印模版url
      */
     private String templateUrl;

     @TableField(exist = false)
     private static final long serialVersionUID = 1L;
 }
