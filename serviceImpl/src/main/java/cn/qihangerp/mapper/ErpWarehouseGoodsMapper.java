package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.ErpWarehouseGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_cloud_warehouse_goods】的数据库操作Mapper
* @createDate 2025-07-10 12:50:56
* @Entity cn.qihangerp.module.stock.domain.ErpCloudWarehouseGoods
*/
public interface ErpWarehouseGoodsMapper extends BaseMapper<ErpWarehouseGoods> {
    List<ErpWarehouseGoods> getVendorGoodsByCode(@Param("merchantId") Long merchantId,@Param("warehouseId") Long warehouseId, @Param("code") String code);
    List<ErpWarehouseGoods> getVendorGoodsStockByCode(@Param("merchantId") Long merchantId,@Param("warehouseId") Long warehouseId, @Param("code") String code);
}




