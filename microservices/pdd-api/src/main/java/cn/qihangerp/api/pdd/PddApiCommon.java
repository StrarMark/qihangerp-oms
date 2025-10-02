package cn.qihangerp.api.pdd;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.api.ShopApiParams;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.model.entity.OShopPlatform;
import cn.qihangerp.module.service.OShopPlatformService;
import cn.qihangerp.module.service.OShopService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@Component
public class PddApiCommon {
    private final OShopService shopService;
    private final OShopPlatformService platformService;

    /**
     * 更新前的检查
     *
     * @param shopId
     * @return
     * @throws
     */
    public ResultVo<ShopApiParams> checkBefore(Long shopId) {
        var shop = shopService.getById(shopId);
        if (shop == null) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有找到店铺");
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "参数错误，没有找到店铺");
        }

        if (shop.getType() != EnumShopType.PDD.getIndex()) {
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "参数错误，店铺不是pdd店铺");
        }

//        if (shop.getSellerId() == null || shop.getSellerId()<=0) {
//            return ResultVo.error(HttpStatus.PARAMS_ERROR, "参数错误，请设置抖店平台店铺ID（shopId）");
//        }
        String appKey = shop.getAppKey();
        String appSecret = shop.getAppSecret();
        String callbackUrl = shop.getApiRedirectUrl();
        if(StringUtils.isEmpty(appKey) || StringUtils.isEmpty(appSecret)){
            OShopPlatform platform = platformService.getById(EnumShopType.PDD.getIndex());
            if(platform != null){
                appKey = platform.getAppKey();
                appSecret = platform.getAppSecret();
                callbackUrl = platform.getRedirectUri();
            }
        }
//

        if (!StringUtils.hasText(appKey)) {
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "平台配置错误，没有找到AppKey");
        }
        if (!StringUtils.hasText(appSecret)) {
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "第三方平台配置错误，没有找到AppSercet");
        }
//        if (!StringUtils.hasText(platform.getRedirectUri())) {
//            return ResultVo.error(HttpStatus.PARAMS_ERROR, "第三方平台配置错误，没有找到RedirectUri");
//        }
//        if (!StringUtils.hasText(platform.getServerUrl())) {
//            return ResultVo.error(HttpStatus.PARAMS_ERROR, "第三方平台配置错误，没有找到ServerUrl");
//        }

//        if(shop.getSellerId() == null || shop.getSellerId() <= 0) {
//            return cn.qihangerp.tao.common.ApiResult.build(HttpStatus.PARAMS_ERROR,  "第三方平台配置错误，没有找到SellerUserId");
//        }

        ShopApiParams params = new ShopApiParams();
        params.setAppKey(appKey);
        params.setAppSecret(appSecret);
        params.setAccessToken(shop.getAccessToken());
        params.setRedirectUri(callbackUrl);
//        params.setServerUrl(shop.getApiRequestUrl());
        params.setSellerId(shop.getSellerId());

        if (!StringUtils.hasText(shop.getAccessToken())) {
            return ResultVo.error(ResultVoEnum.UNAUTHORIZED.getIndex(), "Token已过期，请重新授权", params);
        }

        return ResultVo.success(HttpStatus.SUCCESS, params);
    }

}
