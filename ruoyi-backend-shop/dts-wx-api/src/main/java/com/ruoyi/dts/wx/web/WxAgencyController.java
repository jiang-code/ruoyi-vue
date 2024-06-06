package com.ruoyi.dts.wx.web;

import java.util.HashMap;
import java.util.Map;

import com.ruoyi.dts.core.consts.CommConsts;
import com.ruoyi.dts.core.util.JacksonUtil;
import com.ruoyi.dts.db.domain.DtsUser;
import com.ruoyi.dts.db.service.DtsAgencyService;
import com.ruoyi.dts.db.service.DtsUserService;
import com.ruoyi.dts.wx.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.ruoyi.dts.wx.annotation.LoginUser;
import com.ruoyi.dts.wx.service.WxAgencyService;

/**
 * 代理业务接口
 * 
 * @author ruoyi
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("/wx/agency")
@Validated
public class WxAgencyController {
	private static final Logger logger = LoggerFactory.getLogger(WxAgencyController.class);

	@Autowired
	private DtsAgencyService agencyService;

	@Autowired
	private DtsUserService userService;
	
	@Autowired
	private WxAgencyService wxAgencyService;
	
	
	/**
	 * 生成分享图
	 * 
	 * @param userId
	 * @param page
	 * @param size
	 * @return
	 */
	@PostMapping("createShareImg")
	public Object createShareImg(@LoginUser Integer userId, @RequestBody String body) {
		logger.info("【请求开始】生成分享图,请求参数,body:{}", body);
		if (userId == null) {
			logger.error("生成分享图失败:用户未登录！！！");
			return ResponseUtil.unlogin();
		}
		
		Integer shareObjId = JacksonUtil.parseInteger(body, "shareObjId");
		Integer type = JacksonUtil.parseInteger(body, "type");

		if (shareObjId == null || type == null) {
			logger.error("生成分享图失败:{}", CommConsts.MISS_PARAMS);
			return ResponseUtil.badArgument();
		}
		
		/**
		 * 验证是否需要生成图片
		 * 1.验证用户是否是代理用户，如果是代理用户，需要返回代理用户的分享图
		 *   不存在代理分享图，则需要重新生成，并返回代理图，存在直接返回
		 * 2.如果是非代理用户，则需要直接返回对象的分享图，如果不存在，则创建后返回
		 */
		DtsUser user = userService.findById(userId);
		String shareUrl = null;
		if (user.getUserLevel().equals((byte)2)) {//之所以代理用户与非代理用户分开，是为了减少海报图片的生成
			shareUrl = agencyService.getDtsAgencyShare(userId,type,shareObjId);
			if (StringUtils.isEmpty(shareUrl)) {//如果不存在，则需要创建
				shareUrl = wxAgencyService.createAgencyShareUrl(userId,type,shareObjId);
			}
		} else {
			shareUrl = wxAgencyService.getShareObjUrl(type,shareObjId);
			if (StringUtils.isEmpty(shareUrl)) {// 如果不存在，则需要创建
				shareUrl = wxAgencyService.createShareUrl(type,shareObjId);
			}
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("shareUrl", shareUrl);
		logger.info("【请求结束】生成分享图成功,URL：{} ",shareUrl);
		return ResponseUtil.ok(result);
	}
}
