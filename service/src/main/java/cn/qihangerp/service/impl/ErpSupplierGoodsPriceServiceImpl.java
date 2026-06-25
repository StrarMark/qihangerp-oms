package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpSupplierGoodsPrice;
import cn.qihangerp.service.ErpSupplierGoodsPriceService;
import cn.qihangerp.mapper.ErpSupplierGoodsPriceMapper;
import org.springframework.stereotype.Service;

/**
* @author 1
* @description 针对表【erp_supplier_goods_price(供应商商品报价表)】的数据库操作Service实现
* @createDate 2026-05-02 09:59:53
*/
@Service
public class ErpSupplierGoodsPriceServiceImpl extends ServiceImpl<ErpSupplierGoodsPriceMapper, ErpSupplierGoodsPrice>
    implements ErpSupplierGoodsPriceService{

    @Override
    public PageResult<ErpSupplierGoodsPrice> queryPageList(ErpSupplierGoodsPrice query, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpSupplierGoodsPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getSupplierId() != null, ErpSupplierGoodsPrice::getSupplierId, query.getSupplierId());
        wrapper.eq(query.getStatus() != null, ErpSupplierGoodsPrice::getStatus, query.getStatus());
        wrapper.eq(query.getMerchantId() != null, ErpSupplierGoodsPrice::getMerchantId, query.getMerchantId());
        wrapper.like(StringUtils.isNotBlank(query.getSkuCode()), ErpSupplierGoodsPrice::getSkuCode, query.getSkuCode());
        wrapper.orderByDesc(ErpSupplierGoodsPrice::getCreateTime);
        Page<ErpSupplierGoodsPrice> page = this.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);
        return PageResult.build(page);
    }
}




