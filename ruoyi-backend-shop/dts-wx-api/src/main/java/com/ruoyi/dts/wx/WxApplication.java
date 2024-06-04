package com.ruoyi.dts.wx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 小程序服务启动类
 * 
 * @author CHENBO
 * @QQ:623659388
 */
@SpringBootApplication(scanBasePackages = { "com.ruoyi.dts", "com.ruoyi.dts.core",
		"com.ruoyi.dts.wx" })
@MapperScan({ "com.ruoyi.dts.mapper", "com.ruoyi.dts.mapper.ex" })
@EnableTransactionManagement
@EnableScheduling
public class WxApplication {
	// 小程序后台服务启动类
	public static void main(String[] args) {
		SpringApplication.run(WxApplication.class, args);
	}

}