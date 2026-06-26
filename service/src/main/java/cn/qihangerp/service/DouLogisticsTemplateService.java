package cn.qihangerp.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.DouLogisticsTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_dou_logistics_template】的数据库操作Service
* @createDate 2025-02-24 13:22:14
*/
public interface DouLogisticsTemplateService extends IService<DouLogisticsTemplate> {
    ResultVo<Long> saveAndUpdate(DouLogisticsTemplate template);
    List<DouLogisticsTemplate> getLogisticsTemplateByCode(String logisticsCode);
}
