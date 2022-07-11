package com.example.notebook.config;

import com.example.notebook.interceptor.HDTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * @Author William
 * @Date 2022/6/23 10:44
 * @Version 1.0
 */
@Configuration
public class InterceptorConfig extends WebmvcConfig{
    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HDTInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/download");
    }
}
