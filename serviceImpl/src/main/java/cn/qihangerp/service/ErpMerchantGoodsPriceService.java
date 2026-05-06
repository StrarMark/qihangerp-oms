package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.bo.MerchantGoodsPriceAddRequest;
import cn.qihangerp.model.entity.ErpMerchantGoodsPrice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_merchant_goods_price(渠道商户-商品价格历史表（数据来源：总部定价）)】的数据库操作Service
* @createDate 2026-03-02 15:04:29
*/
public interface ErpMerchantGoodsPriceService extends IService<ErpMerchantGoodsPrice> {
    PageResult<ErpMerchantGoodsPrice> queryPageList(ErpMerchantGoodsPrice bo, PageQuery pageQuery);
    ResultVo<Long> addPrice(MerchantGoodsPriceAddRequest request, String username);

}
