package com.ruoyi.dts.db.service;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.db.mapper.DtsIssueMapper;
import com.ruoyi.dts.db.domain.DtsIssue;
import com.ruoyi.dts.db.domain.DtsIssueExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DtsIssueService {
	@Resource
	private DtsIssueMapper issueMapper;

	public List<DtsIssue> query() {
		DtsIssueExample example = new DtsIssueExample();
		example.or().andDeletedEqualTo(false);
		return issueMapper.selectByExample(example);
	}

	public void deleteById(Integer id) {
		issueMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(DtsIssue issue) {
		issue.setAddTime(LocalDateTime.now());
		issue.setUpdateTime(LocalDateTime.now());
		issueMapper.insertSelective(issue);
	}

	public List<DtsIssue> querySelective(String question) {
		DtsIssueExample example = new DtsIssueExample();
		DtsIssueExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(question)) {
			criteria.andQuestionLike("%" + question + "%");
		}
		criteria.andDeletedEqualTo(false);

		return issueMapper.selectByExample(example);
	}

	public int updateById(DtsIssue issue) {
		issue.setUpdateTime(LocalDateTime.now());
		return issueMapper.updateByPrimaryKeySelective(issue);
	}

	public DtsIssue findById(Integer id) {
		return issueMapper.selectByPrimaryKey(id);
	}
}
