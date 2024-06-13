package com.ruoyi.dts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * 
 * @author suiscj
 */
@Component
@ConfigurationProperties(prefix = "dts.storage.local")
public class AdminStorageConfig
{
    /** 存储路径*/
    private static String storagePath;

    public static String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}
