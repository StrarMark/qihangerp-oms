package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店铺虚拟商品发货实物商品表
 * @TableName oms_shop_goods_sku_ship_item
 */
@TableName(value ="oms_shop_goods_sku_ship_item")
@Data
public class ShopGoodsSkuShipItem {
    /**
     * 
     */
    @Schema(description = "keyId")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺商品id
     */
    private Long shopGoodsId;

    /**
     * 店铺商品skuid
     */
    private Long shopGoodsSkuId;

    /**
     * 平台商品id
     */
    private String productId;

    /**
     * 平台商品skuid
     */
    private String productSkuId;

    /**
     * 商品名
     */
    private String goodsName;

    /**
     * 商家商品编码
     */
    private String goodsNum;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * sku小图
     */
    @Schema(description = "colorImage")
    private String img;

    /**
     * sku名称
     */
    private String skuName;

    private Integer quantity;

    /**
     * erp系统商品id
     */
    @Schema(description = "goodsId")
    private Long erpGoodsId;

    /**
     * erp系统商品skuid
     */
    @Schema(description = "id")
    private Long erpGoodsSkuId;

    /**
     * 创建时间
     */
    private LocalDateTime createOn;


}