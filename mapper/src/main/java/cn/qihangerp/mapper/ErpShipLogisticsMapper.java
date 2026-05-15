package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.ErpShipLogistics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 发货常用快递公司表 Mapper 接口
 */
public interface ErpShipLogisticsMapper extends BaseMapper<ErpShipLogistics> {

    /**
     * 根据实体类型和实体ID查询常用快递公司列表
     */
    java.util.List<ErpShipLogistics> selectByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    /**
     * 根据实体类型和实体ID取消所有默认设置
     */
    int cancelDefault(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    /**
     * 检查快递公司是否已存在
     */
    int checkExists(@Param("entityType") String entityType, @Param("entityId") Long entityId, @Param("logisticsId") Long logisticsId, @Param("shopType") Integer shopType);

    /**
     * 查询实体的默认快递公司
     */
    ErpShipLogistics selectDefault(@Param("entityType") String entityType, @Param("entityId") Long entityId);
}