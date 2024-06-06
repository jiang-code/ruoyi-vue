package com.ruoyi.dts.core.captcha;

import java.time.LocalDateTime;

/**
 * 验证码实体类，用于缓存验证码发送
 */
public class CaptchaItem {
	
	private String flagUid;
	private String code;
	private LocalDateTime expireTime;

	
	public String getFlagUid() {
		return flagUid;
	}

	public void setFlagUid(String flagUid) {
		this.flagUid = flagUid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDateTime getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(LocalDateTime expireTime) {
		this.expireTime = expireTime;
	}
}