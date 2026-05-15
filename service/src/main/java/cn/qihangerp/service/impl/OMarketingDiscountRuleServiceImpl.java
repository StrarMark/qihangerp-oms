package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.request.OMarketingDiscountRuleAdd;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.OMarketingDiscountRule;
import cn.qihangerp.service.OMarketingDiscountRuleService;
import cn.qihangerp.mapper.OMarketingDiscountRuleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
* @author 1
* @description 针对表【o_marketing_discount_rule(订单折扣规则表（营销模块-手动订单折扣）)】的数据库操作Service实现
* @createDate 2026-04-09 17:27:35
*/
@Service
public class OMarketingDiscountRuleServiceImpl extends ServiceImpl<OMarketingDiscountRuleMapper, OMarketingDiscountRule>
    implements OMarketingDiscountRuleService{

    @Override
    public PageResult<OMarketingDiscountRule> queryPageList(OMarketingDiscountRule bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OMarketingDiscountRule> queryWrapper = new LambdaQueryWrapper<OMarketingDiscountRule>()
                .eq(bo.getStatus()!=null, OMarketingDiscountRule::getStatus, bo.getStatus());

        Page<OMarketingDiscountRule> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    /**
     * 查询商户优惠列表
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OMarketingDiscountRule> queryMerchantPageList(Long merchantId,OMarketingDiscountRule bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OMarketingDiscountRule> queryWrapper = new LambdaQueryWrapper<OMarketingDiscountRule>()
                .eq(bo.getStatus()!=null, OMarketingDiscountRule::getStatus, bo.getStatus());
        // 加入or merchant
        queryWrapper.and(x->
                x.eq(OMarketingDiscountRule::getApplyMerchantId, merchantId)
                        .or()
                        .eq(OMarketingDiscountRule::getApplyMerchantId, 0)
        );



        Page<OMarketingDiscountRule> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public PageResult<OMarketingDiscountRule> queryShopPageList(Long merchantId, Long shopId, OMarketingDiscountRule bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OMarketingDiscountRule> queryWrapper = new LambdaQueryWrapper<OMarketingDiscountRule>()
                .eq(bo.getStatus()!=null, OMarketingDiscountRule::getStatus, bo.getStatus());
        // 加入or merchant
        queryWrapper.and(x->
                x.eq(OMarketingDiscountRule::getApplyMerchantId, merchantId)
                        .or()
                        .eq(OMarketingDiscountRule::getApplyMerchantId, 0)
        );

        // 加入or shop
        queryWrapper.and(x->
                x.eq(OMarketingDiscountRule::getApplyShopId, shopId)
                        .or()
                        .eq(OMarketingDiscountRule::getApplyShopId, 0)
        );

        Page<OMarketingDiscountRule> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ResultVo<Long> add(OMarketingDiscountRuleAdd bo) {
        OMarketingDiscountRule rule = new OMarketingDiscountRule();
        BeanUtils.copyProperties(bo, rule);
        try {
            // 开始时间 结束时间 转换成时间戳
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(bo.getStartTime(), formatter);
            LocalDateTime end = LocalDateTime.parse(bo.getEndTime(), formatter);
            rule.setStartTime(start.atZone(zoneId).toInstant().toEpochMilli() / 1000);
            rule.setEndTime(end.atZone(zoneId).toInstant().toEpochMilli() / 1000);
        }catch (Exception e){
            return ResultVo.error("开始时间-结束时间格式不正确");
        }
        rule.setCreatedTime(new Date());
        rule.setUsedQuota(0);
        if(rule.getStatus()==null){
            rule.setStatus(1);
        }
        this.baseMapper.insert(rule);
        return ResultVo.success(rule.getId());
    }
}




