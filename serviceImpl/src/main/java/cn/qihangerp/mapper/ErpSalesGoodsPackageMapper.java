package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.ErpSalesGoodsPackage;
import cn.qihangerp.model.entity.ErpSalesGoodsPackageItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ErpSalesGoodsPackageMapper extends BaseMapper<ErpSalesGoodsPackage> {

    @Select("SELECT * FROM erp_sales_goods_package_item WHERE package_id = #{packageId}")
    List<ErpSalesGoodsPackageItem> selectItemsByPackageId(@Param("packageId") Long packageId);
}