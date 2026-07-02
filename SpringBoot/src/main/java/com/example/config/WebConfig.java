package com.example.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 注意：Windows 路径要使用 "file:" 前缀，且反斜杠改为斜杠或转义
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:D:/Code/java/bootTwo/picture/");
        }
}
