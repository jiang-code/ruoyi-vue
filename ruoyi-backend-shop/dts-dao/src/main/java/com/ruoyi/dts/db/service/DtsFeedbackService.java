package com.ruoyi.dts.db.service;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.db.mapper.DtsFeedbackMapper;
import com.ruoyi.dts.db.domain.DtsFeedback;
import com.ruoyi.dts.db.domain.DtsFeedbackExample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author suichj
 * @date 2017/12/27
 */
@Service
public class DtsFeedbackService {
	@Autowired
	private DtsFeedbackMapper feedbackMapper;

	public Integer add(DtsFeedback feedback) {
		feedback.setAddTime(LocalDateTime.now());
		feedback.setUpdateTime(LocalDateTime.now());
		return feedbackMapper.insertSelective(feedback);
	}

	public List<DtsFeedback> querySelective(Integer userId, String username) {
		DtsFeedbackExample example = new DtsFeedbackExample();
		DtsFeedbackExample.Criteria criteria = example.createCriteria();

		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}
		if (!StringUtils.isEmpty(username)) {
			criteria.andUsernameLike("%" + username + "%");
		}
		criteria.andDeletedEqualTo(false);
		return feedbackMapper.selectByExample(example);
	}
}
