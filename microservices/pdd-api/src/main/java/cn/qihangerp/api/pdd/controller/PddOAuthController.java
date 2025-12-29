package cn.qihangerp.api.pdd.controller;

import cn.qihangerp.api.pdd.PddTokenCreateBo;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.model.entity.OShop;
import cn.qihangerp.model.entity.OShopPlatform;
import cn.qihangerp.module.service.OShopPlatformService;
import cn.qihangerp.module.service.OShopService;
import cn.qihangerp.open.common.ApiResultVo;
import cn.qihangerp.open.pdd.PddTokenApiHelper;

import cn.qihangerp.open.pdd.model.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/pdd")
@RestController
public class PddOAuthController {
    @Autowired
    private OShopService shopService;
    @Autowired
    private OShopPlatformService platformService;

    @Value("${qihangerp.auth-redirect-url:''}")
    private String authRedirectUrl;


    @GetMapping("/getOauthUrl")
    public AjaxResult oauth(@RequestParam Integer shopId) {
//        String returnUrl = serverConfig.getUrl() + "/pdd_api/getToken&state="+req.getParameter("shopId");
//        var shop = shopService.selectShopById(reqData.getShopId());
        OShopPlatform platform = platformService.selectById(EnumShopType.PDD.getIndex());
        String appKey = platform.getAppKey();
        String appSercet = platform.getAppSecret();

        String url = "https://mms.pinduoduo.com/open.html?response_type=code&client_id=" + appKey + "&redirect_uri=" + URLEncoder.encode(platform.getRedirectUri())+"&state="+shopId;
        return AjaxResult.success("SUCCESS",url);
    }

    @PostMapping("/getToken")
    public AjaxResult getToken(@RequestBody PddTokenCreateBo bo) throws IOException, InterruptedException {
        log.info("/**********获取拼多多授权token*********/");
        var shop = shopService.selectShopById(bo.getShopId());
        OShopPlatform platform = platformService.selectById(EnumShopType.PDD.getIndex());
        String appKey = platform.getAppKey();
        String appSercet = platform.getAppSecret();
        ApiResultVo<Token> token = PddTokenApiHelper.getToken(appKey, appSercet, bo.getCode());
        if(token.getCode()==0){
            shopService.updateSessionKey(shop.getId(),token.getData().getAccess_token());
            return AjaxResult.success("SUCCESS");
        }else
            return AjaxResult.error(token.getMsg());
    }

//    /**
//     * 获取授权成功
//     * @param req
//     * @param model
//     * @return
//     */
//    @RequestMapping("/getTokenSuccess")
//    public String getTokeSuccess(HttpServletRequest req, @RequestParam Long mallId, Model model){
//        var shop = shopService.selectShopById(mallId);
//        model.addAttribute("shop",shop);
//        model.addAttribute("shopId",shop.getId());
//        return "get_token_success";
//    }


    /**
     * 授权返回
     * @param request
     * @return
     * @throws IOException
     */
    @GetMapping("/oauth_callback")
    public void oauthCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("================Pdd授权返回==========");
        String shopId = request.getParameter("state");
        String code = request.getParameter("code");
        // 打印查询参数（URL中的参数）
        request.getParameterMap().forEach((key, value) -> {
            log.info("Request param: {} = {}", key, String.join(", ", value));
        });

        OShop shop = shopService.getById(shopId);
        log.info("========获取店铺");
        if(shop==null) {
            log.error("============店铺不存在========");
            sendJsonResponse(response, 500, "店铺不存在");
            return;
        }
        else if(shop.getType()!=EnumShopType.PDD.getIndex()) {
            log.error("============非PDD店铺========");
            sendJsonResponse(response, 500, "非PDD店铺");
            return;
        }
        String appKey="";
        String appSecret="";
        if(StringUtils.hasText(shop.getAppKey())){
            appKey = shop.getAppKey();
            appSecret = shop.getAppSecret();

        }else {
            OShopPlatform oShopPlatform = platformService.selectById(EnumShopType.PDD.getIndex());
            appKey = oShopPlatform.getAppKey();
            appSecret = oShopPlatform.getAppSecret();

        }
        if (!StringUtils.hasText(appKey)) {
            log.error("============平台参数设置错误，没有找到AppKey========");
            sendJsonResponse(response, 500, "平台参数设置错误，没有找到AppKey");
            return;
        }
        if (!StringUtils.hasText(appSecret)) {
            log.error("============平台参数设置错误，没有找到AppSecret========");
            sendJsonResponse(response, 500, "平台参数设置错误，没有找到AppSecret");
            return;
        }
//        log.info("========获取平台参数：{}",appKey);
//        PopAccessTokenClient accessTokenClient = new PopAccessTokenClient(appKey, appSecret);

        // 生成AccessToken
        try {
            ApiResultVo<Token> token = PddTokenApiHelper.getToken(appKey, appSecret, code);
//            log.info("==========获取拼多多授权token:{}", JSONObject.toJSONString(token));
            if (token.getCode() != 0) {
                log.error("===========获取拼多多授权token错误：" + token.getMsg());
                sendJsonResponse(response, 500, token.getMsg());
                return;
            }

            //保存accessToken
            shopService.updateSessionKey(shop.getId(),token.getData().getAccess_token());
            log.info("===========获取拼多多授权token成功=====SAVE====");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("=======获取拼多多AccessToken异常：{}",e.getMessage());
            sendJsonResponse(response, 500, e.getMessage());
            return;
        }
        log.info("======拼多多AccessToken获取成功=========");
        response.sendRedirect(StringUtils.hasText(authRedirectUrl)?authRedirectUrl:"https://erp.qihangerp.cn/");  // 跳转到新页面
    }

    // 返回JSON响应
    private void sendJsonResponse(HttpServletResponse response, int code, String msg) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", URLDecoder.decode(msg, "UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        response.getWriter().write(json);
    }
}
