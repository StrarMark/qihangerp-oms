package cn.qihangerp.service.impl;

import cn.qihangerp.common.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.PlatformLogisticsWaybillTemplate;
import cn.qihangerp.service.PlatformLogisticsWaybillTemplateService;
import cn.qihangerp.mapper.PlatformLogisticsWaybillTemplateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 1
* @description 针对表【oms_platform_logistics_waybill_template(平台物流电子面单打印模板)】的数据库操作Service实现
* @createDate 2025-10-13 09:02:37
*/
@Service
public class PlatformLogisticsWaybillTemplateServiceImpl extends ServiceImpl<PlatformLogisticsWaybillTemplateMapper, PlatformLogisticsWaybillTemplate>
    implements PlatformLogisticsWaybillTemplateService{

    @Override
    public List<PlatformLogisticsWaybillTemplate> getLogisticsWaybillTemplate(Integer platformId, String cpCode) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<PlatformLogisticsWaybillTemplate>()
                .eq(PlatformLogisticsWaybillTemplate::getPlatformId, platformId)
                .eq(PlatformLogisticsWaybillTemplate::getCpCode, cpCode));
    }

    @Override
    public ResultVo addAndUpdate(PlatformLogisticsWaybillTemplate template) {

        List<PlatformLogisticsWaybillTemplate> platformLogisticsWaybillTemplates = this.baseMapper.selectList(new LambdaQueryWrapper<PlatformLogisticsWaybillTemplate>()
                .eq(PlatformLogisticsWaybillTemplate::getPlatformId, template.getPlatformId())
                .eq(PlatformLogisticsWaybillTemplate::getCpCode, template.getCpCode())
                        .eq(template.getTemplateId()!=null,PlatformLogisticsWaybillTemplate::getTemplateId,template.getTemplateId())
                .eq(PlatformLogisticsWaybillTemplate::getTemplateName, template.getTemplateName()));
        if(platformLogisticsWaybillTemplates==null||platformLogisticsWaybillTemplates.size()==0){
            this.baseMapper.insert(template);
        }

        return ResultVo.success();
    }
}




