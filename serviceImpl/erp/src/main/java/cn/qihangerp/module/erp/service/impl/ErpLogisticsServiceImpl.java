package cn.qihangerp.module.erp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpLogistics;
import cn.qihangerp.module.erp.service.ErpLogisticsService;
import cn.qihangerp.module.erp.mapper.ErpLogisticsMapper;
import org.springframework.stereotype.Service;

/**
* @author 1
* @description 针对表【erp_logistics(采购物流公司表)】的数据库操作Service实现
* @createDate 2025-12-30 08:20:08
*/
@Service
public class ErpLogisticsServiceImpl extends ServiceImpl<ErpLogisticsMapper, ErpLogistics>
    implements ErpLogisticsService{
    @Override
    public int updateStatus(Long id, Integer status) {
        ErpLogistics update = new ErpLogistics();
        update.setId(id);
        update.setStatus(status);
        return this.baseMapper.updateById(update);
    }
}




