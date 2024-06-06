package com.ruoyi.dts.db.service;

import com.ruoyi.dts.db.mapper.DtsSystemMapper;
import com.ruoyi.dts.db.domain.DtsSystem;
import com.ruoyi.dts.db.domain.DtsSystemExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DtsSystemConfigService {
	@Resource
	private DtsSystemMapper systemMapper;

	public List<DtsSystem> queryAll() {
		DtsSystemExample example = new DtsSystemExample();
		example.or();
		return systemMapper.selectByExample(example);
	}
}
