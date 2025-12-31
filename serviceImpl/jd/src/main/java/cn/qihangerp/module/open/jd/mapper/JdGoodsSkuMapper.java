package cn.qihangerp.module.open.jd.mapper;

import cn.qihangerp.model.entity.JdGoodsSku;
import cn.qihangerp.model.vo.JdGoodsSkuListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
* @author qilip
* @description 针对表【oms_jd_goods_sku(京东商品SKU表)】的数据库操作Mapper
* @createDate 2025-05-19 18:50:56
* @Entity cn.qihangerp.model.entity.JdGoodsSku
*/
public interface JdGoodsSkuMapper extends BaseMapper<JdGoodsSku> {
    IPage<JdGoodsSkuListVo> selectSkuPageList(Page<JdGoodsSku> page,
                                              @Param("shopId") Integer shopId,
                                              @Param("wareId") Long wareId,
                                              @Param("skuId") Long skuId,
                                              @Param("outerId") String outerId,
                                              @Param("hasLink") Integer hasLink
    );
}




