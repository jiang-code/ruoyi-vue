package com.ruoyi.dts.core.captcha;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存系统中的验证码
 */
public class CaptchaCodeManager {
	private static final Logger logger = LoggerFactory.getLogger(CaptchaCodeManager.class);
	
	private static final Integer DEFAULT_EXPIRE_MINUTES = 3;//验证码默认存储的有效期，单位：分钟
	private static Map<String, CaptchaItem> captchaCodeCache = new HashMap<>();

	/**
	 * 添加到缓存
	 *
	 * @param flagUid
	 *            验证标志码
	 * @param code
	 *            验证码
	 */
	public static boolean addToCache(String flagUid, String code,Integer expireTime) {
		cleanExpireCacheData();//清理过期内存数据
		
		// 已经发过验证码且验证码还未过期
		if (captchaCodeCache.get(flagUid) != null) {
			if (captchaCodeCache.get(flagUid).getExpireTime().isAfter(LocalDateTime.now())) {
				return false;
			} else {
				// 存在但是已过期，删掉
				captchaCodeCache.remove(flagUid);
			}
		}

		CaptchaItem captchaItem = new CaptchaItem();
		captchaItem.setFlagUid(flagUid);
		captchaItem.setCode(code);
		// 有效期为expireTime分钟 
		if (expireTime == null) {
			expireTime = DEFAULT_EXPIRE_MINUTES;
		}
		captchaItem.setExpireTime(LocalDateTime.now().plusMinutes(expireTime));

		captchaCodeCache.put(flagUid, captchaItem);

		return true;
	}

	
	/**
	 * 获取缓存的验证码
	 *
	 * @param flagUid
	 *            关联的标志码
	 * @return 验证码
	 */
	public static String getCachedCaptcha(String flagUid) {
		// 没有标志码记录
		if (captchaCodeCache.get(flagUid) == null)
			return null;

		// 记录但是已经过期
		if (captchaCodeCache.get(flagUid).getExpireTime().isBefore(LocalDateTime.now())) {
			return null;
		}
		cleanExpireCacheData();//清理过期内存数据
		
		return captchaCodeCache.get(flagUid).getCode();
	}
	
	/**
	 * 清理过期验证码
	 */
	private static void cleanExpireCacheData() {
		Iterator<Entry<String, CaptchaItem>> iterator = captchaCodeCache.entrySet().iterator();  //map.entrySet()得到的是set集合，可以使用迭代器遍历
		List<String> keys = new ArrayList<String>();
		while(iterator.hasNext()){
			Entry<String, CaptchaItem> entry = iterator.next();
			if (entry.getValue() != null && entry.getValue().getExpireTime().isBefore(LocalDateTime.now())) {
				keys.add(entry.getKey());
				logger.info("清理商品分享图 验证标志码flagUid:{},验证码 captcha:{}",entry.getKey(),entry.getValue().getCode());
			}
		}
		if (keys.size() > 0) {
			for(String key : keys) {
				captchaCodeCache.remove(key);
			}
		}
	}

}
