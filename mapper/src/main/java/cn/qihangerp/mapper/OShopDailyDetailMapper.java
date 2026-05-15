package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.OShopDailyDetail;
import cn.qihangerp.model.vo.GoodsSaleReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_shop_daily_detail(店铺日报明细（sku级别）)】的数据库操作Mapper
* @createDate 2025-02-11 16:37:22
* @Entity cn.qihangerp.model.entity.OShopDailyDetail
*/
public interface OShopDailyDetailMapper extends BaseMapper<OShopDailyDetail> {
    List<GoodsSaleReport> goodsSaleReport(@Param("startDate") String startDate
            , @Param("endDate") String endDate
            , @Param("regionId") Long regionId
            , @Param("platformId") Long platformId
            , @Param("shopId") Long shopId
            , @Param("manageUserId") Long manageUserId
            , @Param("shopGroupId") Long shopGroupId
            , @Param("skuCode") String skuCode
    );
    List<GoodsSaleReport> goodsSaleRegionReport(@Param("startDate") String startDate
            , @Param("endDate") String endDate
            , @Param("regionId") Long regionId
    );
}




