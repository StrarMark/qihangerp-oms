package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.GoodsDailyQuotation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_gold_price(金价表)】的数据库操作Service
* @createDate 2026-04-09 14:30:14
*/
public interface GoodsDailyQuotationService extends IService<GoodsDailyQuotation> {
    PageResult<GoodsDailyQuotation> queryPageList(GoodsDailyQuotation bo, PageQuery pageQuery);

    /**
     * 取最新报价
     * @param  priceType 报价类型：0采购价；1零售价
     * @return
     */
    GoodsDailyQuotation queryNewGoodsDailyQuotation(Integer priceType);
    ResultVo<Long> add(GoodsDailyQuotation bo);
    ResultVo<Long> update(GoodsDailyQuotation bo);
}
