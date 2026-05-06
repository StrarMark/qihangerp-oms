package cn.qihangerp.service.impl;

import cn.qihangerp.common.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.PddLogisticsTemplate;
import cn.qihangerp.service.PddLogisticsTemplateService;
import cn.qihangerp.mapper.PddLogisticsTemplateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_pdd_logistics_template】的数据库操作Service实现
* @createDate 2025-06-21 11:39:04
*/
@Service
public class PddLogisticsTemplateServiceImpl extends ServiceImpl<PddLogisticsTemplateMapper, PddLogisticsTemplate>
    implements PddLogisticsTemplateService{

    @Override
    public ResultVo<Long> saveAndUpdate(PddLogisticsTemplate template) {
        List<PddLogisticsTemplate> templateList = this.baseMapper.selectList(
                new LambdaQueryWrapper<PddLogisticsTemplate>()
                        .eq(PddLogisticsTemplate::getWpCode, template.getWpCode())
                        .eq(PddLogisticsTemplate::getType,template.getType())
                        .eq(PddLogisticsTemplate::getTemplateId,template.getTemplateId())
        );
        if(templateList.isEmpty()){
            this.baseMapper.insert(template);
        }else{
            template.setId(templateList.get(0).getId());
            this.baseMapper.updateById(template);
        }
        return ResultVo.success();
    }

    @Override
    public List<PddLogisticsTemplate> queryListByWpCode(String wpCode) {
        List<PddLogisticsTemplate> templateList = this.baseMapper.selectList(
                new LambdaQueryWrapper<PddLogisticsTemplate>()
                        .eq(PddLogisticsTemplate::getWpCode, wpCode));

        return templateList;
    }
}




