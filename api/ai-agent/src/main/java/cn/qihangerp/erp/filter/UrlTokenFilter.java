//package cn.qihangerp.erp.filter;
//
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.common.enums.HttpStatus;
//import cn.qihangerp.security.LoginUser;
//import cn.qihangerp.security.TokenService;
//import com.alibaba.fastjson2.JSON;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * token过滤器 从URL参数中获取token并验证有效性
// *
// * @author qihang
// */
//@Component
//public class UrlTokenFilter extends OncePerRequestFilter {
//    @Autowired
//    private TokenService tokenService;
//    private Logger log = LoggerFactory.getLogger(getClass());
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//        // 从URL参数中获取token
//        String token = request.getParameter("token");
//        String url = request.getRequestURI();
//
//        // 跳过登录等不需要token的请求
//        if (url.contains("/login") || url.contains("/captchaImage")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // 如果URL参数中没有token，尝试从header中获取（保持兼容性）
//        if (token == null || token.isEmpty()) {
//            token = request.getHeader("Authorization");
//        }
//
//        // 验证token
//        if (token != null && !token.isEmpty()) {
//            // 移除Bearer前缀
//            if (token.startsWith("Bearer ")) {
//                token = token.substring(7);
//            }
//
//            // 将token声明为final，以便内部类可以引用
//            final String finalToken = token;
//
//            // 将token设置到请求的header中，以便TokenService能够正常工作
//            final HttpServletRequest modifiedRequest = new HttpServletRequestWrapper(request) {
//                @Override
//                public String getHeader(String name) {
//                    if ("Authorization".equals(name)) {
//                        return "Bearer " + finalToken;
//                    }
//                    return super.getHeader(name);
//                }
//            };
//
//            // 验证token并设置用户信息
//            try {
//                LoginUser loginUser = tokenService.getLoginUser(modifiedRequest);
//                if (loginUser != null) {
//                    tokenService.verifyToken(loginUser);
//                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
//                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(modifiedRequest));
//                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                    chain.doFilter(modifiedRequest, response);
//                    return;
//                }
//            } catch (Exception e) {
//                log.error("Token validation failed: {}", e.getMessage());
//            }
//        }
//
//        // token无效或不存在
//        fallback("授权过期！", response);
//    }
//
//    private void fallback(String message, HttpServletResponse response) {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        PrintWriter writer = null;
//        try {
//            if (message == null) {
//                message = "401 Forbidden";
//            }
//            AjaxResult res = AjaxResult.error(HttpStatus.UNAUTHORIZED, message);
//            writer = response.getWriter();
//            writer.append(JSON.toJSONString(res));
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        } finally {
//            if (writer != null) {
//                writer.close();
//            }
//        }
//    }
//}
