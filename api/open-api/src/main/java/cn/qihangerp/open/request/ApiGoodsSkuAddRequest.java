package cn.qihangerp.open.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApiGoodsSkuAddRequest {
   private String outerErpSkuId;	//外部erp商品skuId
   private String skuName;//	sku名
   private String skuCode;//	sku编码
   private String barCode;//产品条形码
   private BigDecimal purPrice;//	采购价
   //   private BigDecimal wholePrice;//	批发价（暂时没有用）
   private BigDecimal retailPrice;//	零售价
   private String color;//	颜色
   private String size;//	尺码
   private String style;//	款式
   private String volume;//	体积
   private Integer length;//	长度（mm)
   private Integer width;//	宽度（mm)
   private Integer height;//	高度（mm)
   private Double weight;//	重量（g)
   private String image;//	sku图片地址
   private String unit;

}
