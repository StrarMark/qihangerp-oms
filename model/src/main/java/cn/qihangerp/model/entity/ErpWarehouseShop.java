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
 * @TableName erp_cloud_warehouse_shop
 */
@TableName(value ="erp_warehouse_shop")
@Data
public class ErpWarehouseShop implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long warehouseId;

    /**
     * CLPS事业部编号
     */
    private String ownerNo;

    /**
     * 所有者类型（0自己1商户）
     */
    private Integer ownerType;

    private Long shopId;
    private Integer shopType;

    /**
     * 开放平台店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商家店铺编号
     */
    private String erpShopNo;

    /**
     * 店铺所属的销售平台编号，常用平台枚举详见：https://cloud.jdl.com/#/open-business-document/access-guide/367/54604
     */
    private String salesPlatformSourceNo;

    /**
     * 
     */
    private String salesPlatformSourceName;

    /**
     * 店铺类型：1）销售平台为京东:1-闪购店铺；2-SOP店铺；3-FBP店铺   ；2）其它销售平台:4-其它
     */
    private String type;

    /**
     * 店铺状态：1-启用；2-停用；3-初始；4-待审核；5-驳回
     */
    private String status;

    /**
     * 销售平台店铺编码
     */
    private String salesPlatformShopNo;

    /**
     * 青龙业主号
     */
    private String customerCode;

    /**
     * 店铺联系人
     */
    private String shopContacts;

    /**
     * 联系人电话
     */
    private String shopPhone;

    /**
     * 	
店铺地址
     */
    private String shopAddress;

    /**
     * 邮箱
     */
    private String shopEmail;

    /**
     * 传真
     */
    private String shopFax;

    /**
     * 售后联系人
     */
    private String afterSaleContacts;

    /**
     * 售后地址
     */
    private String afterSaleAddress;

    /**
     * 售后电话
     */
    private String afterSalePhone;

    /**
     * 出库规则标记位（10位字符串）1为是，0为否，若传值不满10位系统将默认进行补0。常用规则标记位：第1位，允许拆单；第2位，允许货到付款订单拆单；第3位，允许单SKU拆分；第4位，允许赠品单独拆分；第5位，是否订单驱动内配；第6位，仅单库房出库
     */
    private String outBoundRules;

    /**
     * 业务模式：1-京仓；2-京云仓；3-闪购
     */
    private String bizType;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 商户ID
     */
    private Long merchantId;
    private String merchantName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}