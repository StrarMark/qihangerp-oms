//package cn.qihangerp.erp.config;
//
//import cn.qihangerp.erp.filter.UrlTokenFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// * Spring Security配置
// *
// * @author qihang
// */
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    @Autowired
//    private UrlTokenFilter urlTokenFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeRequests(authorizeRequests ->
//                authorizeRequests
//                    .anyRequest().permitAll()
//            )
//            .addFilterBefore(urlTokenFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
