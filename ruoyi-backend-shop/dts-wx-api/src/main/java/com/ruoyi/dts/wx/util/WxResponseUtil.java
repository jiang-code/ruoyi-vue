package com.ruoyi.dts.wx.util;

/**
 * 微信接口枚举信息的响应
 * 
 * @author suichj
 * @since 1.0.0
 * 
 */
public class WxResponseUtil extends ResponseUtil {

	/**
	 * 按枚举返回错误响应结果
	 * 
	 * @param orderUnknown
	 * @return
	 */
	public static Object fail(WxResponseCode responseCode) {
		return fail(responseCode.code(), responseCode.desc());
	}
}
