package com.example.springbootsecurityconcise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 视图控制器
     * @param registry
     */
    // @Override
    // public void addViewControllers(ViewControllerRegistry registry) {
    //     WebMvcConfigurer.super.addViewControllers(registry);
    //     registry.addViewController("/").setViewName("login");
    //     registry.addViewController("/index").setViewName("login");
    // }

    /**
     * 静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        // 例如把网页中请求的/static/css/a.css映射到resource/static下，SpringBoot默认的，去除静态资源目录中多的一层
        // registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}
