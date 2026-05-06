package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * OMS售后处理表
 * @TableName o_refund_after_sale
 */
@Data
public class ORefundAfterSale implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 类型（10退货；20换货；80补发；99订单拦截；）
     */
    private Integer type;

    /**
     * 店铺id
     */
    private Long shopId;
    private Long supplierId;
    private Long merchantId;

    /**
     * 店铺类型
     */
    private Integer shopType;
    private Integer hasGoodsSend;

    /**
     * 退款id（o_refund表主键）
     */
    private String refundId;

    /**
     * 订单号
     */
    private String orderNum;
    private String refundNum;

    /**
     * 子订单号
     */
    private String subOrderNum;

    /**
     * 订单id（o_order表主键id）
     */
    private Long oOrderId;

    /**
     * 子订单id（o_order_item表主键id）
     */
    private Long oOrderItemId;

    /**
     * 平台商品skuid
     */
    private String skuId;

    /**
     * 售后数量
     */
    private Integer quantity;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品图片
     */
    private String img;

    /**
     * sku描述
     */
    private String skuInfo;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * 系统商品id（o_goods表主键id）
     */
    private Long oGoodsId;

    /**
     * 系统商品skuId（o_goods表主键id）
     */
    private Long oGoodsSkuId;

    /**
     * 发货物流单号
     */
    private String sendLogisticsCode;

    /**
     * 发货仓库id
     */
    private Long sendWarehouseId;

    /**
     * 发货仓库类型
     */
    private String sendWarehouseType;

    /**
     * 发货仓库名
     */
    private String sendWarehouseName;
    /**
     * 发货类型（0本地仓库发货；100京东云仓发货；200系统云仓发货；300供应商发货）
     */
    private Integer sendShipType;

    /**
     * 退回人信息json
     */
    private String returnInfo;

    /**
     * 退回快递单号
     */
    private String returnLogisticsCode;

    /**
     * 退回物流公司名称
     */
    private String returnLogisticsCompany;
    private Integer returnType;//退回类型（0退回仓库；300退回供应商）
    private Long returnWarehouseId;
    private String returnWarehouseName;
    private String returnWarehouseType;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人联系电话
     */
    private String receiverTel;

    /**
     * 省
     */
    private String receiverProvince;

    /**
     * 市
     */
    private String receiverCity;

    /**
     * 区
     */
    private String receiverTown;

    /**
     * 收件人详细地址
     */
    private String receiverAddress;

    /**
     * 发货快递单号（补发、换货发货）
     */
    private String reissueLogisticsCode;

    /**
     * 发货快递公司
     */
    private String reissueLogisticsCompany;
    private Integer reissueType;//补发、换货类型（0仓库补发换货；300供应商补发换货）
    private Long reissueWarehouseId;
    private String reissueWarehouseName;
    private String reissueWarehouseType;

    /**
     * 状态:0待进一步处理；1已发出；2已完成(已收货);
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
    private String result;


    /**
     * 换货商品规格ID（平台）
     */
    private String exchangeSkuId;
    /**
     * 换货商品库SkuId
     */
    private Long exchangeErpGoodsSkuId;
    private Long exchangeErpGoodsId;
    /**
     * 换货商品名称
     */
    private String exchangeGoodsName;
    private String exchangeGoodsImg;
    private String exchangeGoodsSkuName;
    private String exchangeGoodsSkuCode;



    /**
     * 申请换货的数量
     */
    private Integer exchangeGoodsNum;




    /**
     * 申请换货的ERP订单id
     */
    private Long exchangeErpOrderId;

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
    private String updateBy;



    private static final long serialVersionUID = 1L;
}