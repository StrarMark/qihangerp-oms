package cn.qihangerp.module.service;

import cn.qihangerp.model.entity.ErpLogistics;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_logistics(采购物流公司表)】的数据库操作Service
* @createDate 2025-12-30 08:20:08
*/
public interface ErpLogisticsService extends IService<ErpLogistics> {
    int updateStatus(Long id,Integer status);
}
