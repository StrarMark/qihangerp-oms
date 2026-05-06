package cn.qihangerp.mapper;

import cn.qihangerp.model.vo.CloudWarehouseGoodsStockVo;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 1
* @description 针对表【erp_cloud_warehouse_goods_stock(云仓商品库存)】的数据库操作Mapper
* @createDate 2025-08-09 11:34:34
* @Entity cn.qihangerp.module.stock.domain.ErpCloudWarehouseGoodsStock
*/
public interface ErpWarehouseGoodsStockMapper extends BaseMapper<ErpWarehouseGoodsStock> {
    IPage<ErpWarehouseGoodsStock> selectPageList(Page<ErpWarehouseGoodsStock> page,
                                              @Param("merchantId") Long merchantId,
                                              @Param("warehouseType") String warehouseType,
                                              @Param("warehouseId") Long warehouseId,
                                              @Param("goodsNo") String goodsNo,
                                              @Param("sellerGoodsSign") String sellerGoodsSign,
                                              @Param("goodsName") String goodsName
    );

    List<CloudWarehouseGoodsStockVo> selectExportList(@Param("merchantId") Long merchantId, @Param("warehouseId")Long warehouseId);
}




