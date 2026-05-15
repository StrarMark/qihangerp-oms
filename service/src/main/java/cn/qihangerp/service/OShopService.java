package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.enums.EnumShopType;
import cn.qihangerp.model.entity.OShop;
import cn.qihangerp.model.open.request.ApiAddShopRequest;
import cn.qihangerp.shop.ShopTokenBo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【sys_shop(数据中心-店铺)】的数据库操作Service
* @createDate 2024-03-17 15:17:34
*/
public interface OShopService extends IService<OShop> {
    PageResult<OShop> queryPageList(OShop bo, PageQuery pageQuery);
    ResultVo syncShop();
    List<OShop> selectShopList(OShop shop);
    OShop selectShopById(Long id);
    OShop selectShopByPosId(Long posId);
    OShop selectShopBySellerId(String sellerId);
    OShop selectShopBySellerNum(String sellerNum);
    int updateShopById(OShop shop);
    int insertShop(OShop shop);

    int deleteShopByIds(Long[] ids);
//    List<SysPlatform> selectShopPlatformList();
//    SysPlatform selectShopPlatformById(Long id);
//    int updateShopPlatformById(SysPlatform platform);
    List<OShop> selectShopByShopType(EnumShopType shopType);
    void updateSessionKey(Long shopId,String sessionKey);
    void updateSessionKey(Long shopId, String token,String refreshToken);
    void updateSessionKey(Long shopId, String token,String refreshToken,Long expiresIn);
    void updateSessionKey(ShopTokenBo tokenBo);
    void updateShopManage(Long shopId,Long userId,Long groupId);

    ResultVo<ApiAddShopRequest> apiAddShop(ApiAddShopRequest apiAddShopRequest);

    /**
     * 所有使用吉客云api的店铺
     * @return
     */
    List<OShop> queryJkyApiShopList();
}
