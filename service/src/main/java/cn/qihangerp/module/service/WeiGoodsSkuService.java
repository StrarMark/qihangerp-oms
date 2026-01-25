package cn.qihangerp.module.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.model.bo.WeiGoodsSkuBo;
import cn.qihangerp.model.entity.WeiGoodsSku;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_wei_goods_sku】的数据库操作Service
* @createDate 2025-05-20 16:30:57
*/
public interface WeiGoodsSkuService extends IService<WeiGoodsSku> {
    PageResult<WeiGoodsSku> queryPageList(WeiGoodsSkuBo bo, PageQuery pageQuery);
    ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo);
}
