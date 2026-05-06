package cn.qihangerp.service.impl;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.OShop;
import cn.qihangerp.model.query.ShopDailyRequest;
import cn.qihangerp.model.entity.OShopDailyDetail;
import cn.qihangerp.mapper.OShopDailyDetailMapper;
import cn.qihangerp.mapper.OShopMapper;
import cn.qihangerp.model.vo.GoodsSaleReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.OShopDaily;
import cn.qihangerp.service.OShopDailyService;
import cn.qihangerp.mapper.OShopDailyMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【o_shop_daily(店铺日报)】的数据库操作Service实现
* @createDate 2025-02-11 16:37:22
*/
@AllArgsConstructor
@Service
public class OShopDailyServiceImpl extends ServiceImpl<OShopDailyMapper, OShopDaily>
    implements OShopDailyService{
    private final OShopMapper shopMapper;
    private final OShopDailyMapper dailyMapper;
    private final OShopDailyDetailMapper  dailyDetailMapper;

    @Transactional
    @Override
    public ResultVo<Long> saveShopDaily(OShopDaily bo) {
        if (bo.getOrderTotal() == null) return ResultVo.error(1500, "缺少参数orderTotal");
        if (bo.getOrderAmount() == null) return ResultVo.error(1500, "缺少参数orderAmount");
        if (bo.getShopId() == null) return ResultVo.error(1500, "缺少参数shopId");
//        if(bo.getItemList()==null || bo.getItemList().isEmpty()) return ResultVo.error(1500,"请添加销售商品明细");

        OShop shop = shopMapper.selectById(bo.getShopId());
        if (shop == null) return ResultVo.error(1500, "店铺不存在");
        bo.setPlatformId(shop.getType().longValue());
        bo.setRegionId(shop.getRegionId());
        if (bo.getFalseOrderTotal() == null) bo.setFalseOrderTotal(0);
        if (bo.getFalseOrderAmount() == null) bo.setFalseOrderAmount(BigDecimal.ZERO);
        bo.setTrueOrderTotal(bo.getOrderTotal() - bo.getFalseOrderTotal());
        bo.setTrueOrderAmount(bo.getOrderAmount().subtract(bo.getFalseOrderAmount()));
        if (bo.getAdFee() != null && !bo.getAdFee().equals(BigDecimal.ZERO)) {
            // 计算点击单价
            if (bo.getAdClick() == null) bo.setAdClick(1);
            bo.setAdClickFee(bo.getAdFee().divide(BigDecimal.valueOf(bo.getAdClick()), 2, RoundingMode.HALF_UP));
            // 计算ROI
            if (bo.getFalseOrderAmount() == null) bo.setFalseOrderAmount(BigDecimal.ZERO);
            bo.setAdRoi(bo.getTrueOrderAmount().divide(bo.getAdFee(), 2, RoundingMode.HALF_UP));
        } else {
            bo.setAdClickFee(BigDecimal.ZERO);
            bo.setAdRoi(BigDecimal.ZERO);
        }
        // 计算客单价
        if (bo.getOrderTotal() != null && bo.getOrderTotal()>0) {
            bo.setUnitPrice(bo.getOrderAmount().divide(BigDecimal.valueOf(bo.getOrderTotal()), 2, RoundingMode.HALF_UP));
        } else {
            bo.setUnitPrice(BigDecimal.ZERO);
        }
        bo.setCreateTime(new Date());
        dailyMapper.insert(bo);
        for (OShopDailyDetail item:bo.getItemList()){
            item.setDailyId(bo.getId());
            item.setDate(bo.getDate());
            item.setShopId(bo.getShopId());
            item.setPlatformId(bo.getPlatformId());
            item.setRegionId(bo.getRegionId());
            item.setCreateTime(new Date());
            item.setCreateBy(bo.getCreateBy());
            if(item.getFalseOrderTotal() == null) item.setFalseOrderTotal(0);
            if(item.getFalseOrderAmount()==null)item.setFalseOrderAmount(BigDecimal.ZERO);
            item.setTrueOrderTotal(item.getOrderTotal()-item.getFalseOrderTotal());
            item.setTrueOrderAmount(item.getOrderAmount().subtract(item.getFalseOrderAmount()));
            if (item.getAdFee() != null && !item.getAdFee().equals(BigDecimal.ZERO)) {
                // 计算点击单价
                if (item.getAdClick() == null) item.setAdClick(1);
                item.setAdClickFee(item.getAdFee().divide(BigDecimal.valueOf(item.getAdClick()), 2, RoundingMode.HALF_UP));
                // 计算ROI

                item.setAdRoi(bo.getTrueOrderAmount().divide(item.getAdFee(), 2, RoundingMode.HALF_UP));
            } else {
                item.setAdClickFee(BigDecimal.ZERO);
                item.setAdRoi(BigDecimal.ZERO);
            }
            dailyDetailMapper.insert(item);

        }
        return ResultVo.success();
    }

    @Transactional
    @Override
    public ResultVo<Long> delShopDaily(Long id) {
        dailyMapper.deleteById(id);
        dailyDetailMapper.delete(new LambdaQueryWrapper<OShopDailyDetail>().eq(OShopDailyDetail::getDailyId,id));
        return ResultVo.success();
    }

    @Override
    public ResultVo<Long> updateShopDaily(OShopDaily bo) {
        if (bo.getOrderTotal() == null) return ResultVo.error(1500, "缺少参数orderTotal");
        if (bo.getOrderAmount() == null) return ResultVo.error(1500, "缺少参数orderAmount");
        if (bo.getShopId() == null) return ResultVo.error(1500, "缺少参数shopId");
        if(bo.getItemList()==null || bo.getItemList().isEmpty()) return ResultVo.error(1500,"请添加销售商品明细");
        if (bo.getFalseOrderTotal() == null) bo.setFalseOrderTotal(0);
        if (bo.getFalseOrderAmount() == null) bo.setFalseOrderAmount(BigDecimal.ZERO);
        bo.setTrueOrderTotal(bo.getOrderTotal() - bo.getFalseOrderTotal());
        bo.setTrueOrderAmount(bo.getOrderAmount().subtract(bo.getFalseOrderAmount()));
        if (bo.getAdFee() != null && !bo.getAdFee().equals(BigDecimal.ZERO)) {
            // 计算点击单价
            if (bo.getAdClick() == null) bo.setAdClick(1);
            bo.setAdClickFee(bo.getAdFee().divide(BigDecimal.valueOf(bo.getAdClick()), 2, RoundingMode.HALF_UP));
            // 计算ROI
            if (bo.getFalseOrderAmount() == null) bo.setFalseOrderAmount(BigDecimal.ZERO);
            bo.setAdRoi(bo.getTrueOrderAmount().divide(bo.getAdFee(), 2, RoundingMode.HALF_UP));
        } else {
            bo.setAdClickFee(BigDecimal.ZERO);
            bo.setAdRoi(BigDecimal.ZERO);
        }
        // 计算客单价
        if (bo.getOrderTotal() != null) {
            bo.setUnitPrice(bo.getOrderAmount().divide(BigDecimal.valueOf(bo.getOrderTotal()), 2, RoundingMode.HALF_UP));
        } else {
            bo.setUnitPrice(BigDecimal.ZERO);
        }
        bo.setUpdateTime(new Date());
        dailyMapper.updateById(bo);
        for (OShopDailyDetail item : bo.getItemList()){
            item.setUpdateBy(bo.getUpdateBy());
            item.setUpdateTime(new Date());
            dailyDetailMapper.updateById(item);
        }
        return ResultVo.success();
    }

    @Override
    public List<OShopDaily> searchList(ShopDailyRequest request) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().minusDays(1).format(formatter);
        String endDate = startDate;
        // 日期处理
        if(request.getDates()!=null){
            if(request.getDates().length>1){
                startDate = request.getDates()[0];
                endDate = request.getDates()[1];;
            }else if(request.getDates().length>0){
                startDate = request.getDates()[0];
                endDate = startDate;
            }
        }


//        LambdaQueryWrapper<OShopDaily> qw = new LambdaQueryWrapper<>();
//        qw.ge(OShopDaily::getDate,startDate);
//        qw.le(OShopDaily::getDate,endDate);
//
//        Long shopId = null;
//        List<Long> shopIds = null;
//        if(request.getShopGroupId()==null&&request.getManageUserId()==null) {
//            qw.eq(request.getRegionId() != null, OShopDaily::getRegionId, request.getRegionId());
//            qw.eq(request.getPlatformId() != null, OShopDaily::getPlatformId, request.getPlatformId());
//            qw.eq(request.getShopId() != null, OShopDaily::getShopId, request.getShopId());
//        }else{
//            // 选了 小组 或 负责人
//            if(request.getShopGroupId()!=null){
//                // 店铺小组 查出小组所有店铺
//                List<OShop> oShops = shopMapper.selectList(new LambdaQueryWrapper<OShop>().eq(OShop::getShopGroupId, request.getShopGroupId()).select(OShop::getId));
//                if(!oShops.isEmpty())  shopIds = oShops.stream().map(OShop::getId).collect(Collectors.toList());
//                qw.in(OShopDaily::getShopId,shopIds);
//            }else if(request.getManageUserId()!=null){
//                List<OShop> oShops = shopMapper.selectList(new LambdaQueryWrapper<OShop>().eq(OShop::getManageUserId, request.getManageUserId()).select(OShop::getId));
//                if(!oShops.isEmpty()) shopIds = oShops.stream().map(OShop::getId).collect(Collectors.toList());
//                qw.in(OShopDaily::getShopId,shopIds);
//            }
//        }
//        Long[] shopIdArray = null;
//        if( shopIds !=null &&!shopIds.isEmpty()) shopIdArray = shopIds.toArray(new Long[shopIds.size()]);
//        else shopId = request.getShopId();
//        qw.last("order by date desc,id desc");
//        List<OShopDaily> list = dailyMapper.selectList(qw);
        List<OShopDaily> list = dailyMapper.shopDailyReport(startDate, endDate,request.getRegionId(),request.getPlatformId(), request.getShopId()
                ,request.getManageUserId(),request.getShopGroupId());
        return list;
    }
//    @Override
//    public List<OShopDaily> searchList(ShopDailyRequest request) {
//        // 定义日期格式
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String startDate = LocalDate.now().minusDays(1).format(formatter);
//        String endDate = startDate;
//        // 日期处理
//        if(request.getDates()!=null){
//            if(request.getDates().length>1){
//                startDate = request.getDates()[0];
//                endDate = request.getDates()[1];;
//            }else if(request.getDates().length>0){
//                startDate = request.getDates()[0];
//                endDate = startDate;
//            }
//        }
//
//
//        LambdaQueryWrapper<OShopDaily> qw = new LambdaQueryWrapper<>();
//        qw.ge(OShopDaily::getDate,startDate);
//        qw.le(OShopDaily::getDate,endDate);
//        if(request.getShopGroupId()==null&&request.getManageUserId()==null) {
//            qw.eq(request.getRegionId() != null, OShopDaily::getRegionId, request.getRegionId());
//            qw.eq(request.getPlatformId() != null, OShopDaily::getPlatformId, request.getPlatformId());
//            qw.eq(request.getShopId() != null, OShopDaily::getShopId, request.getShopId());
//        }else{
//            // 选了 小组 或 负责人
//            if(request.getShopGroupId()!=null){
//                // 店铺小组 查出小组所有店铺
//                List<OShop> oShops = shopMapper.selectList(new LambdaQueryWrapper<OShop>().eq(OShop::getShopGroupId, request.getShopGroupId()).select(OShop::getId));
//                List<Long> shopIds = oShops.stream().map(OShop::getId).collect(Collectors.toList());
//                qw.in(OShopDaily::getShopId,shopIds);
//            }else if(request.getManageUserId()!=null){
//                List<OShop> oShops = shopMapper.selectList(new LambdaQueryWrapper<OShop>().eq(OShop::getManageUserId, request.getManageUserId()).select(OShop::getId));
//                List<Long> shopIds = oShops.stream().map(OShop::getId).collect(Collectors.toList());
//                qw.in(OShopDaily::getShopId,shopIds);
//            }
//        }
//        qw.last("order by date desc,id desc");
//        List<OShopDaily> list = dailyMapper.selectList(qw);
//        return list;
//    }


    @Override
    public List<GoodsSaleReport> goodsSaleReport(ShopDailyRequest request) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().minusDays(1).format(formatter);
        String endDate = startDate;
        // 日期处理
        if(request.getDates()!=null){
            if(request.getDates().length>1){
                startDate = request.getDates()[0];
                endDate = request.getDates()[1];
            }else if(request.getDates().length>0){
                startDate = request.getDates()[0];
                endDate = startDate;
            }
        }
//        Long regionId = null;
//        Long platformId = null;
//        Long shopId = null;
//        Long[] shopIds = null;

//        if(request.getShopGroupId()==null&&request.getManageUserId()==null) {
//            regionId = request.getRegionId();
//            platformId = request.getPlatformId();
//            shopId = request.getShopId();
//        }else {
//            // 选了 小组 或 负责人
//            if (request.getShopGroupId() != null) {
//                // 店铺小组 查出小组所有店铺
//                List<OShop> oShops = shopMapper.selectList(new LambdaQueryWrapper<OShop>().eq(OShop::getShopGroupId, request.getShopGroupId()).select(OShop::getId));
//                List<Long> collect = oShops.stream().map(OShop::getId).collect(Collectors.toList());
//                if(!collect.isEmpty()) shopIds = collect.toArray(new Long[collect.size()]);
//            } else if (request.getManageUserId() != null) {
//                List<OShop> oShops = shopMapper.selectList(new LambdaQueryWrapper<OShop>().eq(OShop::getManageUserId, request.getShopGroupId()).select(OShop::getId));
//                List<Long> collect = oShops.stream().map(OShop::getId).collect(Collectors.toList());
//                if(!collect.isEmpty()) shopIds = collect.toArray(new Long[collect.size()]);
//            }
//        }


        return dailyDetailMapper.goodsSaleReport(startDate,endDate,request.getRegionId(),request.getPlatformId()
                , request.getShopId(), request.getManageUserId(),request.getShopGroupId(),request.getSkuCode());
    }

    @Override
    public List<GoodsSaleReport> goodsSaleRegionReport(ShopDailyRequest request) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().minusDays(1).format(formatter);
        String endDate = startDate;
        // 日期处理
        if(request.getDates()!=null){
            if(request.getDates().length>1){
                startDate = request.getDates()[0];
                endDate = request.getDates()[1];
            }else if(request.getDates().length>0){
                startDate = request.getDates()[0];
                endDate = startDate;
            }
        }
        return dailyDetailMapper.goodsSaleRegionReport(startDate,endDate,request.getRegionId());
    }

    @Override
    public List<OShopDaily> shopRegionList(ShopDailyRequest request) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().minusDays(1).format(formatter);
        String endDate = startDate;
        // 日期处理
        if(request.getDates()!=null){
            if(request.getDates().length>1){
                startDate = request.getDates()[0];
                endDate = request.getDates()[1];
            }else if(request.getDates().length>0){
                startDate = request.getDates()[0];
                endDate = startDate;
            }
        }
        return         dailyMapper.shopRegionReport(startDate,endDate, request.getRegionId(), request.getShopGroupId());
    }
}




