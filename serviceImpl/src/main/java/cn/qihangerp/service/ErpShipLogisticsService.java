package cn.qihangerp.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpShipLogistics;

import java.util.List;

/**
 * 发货常用快递公司表 Service 接口
 */
public interface ErpShipLogisticsService {

    /**
     * 根据实体类型和实体ID查询常用快递公司列表
     */
    List<ErpShipLogistics> queryListByEntity(String entityType, Long entityId);

    /**
     * 添加常用快递公司
     */
    ResultVo<Integer> add(ErpShipLogistics entity);

    /**
     * 删除常用快递公司
     */
    ResultVo<Integer> delete(Long id);

    /**
     * 设置默认快递公司
     */
    ResultVo<Integer> setDefault(Long id);

    /**
     * 查询实体的默认快递公司
     */
    ErpShipLogistics getDefault(String entityType, Long entityId);
}