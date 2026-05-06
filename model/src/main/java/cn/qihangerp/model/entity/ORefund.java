package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 退换货表
 * @TableName o_refund
 */
@Data
public class ORefund implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 退货单号
     */
    private String refundNum;

    /**
     * 类型(10-退货 20-换货 30-维修 40-大家电安装 50-大家电移机 60-大家电增值服务 70-上门维修 90-优鲜赔 80-补发商品 100-试用收回 11-仅退款)
     */
    private Integer refundType;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺类型
     */
    private Integer shopType;

    /**
     * 源订单号
     */
    private String orderNum;
    private Double orderAmount;
    private Double refundFee;
    private String refundReason;

    /**
     * 子订单号或id
     */
    private String orderItemNum;

    /**
     * 源skuId
     */
    private String skuId;

    /**
     * erp商品id
     */
    private Long goodsId;

    /**
     * erp sku id
     */
    private Long goodsSkuId;

    private Integer hasGoodReturn;

    /**
     * sku编码
     */
    private String skuNum;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品sku
     */
    private String goodsSku;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 退货数量
     */
    private Integer quantity;

    /**
     * 退货物流公司
     */
    private String returnLogisticsCompany;
    private String sendLogisticsCompany;

    /**
     * 退货物流单号
     */
    private String returnLogisticsCode;
    private String sendLogisticsCode;

    /**
     * 收货时间
     */
    private Date receiveTime;

    /**
     * 备注
     */
    private String remark;



    /**
     * 状态（10001待审核 10002等待买家退货 10003等待平台审核 10004待买家处理 10005等待卖家处理 10006等待卖家发货 14000拒绝退款 10011退款关闭 10010退款完成 10020售后成功 10021售后失败 10090退款中 10091换货成功 10092换货失败 10093维修关闭 10094维修成功 ）
     *
     * 新状态：售后状态 0：售后申请 1：售后关闭，2：售后处理中，3：退款中，4： 售后成功
     */
    private Integer status;

    /**
     * 订单创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    private Integer erpPushStatus;
    private String erpPushResult;
    private Date erpPushTime;
    /**
     * 是否处理0未处理1已处理9无需处理
     */
    private Integer hasProcessing;
    private Integer processType;
    private String afterSaleId;
    //商户id
    private Long merchantId;
    //平台状态
    private String platformStatus;
    //平台状态文本
    private String platformStatusText;
    //ERP状态0待处理10已退款21退货中22已退货退款31换货中32换货完成41补发中42补发完成
    private Integer erpStatus;


    /**
     * 订单发货状态 0:未发货， 1:已发货（包含：已发货，已揽收）
     */
    private Integer shippingStatus;


    /**
     * 换货商品规格ID（平台）
     */
    private String exchangeSkuId;

    /**
     * 换货商品名称
     */
    private String exchangeGoodsName;

    /**
     * 换货商品价格
     */
    private Integer exchangeGoodsPrice;

    /**
     * 申请换货的数量
     */
    private Integer exchangeGoodsNum;


    /**
     * 换货商品库SkuId
     */
    private Long exchangeErpGoodsSkuId;

    /**
     * 申请换货的ERP订单id
     */
    private Long exchangeErpOrderId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}