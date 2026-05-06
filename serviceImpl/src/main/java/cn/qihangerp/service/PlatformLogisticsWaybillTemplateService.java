package cn.qihangerp.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.PlatformLogisticsWaybillTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 1
* @description 针对表【oms_platform_logistics_waybill_template(平台物流电子面单打印模板)】的数据库操作Service
* @createDate 2025-10-13 09:02:37
*/
public interface PlatformLogisticsWaybillTemplateService extends IService<PlatformLogisticsWaybillTemplate> {
    List<PlatformLogisticsWaybillTemplate> getLogisticsWaybillTemplate(Integer platformId, String cpCode);
    ResultVo addAndUpdate(PlatformLogisticsWaybillTemplate platformLogisticsWaybillTemplate);
}
