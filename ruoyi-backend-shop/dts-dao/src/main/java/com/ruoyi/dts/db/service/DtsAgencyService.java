package com.ruoyi.dts.db.service;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ruoyi.dts.db.mapper.DtsAgencyShareMapper;
import com.ruoyi.dts.db.domain.DtsAgencyShare;
import com.ruoyi.dts.db.domain.DtsAgencyShareExample;

@Service
public class DtsAgencyService {

	@Resource
	private DtsAgencyShareMapper agencyShareMapper;

	/**
	 * 获取代理用户的分享对象海报图
	 * @param userId
	 * @param type
	 * @param shareObjId
	 * @return
	 */
	public String getDtsAgencyShare(Integer userId, Integer type, Integer shareObjId) {
		String shareUrl = null;
		DtsAgencyShareExample example = new DtsAgencyShareExample();
		example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andShareObjIdEqualTo(shareObjId);
		DtsAgencyShare das = agencyShareMapper.selectOneByExample(example);
		if (das != null) {
			shareUrl = das.getShareUrl();
		}
		return shareUrl;
	}

	/**
	 * 存储代理用户的分享对象海报图
	 * @param userId
	 * @param type
	 * @param shareObjId
	 * @param shareUrl
	 */
	public void saveDtsAgencyShare(Integer userId, Integer type, Integer shareObjId, String shareUrl) {
		DtsAgencyShare record = new DtsAgencyShare();
		record.setUserId(userId);
		record.setType(type);
		record.setShareObjId(shareObjId);
		record.setShareUrl(shareUrl);
		record.setAddTime(LocalDateTime.now());
		record.setUpdateTime(LocalDateTime.now());
		agencyShareMapper.insert(record );
	}
}
