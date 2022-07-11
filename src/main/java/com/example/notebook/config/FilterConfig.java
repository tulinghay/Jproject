package com.example.notebook.config;

import com.example.notebook.filter.HDTFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author William
 * @Date 2022/6/23 10:54
 * @Version 1.0
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean configFilter() {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new HDTFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("hdtFilter");
        return filterRegistrationBean;
    }
}
