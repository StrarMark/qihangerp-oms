package cn.qihangerp.open.filter;

import cn.qihangerp.common.AjaxResult;


import cn.qihangerp.common.utils.IpUtils;
import cn.qihangerp.model.entity.SysOpenAuth;
import cn.qihangerp.service.SysOpenAuthService;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ApiKeyValidationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthService authService;
    @Autowired
    private SysOpenAuthService sysOpenAuthService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 获取请求路径
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/cloudWarehouse/feedback")
                ||requestURI.contains("/feishu/getOauthUrl")
                ||requestURI.contains("/feishu/callback")
                ||requestURI.contains("/feishu/kd")
                ||requestURI.contains("/feishu/getUserToken")
                ||requestURI.contains("/feishu/queryDocs")
                ||requestURI.contains("/feishu/saveDocs")
        ) {
            filterChain.doFilter(request, response);
        }else {
            String appKey = httpRequest.getHeader("appKey");
            String appSecret = httpRequest.getHeader("appSecret");
            String signature = httpRequest.getHeader("sign");
            String timestamp = httpRequest.getHeader("timestamp");
            String paramJson = httpRequest.getHeader("paramJson");
            log.info("========请求：{}====appKey：{}==appSecret:{}===timestamp:{}==paramJson:{}===sign:{}",requestURI,appKey,appSecret,timestamp,paramJson,signature);
            if (appKey == null || appKey.equals("")) {
                fallback(401, "appKey参数不能为空", response);
                return;
            }

            if (appSecret == null || appSecret.equals("")) {
                fallback(401, "appSecret参数不能为空", response);
                return;
            }
            if (timestamp == null || timestamp.equals("")) {
                fallback(401, "timestamp参数不能为空", response);
                return;
            }
            if (paramJson == null || paramJson.equals("")) {
                fallback(401, "paramJson参数不能为空", response);
                return;
            }
            if (signature == null || signature.equals("")) {
                fallback(401, "sign参数不能为空", response);
                return;
            }

//        try {
//            long ts = System.currentTimeMillis() / 1000 - 60 * 10;//10分钟前
//            if (Long.parseLong(timestamp) < ts) {
//                fallback(HttpStatus.UNAUTHORIZED, "Timestamp超时", response);
//                return;
//            }
//        }catch (Exception e){
//            fallback(HttpStatus.UNAUTHORIZED,"Timestamp错误",response);
//            return;
//        }

            // 验证 appKey 和 appSecret
            if (!authService.validateCredentials(appKey, appSecret)) {
//            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid appKey or appSecret");
                fallback(401, "Invalid appKey or appSecret", response);
                return;
            }

            SysOpenAuth sysOpenAuth = sysOpenAuthService.queryByAppKey(appKey);
            if (StringUtils.hasText(sysOpenAuth.getWhiteList())) {
                int i = sysOpenAuth.getWhiteList().indexOf(IpUtils.getIpAddr());
                if (i < 0) {
                    log.error(IpUtils.getIpAddr() + " Not In White List");
                    fallback(401, IpUtils.getIpAddr() + " Not In White List", response);
                    return;
                }
            }

//        String body = getRequestBody(request);
//        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

            // 获取缓存的请求体作为字符串
//        String body = cachedRequest.getBodyAsString();
            String signString = "";

            if (StringUtils.hasText(paramJson)) {
                // 解析JSON为Map
                Map<String, String> paramMap = objectMapper.readValue(paramJson, LinkedHashMap.class);

                // 排序参数
                Map<String, String> sortedMap = paramMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new
                        ));

                // 组合成字符串用于签名
                signString = createSignString(sortedMap);
            }


            // 验证签名
            String generatedSignature = authService.generateSignature(appKey, timestamp, signString);
            System.out.println("=======sign=====" + generatedSignature);
            if (!generatedSignature.equals(signature)) {
//            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature");
                fallback(401, "Invalid signature", response);
                return;
            }

            filterChain.doFilter(request, response); // 继续处理请求
        }
    }

//    private String getRequestBody(HttpServletRequest request) throws IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader bufferedReader = request.getReader();
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            stringBuilder.append(line);
//        }
//        return stringBuilder.toString();
//    }

    private String createSignString(Map<String, String> sortedMap) {
        StringBuilder signStringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            signStringBuilder.append(key).append("=").append(value).append("&");
        }
        if (signStringBuilder.length() > 0) {
            signStringBuilder.setLength(signStringBuilder.length() - 1);
        }
        return signStringBuilder.toString();
    }

    private void fallback(Integer code,String message, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = null;
        try {
            AjaxResult res = AjaxResult.error(code, message);
            writer = response.getWriter();
            writer.append(JSON.toJSONString(res));
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
