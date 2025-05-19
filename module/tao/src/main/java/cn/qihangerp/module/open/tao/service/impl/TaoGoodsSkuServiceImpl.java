package cn.qihangerp.module.open.tao.service.impl;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.domain.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.service.OGoodsSkuService;
import cn.qihangerp.module.open.tao.domain.TaoGoods;
import cn.qihangerp.module.open.tao.domain.TaoGoodsSku;
import cn.qihangerp.module.open.tao.domain.bo.TaoGoodsBo;
import cn.qihangerp.module.open.tao.domain.vo.TaoGoodsSkuListVo;
import cn.qihangerp.module.open.tao.mapper.TaoGoodsMapper;
import cn.qihangerp.module.open.tao.mapper.TaoGoodsSkuMapper;
import cn.qihangerp.module.open.tao.service.TaoGoodsSkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author TW
* @description 针对表【tao_goods_sku】的数据库操作Service实现
* @createDate 2024-02-29 19:01:35
*/
@AllArgsConstructor
@Service
public class TaoGoodsSkuServiceImpl extends ServiceImpl<TaoGoodsSkuMapper, TaoGoodsSku>
    implements TaoGoodsSkuService {
    private final TaoGoodsSkuMapper mapper;
    private final TaoGoodsMapper goodsMapper;
    private final OGoodsSkuService oGoodsSkuService;

    @Override
    public PageResult<TaoGoodsSkuListVo> queryPageList(TaoGoodsBo bo, PageQuery pageQuery) {
        IPage<TaoGoodsSkuListVo> result = mapper.selectSkuPageList(pageQuery.build(), bo.getShopId(),bo.getNumIid(),bo.getSkuId(),bo.getOuterId(), bo.getHasLink());
        return PageResult.build(result);
    }

    /**
     * 手动绑定erpGoodsSku
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo) {
        OGoodsSku oGoodsSku = oGoodsSkuService.getById(bo.getErpGoodsSkuId());
        if(oGoodsSku == null) return ResultVo.error("未找到系统商品sku");
        TaoGoodsSku taoGoodsSku = mapper.selectById(bo.getId());
        if(taoGoodsSku == null) {
            return ResultVo.error("Tao商品sku数据不存在");
        }
        TaoGoodsSku sku = new TaoGoodsSku();
        sku.setId(bo.getId());
        sku.setOGoodsId(oGoodsSku.getGoodsId());
        sku.setOGoodsSkuId(bo.getErpGoodsSkuId());
        mapper.updateById(sku);

        TaoGoods goodsUp=new TaoGoods();
        goodsUp.setId(taoGoodsSku.getTaoGoodsId());
        goodsUp.setErpGoodsId(oGoodsSku.getGoodsId());
        goodsMapper.updateById(goodsUp);
        return ResultVo.success();
    }
}




