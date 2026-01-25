package cn.qihangerp.module.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.model.bo.WeiGoodsSkuBo;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.mapper.WeiGoodsMapper;
import cn.qihangerp.mapper.WeiGoodsSkuMapper;
import cn.qihangerp.module.service.OGoodsService;
import cn.qihangerp.module.service.OGoodsSkuService;
import cn.qihangerp.module.service.WeiGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author TW
* @description 针对表【oms_wei_goods_sku】的数据库操作Service实现
* @createDate 2024-06-03 16:51:29
*/
@AllArgsConstructor
@Service
public class WeiGoodsSkuServiceImpl extends ServiceImpl<WeiGoodsSkuMapper, WeiGoodsSku>
    implements WeiGoodsSkuService {
    private final WeiGoodsSkuMapper mapper;
    private final WeiGoodsMapper weiGoodsMapper;
    private final OGoodsSkuService oGoodsSkuService;
    private final OGoodsService oGoodsService;

    @Override
    public PageResult<WeiGoodsSku> queryPageList(WeiGoodsSkuBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WeiGoodsSku> queryWrapper = new LambdaQueryWrapper<WeiGoodsSku>()
                .eq(bo.getShopId()!=null,WeiGoodsSku::getShopId,bo.getShopId())
                .eq(StringUtils.hasText(bo.getProductId()), WeiGoodsSku::getProductId,bo.getProductId())
                .eq(StringUtils.hasText(bo.getSkuId()), WeiGoodsSku::getSkuId,bo.getSkuId())
                .like(StringUtils.hasText(bo.getSkuCode()), WeiGoodsSku::getSkuCode,bo.getSkuCode())
                .eq(bo.getErpSkuId()!=null, WeiGoodsSku::getErpGoodsSkuId,bo.getErpSkuId())
                ;

        Page<WeiGoodsSku> page = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo) {
        OGoodsSku oGoodsSku = oGoodsSkuService.getById(bo.getErpGoodsSkuId());
        if(oGoodsSku == null) return ResultVo.error("未找到系统商品sku");

        OGoods oGoods=oGoodsService.getById(oGoodsSku.getGoodsId());
        if(oGoods == null){
            return ResultVo.error("未找到系统商品");
        }

        WeiGoodsSku taoGoodsSku = mapper.selectById(bo.getId());
        if(taoGoodsSku == null) {
            return ResultVo.error("WEI商品sku数据不存在");
        }
        List<WeiGoods> jdGoods = weiGoodsMapper.selectList(new LambdaQueryWrapper<WeiGoods>().eq(WeiGoods::getProductId, taoGoodsSku.getProductId()));
        if(jdGoods==null||jdGoods.size()==0){
            return ResultVo.error("WEI商品数据不存在");
        }

        WeiGoodsSku sku = new WeiGoodsSku();
        sku.setId(bo.getId());
        sku.setErpGoodsId(oGoodsSku.getGoodsId());
        sku.setErpGoodsSkuId(oGoodsSku.getId());
        mapper.updateById(sku);

        WeiGoods goodsUp=new WeiGoods();
        goodsUp.setId(jdGoods.get(0).getId());
        goodsUp.setErpGoodsId(oGoodsSku.getGoodsId());
        weiGoodsMapper.updateById(goodsUp);
        return ResultVo.success();
    }
}




