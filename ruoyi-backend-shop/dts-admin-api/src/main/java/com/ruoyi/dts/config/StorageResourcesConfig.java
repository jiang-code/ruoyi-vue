package com.ruoyi.dts.config;

import com.ruoyi.common.constant.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 存储配置
 *
 * @author suichj
 */
@Configuration
public class StorageResourcesConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /** 存储路径 */
        registry.addResourceHandler(Constants.STORAGE_PREFIX + "/**")
                .addResourceLocations("file:" + AdminStorageConfig.getStoragePath());
    }
}