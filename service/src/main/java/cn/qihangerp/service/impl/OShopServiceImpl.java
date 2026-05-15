package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.enums.EnumShopApiStatus;
import cn.qihangerp.enums.EnumShopType;
import cn.qihangerp.mapper.OShopMapper;
import cn.qihangerp.model.entity.ErpMerchant;
import cn.qihangerp.model.entity.OShop;
import cn.qihangerp.model.entity.OShopPlatform;
import cn.qihangerp.model.entity.OShopPullLasttime;
import cn.qihangerp.model.open.request.ApiAddShopRequest;
import cn.qihangerp.service.ErpMerchantService;
import cn.qihangerp.service.OShopPlatformService;
import cn.qihangerp.service.OShopPullLasttimeService;
import cn.qihangerp.service.OShopService;
import cn.qihangerp.shop.ShopTokenBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【sys_shop(数据中心-店铺)】的数据库操作Service实现
* @createDate 2024-03-17 15:17:34
*/
@Slf4j
@AllArgsConstructor
@Service
public class OShopServiceImpl extends ServiceImpl<OShopMapper, OShop>
    implements OShopService {
    private final OShopMapper mapper;
    private final OShopPullLasttimeService pullLasttimeService;
    private final ErpMerchantService merchantService;
    private final OShopPlatformService platformService;

    @Override
    public PageResult<OShop> queryPageList(OShop bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OShop> queryWrapper = new LambdaQueryWrapper<OShop>()
                .like(StringUtils.hasText(bo.getName()), OShop::getName, bo.getName())
                .eq(StringUtils.hasText(bo.getSellerId()), OShop::getSellerId, bo.getSellerId())
                .eq(bo.getMerchantId() != null, OShop::getMerchantId, bo.getMerchantId())
                .eq(bo.getStatus()!=null, OShop::getStatus, bo.getStatus())
                .eq(bo.getType()!=null, OShop::getType, bo.getType())
                .eq(bo.getRegionId() != null, OShop::getRegionId, bo.getRegionId());

        Page<OShop> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ResultVo syncShop() {
        log.info("===========同步店铺数据=========START===");
        try {
            List<OShop> benshuList = mapper.getBenshuList();
            if (benshuList != null) {
                for (OShop shop : benshuList) {
                    List<OShop> oShops = mapper.selectList(new LambdaQueryWrapper<OShop>().eq(OShop::getSellerId, shop.getSellerId()));
                    if(oShops != null && oShops.size() > 0) {
                        //更新
                        shop.setId(oShops.get(0).getId());
                        shop.setStatus("1");
                        shop.setUpdateBy("同步更新店铺数据");
                        shop.setUpdateTime(new Date());
                        mapper.updateById(shop);
                        log.info("=====同步店铺===更新===");
                    }else{
                        shop.setRegionId(0L);
                        shop.setType(EnumShopType.OFFLINE.getIndex());
                        shop.setStatus("1");
                        shop.setCreateBy("同步店铺数据");
                        shop.setCreateTime(new Date());
                        mapper.insert(shop);
                        log.info("=====同步店铺===新增===");
                    }

                }
            }
            log.info("===========同步店铺数据=========SUCCESS===");
            return ResultVo.success();
        }catch (Exception e){
            log.error("===========同步店铺数据=========异常===");
            log.error(e.getMessage());
            return ResultVo.error(e.getMessage());
        }

    }

    @Override
    public List<OShop> selectShopList(OShop shop) {
        LambdaQueryWrapper<OShop> qw = new LambdaQueryWrapper<OShop>()
                .eq(shop.getMerchantId() != null, OShop::getMerchantId, shop.getMerchantId())
                .eq(OShop::getStatus,1)
                .eq(shop.getType()!=null, OShop::getType,shop.getType())
                .like(StringUtils.hasText(shop.getName()),OShop::getName,shop.getName())
                .eq(shop.getShopGroupId()!=null, OShop::getShopGroupId,shop.getShopGroupId())
                .eq(shop.getManageUserId()!=null&&shop.getManageUserId()!=1,OShop::getManageUserId,shop.getManageUserId())
                ;

        qw.last(" LIMIT 30 ");
        List<OShop> oShops = mapper.selectList(qw);
        if(!oShops.isEmpty()) {
            for (var sh : oShops) {
                if (sh.getMerchantId() > 0) {
                    var mer = merchantService.getById(sh.getMerchantId());
                    sh.setMerchantName(mer != null ? mer.getName() : "");
                }
            }
        }
        return oShops;
    }

    @Override
    public OShop selectShopById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public OShop selectShopByPosId(Long posId) {
        LambdaQueryWrapper<OShop> qw = new LambdaQueryWrapper<OShop>()
                .eq(OShop::getShopGroupId,posId);
        List<OShop> oShops = mapper.selectList(qw);
        if(oShops==null||oShops.isEmpty()) return null;
        else return oShops.get(0);
    }

    @Override
    public OShop selectShopBySellerId(String sellerId) {
        LambdaQueryWrapper<OShop> qw = new LambdaQueryWrapper<OShop>()
                .eq(OShop::getSellerId,sellerId);
        OShop oShop = mapper.selectOne(qw);
        return oShop;
    }

    @Override
    public OShop selectShopBySellerNum(String sellerNum) {
        LambdaQueryWrapper<OShop> qw = new LambdaQueryWrapper<OShop>()
                .eq(OShop::getSellerNum,sellerNum);
        OShop oShop = mapper.selectOne(qw);
        return oShop;
    }

    @Override
    public int updateShopById(OShop shop) {
        return mapper.updateById(shop);
    }

    @Override
    public int insertShop(OShop shop) {
        return mapper.insert(shop);
    }

    @Override
    public int deleteShopByIds(Long[] ids) {
        //删除pulllasttime
        pullLasttimeService.remove(new LambdaQueryWrapper<OShopPullLasttime>().in(OShopPullLasttime::getShopId,ids));
        return mapper.deleteBatchIds(Arrays.asList(ids));
    }


    @Override
    public List<OShop> selectShopByShopType(EnumShopType shopType) {
        LambdaQueryWrapper<OShop> qw = new LambdaQueryWrapper<OShop>()
                .eq(OShop::getType,shopType.getIndex())
                .eq(OShop::getStatus,1);
        return mapper.selectList(qw);
    }

    @Override
    public void updateSessionKey(Long shopId, String sessionKey) {
        OShop shop = new OShop();
        shop.setId(shopId);
        shop.setAccessToken(sessionKey);
        shop.setApiStatus(1);
        mapper.updateById(shop);
    }

    @Override
    public void updateSessionKey(Long shopId, String token,String refreshToken) {
        OShop shop = new OShop();
        shop.setId(shopId);
        shop.setAccessToken(token);
        shop.setRefreshToken(refreshToken);
        shop.setApiStatus(1);
        mapper.updateById(shop);
    }

    @Override
    public void updateSessionKey(Long shopId, String token,String refreshToken,Long expiresIn) {
        OShop shop = new OShop();
        shop.setId(shopId);
        shop.setAccessToken(token);
        shop.setRefreshToken(refreshToken);
        shop.setExpiresIn(expiresIn);
        shop.setAccessTokenBegin(System.currentTimeMillis()/1000);
        shop.setApiStatus(1);
        shop.setUpdateBy("更新Token");
        shop.setUpdateTime(new Date());
        mapper.updateById(shop);
    }

    @Override
    public void updateSessionKey(ShopTokenBo tokenBo) {
        OShop shop = new OShop();
        shop.setId(tokenBo.getShopId());
        shop.setAccessToken(tokenBo.getToken());
        shop.setRefreshToken(tokenBo.getRefreshToken());
        shop.setAccessTokenBegin(System.currentTimeMillis()/1000);
        if(tokenBo.getExpiresIn()==null)
            shop.setExpiresIn(tokenBo.getExpiresAt());
        else shop.setExpiresIn(tokenBo.getExpiresIn().longValue());
        shop.setSellerId(tokenBo.getSellerId());
        shop.setSellerNum(tokenBo.getSellerNum());
        shop.setName(tokenBo.getSellerName());
        shop.setCreateBy(tokenBo.getOwnerName());
        shop.setApiStatus(1);
        mapper.updateById(shop);
    }
    @Override
    public void updateShopManage(Long shopId, Long userId, Long groupId) {
        OShop shop = new OShop();
        shop.setId(shopId);
        shop.setManageUserId(userId);
        shop.setShopGroupId(groupId);
        shop.setUpdateTime(new Date());

        mapper.updateById(shop);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<ApiAddShopRequest> apiAddShop(ApiAddShopRequest apiAddShopRequest) {
        ErpMerchant merchant = merchantService.getById(apiAddShopRequest.getMerchantId());
        if (merchant == null) return ResultVo.error("商户不存在");
        OShopPlatform platform = platformService.getById(apiAddShopRequest.getPlatformId());
        if (platform == null) return ResultVo.error("平台不存在");
        if(apiAddShopRequest.getShopId() != null){
           var shop1 =  mapper.selectById(apiAddShopRequest.getShopId());
           if(shop1 == null) return ResultVo.error("店铺ID不存在");
           // 修改
            // 添加店铺
            OShop shopUpdate = new OShop();
            shopUpdate.setId(shop1.getId());
            shopUpdate.setName(apiAddShopRequest.getName());
            shopUpdate.setProvince(apiAddShopRequest.getProvince());
            shopUpdate.setCity(apiAddShopRequest.getCity());
            shopUpdate.setDistrict(apiAddShopRequest.getDistrict());
            shopUpdate.setAddress(apiAddShopRequest.getAddress());
            shopUpdate.setMerchantId(apiAddShopRequest.getMerchantId());
//            shop.setRegionId(0L);
            shopUpdate.setType(apiAddShopRequest.getPlatformId());
            shopUpdate.setSellerId(apiAddShopRequest.getSellerId());
            shopUpdate.setStatus("1");
            shopUpdate.setUpdateBy("API接口修改店铺");
            shopUpdate.setUpdateTime(new Date());
            mapper.updateById(shopUpdate);
            return ResultVo.success(apiAddShopRequest);
        }else {
            // 添加店铺
            OShop shop = new OShop();
            shop.setName(apiAddShopRequest.getName());
            shop.setProvince(apiAddShopRequest.getProvince());
            shop.setCity(apiAddShopRequest.getCity());
            shop.setDistrict(apiAddShopRequest.getDistrict());
            shop.setAddress(apiAddShopRequest.getAddress());
            shop.setMerchantId(apiAddShopRequest.getMerchantId());
            shop.setRegionId(0L);
            shop.setType(apiAddShopRequest.getPlatformId());
            shop.setSellerId("");
            shop.setStatus("1");
            shop.setCreateBy("API接口添加店铺");
            shop.setCreateTime(new Date());
            mapper.insert(shop);
            apiAddShopRequest.setShopId(shop.getId());
            return ResultVo.success(apiAddShopRequest);
        }
    }

    @Override
    public List<OShop> queryJkyApiShopList() {
        LambdaQueryWrapper<OShop> queryWrapper = new LambdaQueryWrapper<OShop>()
                .eq( OShop::getStatus, 1)
                .eq( OShop::getApiStatus,  EnumShopApiStatus.JKY.getIndex());

        return mapper.selectList(queryWrapper);
    }
//    @Override
//    public List<SysPlatform> selectShopPlatformList() {
//        return platformMapper.selectList(new LambdaQueryWrapper<>());
//    }
//
//    @Override
//    public SysPlatform selectShopPlatformById(Long id) {
//        return platformMapper.selectById(id);
//    }
//
//    @Override
//    public int updateShopPlatformById(SysPlatform platform) {
//        return platformMapper.updateById(platform);
//    }
}




