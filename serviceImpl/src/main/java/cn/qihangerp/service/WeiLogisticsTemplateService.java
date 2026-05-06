package cn.qihangerp.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.WeiLogisticsTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_wei_logistics_template】的数据库操作Service
* @createDate 2025-02-24 15:14:11
*/
public interface WeiLogisticsTemplateService extends IService<WeiLogisticsTemplate> {
    ResultVo<Long> saveAndUpdate(WeiLogisticsTemplate template);
    List<WeiLogisticsTemplate> getByLogisticsCode(String logisticsCode);
}
