package com.ruoyi.dts.wx;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 小程序服务启动类dynamic-datasourc
 *
 * @author suichj
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, DynamicDataSourceAutoConfiguration.class},
        scanBasePackages = {"com.ruoyi.dts.core", "com.ruoyi.dts.wx", "com.ruoyi.dts.db"})
@MapperScan({"com.ruoyi.dts.db.mapper", "com.ruoyi.dts.mapper.ex", "com.ruoyi.system.mapper"})
@EnableTransactionManagement
@EnableScheduling
public class WxApplication {
    // 小程序后台服务启动类
    public static void main(String[] args) {
        SpringApplication.run(WxApplication.class, args);
    }

}