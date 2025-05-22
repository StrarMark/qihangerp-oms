package cn.qihangerp.api.dou;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.api.ShopApiParams;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.service.OShopPlatformService;
import cn.qihangerp.module.service.OShopService;
import cn.qihangerp.open.common.ApiResultVo;
import cn.qihangerp.sdk.dou.DouTokenApiHelper;
import cn.qihangerp.sdk.dou.model.Token;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@Component
public class DouApiCommon {
    private final OShopService shopService;
//    private final OShopPlatformService platformService;

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

        if (shop.getType() != EnumShopType.DOU.getIndex()) {
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "参数错误，店铺不是抖店店铺");
        }

        if (shop.getSellerId() == null || shop.getSellerId()<=0) {
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "参数错误，请设置抖店平台店铺ID（shopId）");
        }

//        OShopPlatform platform = platformService.getById(EnumShopType.DOU.getIndex());

        if (!StringUtils.hasText(shop.getAppKey())) {
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "店铺配置错误，没有找到AppKey");
        }
        if (!StringUtils.hasText(shop.getAppSecret())) {
            return ResultVo.error(HttpStatus.PARAMS_ERROR, "店铺配置错误，没有找到AppSecret");
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
        params.setAppKey(shop.getAppKey());
        params.setAppSecret(shop.getAppSecret());
        params.setAccessToken(shop.getAccessToken());
        params.setRedirectUri(shop.getApiRedirectUrl());
        params.setServerUrl(shop.getApiRequestUrl());
        params.setSellerId(shop.getSellerId());

        if (!StringUtils.hasText(shop.getAccessToken())) {
            ApiResultVo<Token> token = DouTokenApiHelper.getToken(shop.getAppKey(), shop.getAppSecret(), shop.getSellerId());
            if(token.getCode()!=0) {
                return ResultVo.error(ResultVoEnum.API_FAIL.getIndex(), token.getMsg(), params);
            }else{
                shopService.updateSessionKey(shopId,token.getData().getAccessToken(),token.getData().getRefreshToken());
                params.setAccessToken(token.getData().getAccessToken());
            }
        }else{
            ApiResultVo<Token> token1= DouTokenApiHelper.refreshToken(shop.getAppKey(),shop.getAppSecret(),shop.getAccessToken(),shop.getRefreshToken());
            if(token1.getCode()==0){
                shopService.updateSessionKey(shopId,token1.getData().getAccessToken(),token1.getData().getRefreshToken());
                params.setAccessToken(token1.getData().getAccessToken());
            }
        }

        return ResultVo.success(HttpStatus.SUCCESS, params);
    }

}
