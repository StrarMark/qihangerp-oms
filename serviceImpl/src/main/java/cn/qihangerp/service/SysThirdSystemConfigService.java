package cn.qihangerp.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.SysThirdSystemConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author TW
* @description 针对表【erp_echo_config(ERP系统交互配置表)】的数据库操作Service
* @createDate 2024-04-19 11:46:03
*/
public interface SysThirdSystemConfigService extends IService<SysThirdSystemConfig> {
    List<SysThirdSystemConfig> getConfigListBySystemId(Integer systemId );
    List<SysThirdSystemConfig> getConfigListBySystemId(Integer systemId,Long merchantId);
    List<SysThirdSystemConfig> getConfigListBySystemId(Integer systemId,String systemType);
    ResultVo<Long> saveThirdShopConfig(SysThirdSystemConfig sysThirdSystemConfig);
}
