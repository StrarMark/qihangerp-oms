package cn.qihangerp.service.impl;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.mapper.DouLogisticsTemplateMapper;
import cn.qihangerp.model.entity.DouLogisticsTemplate;
import cn.qihangerp.service.DouLogisticsTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_dou_logistics_template】的数据库操作Service实现
* @createDate 2025-02-24 13:22:14
*/
@AllArgsConstructor
@Service
public class DouLogisticsTemplateServiceImpl extends ServiceImpl<DouLogisticsTemplateMapper, DouLogisticsTemplate>
    implements DouLogisticsTemplateService {
    private final DouLogisticsTemplateMapper templateMapper;

    @Override
    public ResultVo<Long> saveAndUpdate(DouLogisticsTemplate template) {
        List<DouLogisticsTemplate> templateList = templateMapper.selectList(new LambdaQueryWrapper<DouLogisticsTemplate>().eq(DouLogisticsTemplate::getTemplateId, template.getTemplateId()));
        if(templateList.isEmpty()){
            templateMapper.insert(template);
        }else{
            template.setId(templateList.get(0).getId());
            templateMapper.updateById(template);
        }
        return ResultVo.success();
    }

    @Override
    public List<DouLogisticsTemplate> getLogisticsTemplateByCode(String logisticsCode) {
        List<DouLogisticsTemplate> templateList = templateMapper.selectList(new LambdaQueryWrapper<DouLogisticsTemplate>()
                .eq(DouLogisticsTemplate::getLogisticsCode, logisticsCode));
        if(templateList.isEmpty())
            return null;
        else return templateList;
    }
}
