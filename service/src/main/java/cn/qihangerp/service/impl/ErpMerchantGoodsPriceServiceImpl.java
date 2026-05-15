package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.bo.MerchantGoodsPriceAddRequest;
import cn.qihangerp.model.entity.ErpMerchant;
import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.mapper.ErpMerchantMapper;
import cn.qihangerp.mapper.OGoodsSkuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpMerchantGoodsPrice;
import cn.qihangerp.service.ErpMerchantGoodsPriceService;
import cn.qihangerp.mapper.ErpMerchantGoodsPriceMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* @author 1
* @description 针对表【erp_merchant_goods_price(渠道商户-商品价格历史表（数据来源：总部定价）)】的数据库操作Service实现
* @createDate 2026-03-02 15:04:29
*/
@AllArgsConstructor
@Service
public class ErpMerchantGoodsPriceServiceImpl extends ServiceImpl<ErpMerchantGoodsPriceMapper, ErpMerchantGoodsPrice>
    implements ErpMerchantGoodsPriceService{
    private final OGoodsSkuMapper goodsSkuMapper;
    private final ErpMerchantMapper merchantMapper;

    @Override
    public PageResult<ErpMerchantGoodsPrice> queryPageList(ErpMerchantGoodsPrice bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpMerchantGoodsPrice> queryWrapper = new LambdaQueryWrapper<ErpMerchantGoodsPrice>();
        queryWrapper.eq(bo.getMerchantId()!=null,ErpMerchantGoodsPrice::getMerchantId,bo.getMerchantId());
        queryWrapper.eq(bo.getStatus()!=null,ErpMerchantGoodsPrice::getStatus,bo.getStatus());
        queryWrapper.eq(bo.getGoodsId()!=null,ErpMerchantGoodsPrice::getGoodsId,bo.getGoodsId());
        queryWrapper.eq(bo.getGoodsSkuId()!=null,ErpMerchantGoodsPrice::getGoodsSkuId,bo.getGoodsSkuId());

        Page<ErpMerchantGoodsPrice> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    /**
     * 添加商户价格
     * @param request
     * @param username
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> addPrice(MerchantGoodsPriceAddRequest request, String username) {
        OGoodsSku oGoodsSku = goodsSkuMapper.selectById(request.getGoodsSkuId());
        if (oGoodsSku == null) return ResultVo.error("商品不存在");
        ErpMerchant erpMerchant = merchantMapper.selectById(request.getMerchantId());
        if (erpMerchant == null) return ResultVo.error("商户不存在");

        // 添加最新价格信息
        ErpMerchantGoodsPrice price = new ErpMerchantGoodsPrice();
        price.setGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        price.setGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
        price.setGoodsName(oGoodsSku.getGoodsName());
        price.setSkuName(oGoodsSku.getSkuName());
        price.setSkuCode(oGoodsSku.getSkuCode());
        price.setShopPlatformId(0);
        price.setMerchantId(erpMerchant.getId());
        price.setPurPrice(request.getPrice());
        price.setRetailPrice(request.getPrice());
        price.setStatus(1);
        price.setCreateTime(new Date());
        price.setCreateBy(username);
        this.baseMapper.insert(price);

        return ResultVo.success();
    }
}




