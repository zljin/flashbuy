package com.zljin.flashbuy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final List<String> businessApiWhiteList = Arrays.asList(
            "/**/get-otp/**",
            "/**/register/**",
            "/**/function/**",
            "/**/login/**");

    private static final List<String> swaggerUIWhiteList = Arrays.asList(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/webjars/**");

    private static final List<String> staticWhiteList = Arrays.asList(
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/favicon.ico"
    );


    private final UserInterceptor userInterceptor;

    public WebMvcConfig(UserInterceptor userInterceptor) {
        this.userInterceptor = userInterceptor;
    }

    /**
     * 添加Web项目的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                //添加白名单
                .excludePathPatterns(businessApiWhiteList)
                .excludePathPatterns(swaggerUIWhiteList)
                .excludePathPatterns(staticWhiteList);

    }
}
