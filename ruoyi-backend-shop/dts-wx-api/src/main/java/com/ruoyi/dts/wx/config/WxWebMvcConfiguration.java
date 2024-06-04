package com.ruoyi.dts.wx.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ruoyi.dts.wx.annotation.support.LoginUserHandlerMethodArgumentResolver;

@Configuration
public class WxWebMvcConfiguration implements WebMvcConfigurer {
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new LoginUserHandlerMethodArgumentResolver());
	}
}
