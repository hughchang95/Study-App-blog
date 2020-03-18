package com.hugh.blog.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则，/**表示拦截所有请求
        // excludePathPatterns 用户排除拦截
        String[] includes = new String[]{
                "/admin/**"
        };
        String[] excludes = new String[]{
                "/admin",
                "/admin/login",
        };
        registry.addInterceptor(loginInterceptor).addPathPatterns(includes)
                .excludePathPatterns(excludes);
    }
}
