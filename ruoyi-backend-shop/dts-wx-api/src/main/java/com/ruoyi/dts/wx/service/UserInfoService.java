package com.ruoyi.dts.wx.service;

import com.ruoyi.dts.db.domain.DtsUser;
import com.ruoyi.dts.db.service.DtsUserService;
import com.ruoyi.dts.wx.dao.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class UserInfoService {
	@Autowired
	private DtsUserService userService;

	public UserInfo getInfo(Integer userId) {
		DtsUser user = userService.findById(userId);
		Assert.state(user != null, "用户不存在");
		UserInfo userInfo = new UserInfo();
		userInfo.setNickName(user.getNickname());
		userInfo.setAvatarUrl(user.getAvatar());
		return userInfo;
	}
}
