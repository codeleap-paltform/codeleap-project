package com.example.codeleapbackend.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 放行后端所有接口
                .allowedOriginPatterns("*") // 允许所有前端地址访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方式
                .allowedHeaders("*") // 放行所有请求头
                .allowCredentials(true) // 允许携带认证信息
                .maxAge(3600); // 预检请求缓存1小时
    }
}
