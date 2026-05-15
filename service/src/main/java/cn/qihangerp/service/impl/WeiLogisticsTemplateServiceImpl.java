package cn.qihangerp.service.impl;

import cn.qihangerp.common.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.WeiLogisticsTemplate;
import cn.qihangerp.service.WeiLogisticsTemplateService;
import cn.qihangerp.mapper.WeiLogisticsTemplateMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_wei_logistics_template】的数据库操作Service实现
* @createDate 2025-02-24 15:14:11
*/
@AllArgsConstructor
@Service
public class WeiLogisticsTemplateServiceImpl extends ServiceImpl<WeiLogisticsTemplateMapper, WeiLogisticsTemplate>
    implements WeiLogisticsTemplateService {
    private final WeiLogisticsTemplateMapper templateMapper;

    @Override
    public ResultVo<Long> saveAndUpdate(WeiLogisticsTemplate template) {
        List<WeiLogisticsTemplate> templateList = templateMapper.selectList(
                new LambdaQueryWrapper<WeiLogisticsTemplate>()
                        .eq(WeiLogisticsTemplate::getLogisticsCode, template.getLogisticsCode())
                        .eq(WeiLogisticsTemplate::getType,template.getType())
        );
        if(templateList.isEmpty()){
            templateMapper.insert(template);
        }else{
            template.setId(templateList.get(0).getId());
            templateMapper.updateById(template);
        }
        return ResultVo.success();
    }

    @Override
    public List<WeiLogisticsTemplate> getByLogisticsCode(String logisticsCode) {
        List<WeiLogisticsTemplate> templateList = templateMapper.selectList(
                new LambdaQueryWrapper<WeiLogisticsTemplate>()
                        .eq(WeiLogisticsTemplate::getLogisticsCode, logisticsCode));
        if(templateList.isEmpty())
            return new ArrayList<>();
        else return templateList;
    }
}




