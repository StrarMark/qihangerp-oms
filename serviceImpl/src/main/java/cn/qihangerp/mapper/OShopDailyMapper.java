package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.OShopDaily;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_shop_daily(店铺日报)】的数据库操作Mapper
* @createDate 2025-02-15 18:17:48
* @Entity cn.qihangerp.model.entity.OShopDaily
*/
public interface OShopDailyMapper extends BaseMapper<OShopDaily> {
    List<OShopDaily> shopDailyReport(@Param("startDate") String startDate
            , @Param("endDate") String endDate
            , @Param("regionId") Long regionId
            , @Param("platformId") Long platformId
            , @Param("shopId") Long shopId
            , @Param("manageUserId") Long manageUserId
            , @Param("shopGroupId") Long shopGroupId
    );

    List<OShopDaily> shopRegionReport(@Param("startDate") String startDate
            , @Param("endDate") String endDate
            , @Param("regionId") Long regionId
            , @Param("shopGroupId") Long shopGroupId
    );
}




