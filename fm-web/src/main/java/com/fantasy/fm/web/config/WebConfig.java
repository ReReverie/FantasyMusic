package com.fantasy.fm.web.config;

import com.fantasy.fm.web.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    /**
     * 注册拦截器
     *
     * @param registry InterceptorRegistry对象
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册登录拦截器...");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/auth/**",
                        "/user/register",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**"
                );
    }

    /**
     * 启用全局CORS支持
     *
     * @param registry CorsRegistry对象
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:8080",      // 本地开发
                        "http://localhost:5173",      // 前端开发服务器
                        "http://localhost:80",      // nginx
                        "http://localhost:443"/*,      // nginx
                        "https://music.pixmc.top"    // 生产域名*/
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
