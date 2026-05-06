package cn.qihangerp.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.PddLogisticsTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_pdd_logistics_template】的数据库操作Service
* @createDate 2025-06-21 11:39:04
*/
public interface PddLogisticsTemplateService extends IService<PddLogisticsTemplate> {
    ResultVo<Long> saveAndUpdate(PddLogisticsTemplate template);
    List<PddLogisticsTemplate> queryListByWpCode(String wpCode);
}
