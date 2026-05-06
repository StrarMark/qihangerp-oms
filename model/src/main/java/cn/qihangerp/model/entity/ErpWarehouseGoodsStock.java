package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 云仓商品库存
 * @TableName erp_cloud_warehouse_goods_stock
 */
@TableName(value ="erp_warehouse_goods_stock")
@Data
public class ErpWarehouseGoodsStock {
    /**
     *  主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;

    /**
     * CLPS商品编码（事业部商品）
     */
    private String goodsNo;
    /**
     * 商家商品编码
     */
    private String erpGoodsNo;

    /**
     * 商品商家标识
     */
    private String erpGoodsSign;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 事业部编号
     */
    private String ownerNo;

    /**
     * 事业部名称
     */
    private String ownerName;

    /**
     * 云仓仓库编码
     */
    private String warehouseNo;

    /**
     * 云仓仓库名称
     */
    private String warehouseName;

    /**
     * 商家商品编码
     */
    private String sellerGoodsSign;

    /**
     * 库存状态：1-良品；2-残品
     */
    private Integer stockStatus;

    /**
     * 库存类型：1-可销售；2-可退品；3-商家预留；4-仓库锁定；5-临期锁定；6-盘点锁定；7-内配出库锁定；8-在途库存；9-质押；10-VMI锁定；11-过期锁定；13-在途差异
     */
    private Integer stockType;

    /**
     * 商品总库存
     */
    private Integer totalNum;

    /**
     * 商品可用库存
     */
    private Integer usableNum;

    /**
     * 扩展字段
     */
    private String ext1;

    /**
     * 商品总库存（Double型）支持1-9位数，最多保留小数点后四位
     */
    private Double totalNumValue;

    /**
     * 商品可用库存（Double型）支持1-9位数，最多保留小数点后四位
     */
    private Double usableNumValue;

    /**
     * 商品id(o_goods外键)
     */
    private Long erpGoodsId;

    /**
     * 商品skuid(o_goods_sku外键)
     */
    private Long erpGoodsSkuId;

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
    /**
     * 商户店铺id，0代表商户自己
     */
    private Long shopId;

    private Long warehouseId;
    private String warehouseType;
}