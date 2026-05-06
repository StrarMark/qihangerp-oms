package cn.qihangerp.service.impl;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.enums.EnumShopType;
import cn.qihangerp.model.entity.OShop;
import cn.qihangerp.mapper.OShopMapper;
import cn.qihangerp.mapper.SysThirdSystemConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.SysThirdSystemConfig;
import cn.qihangerp.service.SysThirdSystemConfigService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author TW
* @description 针对表【erp_echo_config(ERP系统交互配置表)】的数据库操作Service实现
* @createDate 2024-04-19 11:46:03
*/
@AllArgsConstructor
@Service
public class SysThirdSystemConfigServiceImpl extends ServiceImpl<SysThirdSystemConfigMapper, SysThirdSystemConfig>
    implements SysThirdSystemConfigService {
    private final OShopMapper oShopMapper;

    @Override
    public List<SysThirdSystemConfig> getConfigListBySystemId(Integer systemId) {
        LambdaQueryWrapper<SysThirdSystemConfig> qw = new LambdaQueryWrapper<SysThirdSystemConfig>()
                .eq(SysThirdSystemConfig::getSystemId, systemId);
        return this.baseMapper.selectList(qw);
    }

    @Override
    public List<SysThirdSystemConfig> getConfigListBySystemId(Integer systemId,Long merchantId) {
        LambdaQueryWrapper<SysThirdSystemConfig> qw = new LambdaQueryWrapper<SysThirdSystemConfig>()
                .eq(SysThirdSystemConfig::getMerchantId, merchantId)
                .eq(SysThirdSystemConfig::getSystemId, systemId);
        return this.baseMapper.selectList(qw);
    }

    @Override
    public List<SysThirdSystemConfig> getConfigListBySystemId(Integer systemId, String systemType) {
        LambdaQueryWrapper<SysThirdSystemConfig> qw = new LambdaQueryWrapper<SysThirdSystemConfig>()
                .eq(SysThirdSystemConfig::getSystemId, systemId)
//                .eq(SysThirdSystemConfig::getMerchantId, merchantId)
                .eq(SysThirdSystemConfig::getSystemType, systemType);
        return this.baseMapper.selectList(qw);
    }

    /**
     * 保存第三方店铺配置
     * @param sysThirdSystemConfig
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo saveThirdShopConfig(SysThirdSystemConfig sysThirdSystemConfig) {
        LambdaQueryWrapper<SysThirdSystemConfig> qw = new LambdaQueryWrapper<SysThirdSystemConfig>()
                .eq(SysThirdSystemConfig::getSystemId, sysThirdSystemConfig.getSystemId())
                .eq(SysThirdSystemConfig::getMerchantId, sysThirdSystemConfig.getMerchantId())
                .eq(SysThirdSystemConfig::getSystemType, sysThirdSystemConfig.getSystemType());
        List<SysThirdSystemConfig> list = this.baseMapper.selectList(qw);
        if(list!=null&&list.size()>0){
            // 存在就更新
            sysThirdSystemConfig.setId(list.get(0).getId());
            this.baseMapper.updateById(sysThirdSystemConfig);
        }else{
            // 不存就新增
            this.baseMapper.insert(sysThirdSystemConfig);
        }
        // 添加店铺
        List<OShop> oShops = oShopMapper.selectList(new LambdaQueryWrapper<OShop>()
                .eq(OShop::getShopGroupId, sysThirdSystemConfig.getId())
                .eq(OShop::getType,EnumShopType.TANG_LANG.getIndex())
        );
        if(oShops==null||oShops.size()==0){
            //新增
            OShop oShop = new OShop();
            oShop.setShopGroupId(sysThirdSystemConfig.getId());
            oShop.setName("螳螂系统专用店铺");
            oShop.setType(EnumShopType.TANG_LANG.getIndex());
            oShop.setSort(0);
            oShop.setMerchantId(sysThirdSystemConfig.getMerchantId());
            oShop.setSellerId(sysThirdSystemConfig.getBizId());
            oShop.setRegionId(1L);
            oShop.setApiStatus(0);
            oShop.setStatus("1");
            oShop.setCreateTime(new Date());
            oShopMapper.insert(oShop);
            // 更新上面的店铺id-使用isvSource字段
            SysThirdSystemConfig update = new SysThirdSystemConfig();
            update.setId(sysThirdSystemConfig.getId());
            update.setIsvSource(oShop.getId().toString());
            this.baseMapper.updateById(update);
            return ResultVo.success(oShop.getId());
        }else {
            // 更新上面的店铺id-使用isvSource字段
            SysThirdSystemConfig update = new SysThirdSystemConfig();
            update.setId(sysThirdSystemConfig.getId());
            update.setIsvSource(oShops.get(0).getId().toString());
            this.baseMapper.updateById(update);
            return ResultVo.success(oShops.get(0).getId());
        }
    }
}




