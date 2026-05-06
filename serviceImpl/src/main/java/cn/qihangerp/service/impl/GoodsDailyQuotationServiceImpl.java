package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.GoodsDailyQuotation;
import cn.qihangerp.service.GoodsDailyQuotationService;
import cn.qihangerp.mapper.GoodsDailyQuotationMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author 1
* @description 针对表【erp_gold_price(金价表)】的数据库操作Service实现
* @createDate 2026-04-09 14:30:14
*/
@Service
public class GoodsDailyQuotationServiceImpl extends ServiceImpl<GoodsDailyQuotationMapper, GoodsDailyQuotation>
    implements GoodsDailyQuotationService {


    @Override
    public PageResult<GoodsDailyQuotation> queryPageList(GoodsDailyQuotation bo, PageQuery pageQuery) {
        LambdaQueryWrapper<GoodsDailyQuotation> queryWrapper = new LambdaQueryWrapper<GoodsDailyQuotation>()
                .eq(StringUtils.hasText(bo.getPriceDate()), GoodsDailyQuotation::getPriceDate, bo.getPriceDate());

        Page<GoodsDailyQuotation> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public GoodsDailyQuotation queryNewGoodsDailyQuotation(Integer priceType) {
        LambdaQueryWrapper<GoodsDailyQuotation> queryWrapper = new LambdaQueryWrapper<GoodsDailyQuotation>()
                .eq(GoodsDailyQuotation::getStatus, 0)
                .eq(GoodsDailyQuotation::getPriceType, priceType);
        queryWrapper.orderByDesc(GoodsDailyQuotation::getCreateTime);
        queryWrapper.last("limit 1");
        var list = this.baseMapper.selectList(queryWrapper);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }

    @Override
    public ResultVo<Long> add(GoodsDailyQuotation bo) {
        if(!StringUtils.hasText(bo.getPriceDate())) {
            return ResultVo.error("报价日期不能为空");
        }
        if(bo.getPriceType()==null) {
            return ResultVo.error("报价类型不能为空");
        }
        if(bo.getPrice1()==null) return ResultVo.error("金价不能为空");
        if(bo.getPrice2()==null) return ResultVo.error("银价不能为空");
        if(bo.getPrice3()==null) return ResultVo.error("工费不能为空");
        List<GoodsDailyQuotation> goodsDailyQuotations = this.baseMapper.selectList(new LambdaQueryWrapper<GoodsDailyQuotation>().eq(StringUtils.hasText(bo.getPriceDate()), GoodsDailyQuotation::getPriceDate, bo.getPriceDate()));
        if(goodsDailyQuotations.size()>0) {
            return ResultVo.error(bo.getPriceDate()+"报价已存在");
        }
        GoodsDailyQuotation goodsDailyQuotation = new GoodsDailyQuotation();
        goodsDailyQuotation.setPriceDate(bo.getPriceDate());
        goodsDailyQuotation.setPriceType(bo.getPriceType());
        goodsDailyQuotation.setPrice1(bo.getPrice1());
        goodsDailyQuotation.setPrice2(bo.getPrice2());
        goodsDailyQuotation.setPrice3(bo.getPrice3());
        goodsDailyQuotation.setStatus(0);
        goodsDailyQuotation.setCreateTime(new Date());
        this.baseMapper.insert(goodsDailyQuotation);
        return ResultVo.success(goodsDailyQuotation.getId());
    }

    @Override
    public ResultVo<Long> update(GoodsDailyQuotation bo) {
        if(bo.getId()==null) return ResultVo.error("ID不能为空");
        if(bo.getPrice1()==null) return ResultVo.error("金价不能为空");
        if(bo.getPrice2()==null) return ResultVo.error("银价不能为空");
        if(bo.getPrice3()==null) return ResultVo.error("工费不能为空");
        bo.setUpdateTime(new Date());
        bo.setCreateTime(null);
        bo.setPriceDate(null);
        bo.setPriceType(null);
        this.baseMapper.updateById(bo);
        return ResultVo.success(bo.getId());
    }
}




