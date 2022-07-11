package com.example.notebook.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;

/**
 * @Author William
 * @Date 2022/6/23 10:21
 * @Version 1.0
 */
@Configuration
public class FileConfig {

    /**
     * bean配置上传文件大小限制，以及单次上传数据总大小限制，也可以直接在yml文件里进行配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory multipartConfigFactory = new MultipartConfigFactory();
        multipartConfigFactory.setMaxFileSize(DataSize.parse("1MB"));
        multipartConfigFactory.setMaxRequestSize(DataSize.parse("50MB"));
        return multipartConfigFactory.createMultipartConfig();
    }

}
