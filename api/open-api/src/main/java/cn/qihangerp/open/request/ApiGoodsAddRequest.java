package cn.qihangerp.open.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApiGoodsAddRequest {
   private String goodsNum;	//商品编号
   private String goodsName;//	商品名称
   private String goodsImage;//	商品图片
   private String outerErpGoodsId;//	外部erp商品编码
   private BigDecimal purPrice;//	采购价
//   private BigDecimal wholePrice;//	批发价（暂时没有用）
   private BigDecimal retailPrice;//	零售价
   private Long merchantId;//商户ID
   private String sellerId;//卖家ID(外部系统使用)
   private String sellerBrandId;//卖家品牌ID(外部系统使用)
   private List<ApiGoodsSkuAddRequest> skus;
}
