package com.ruoyi.dts.service;

import com.ruoyi.dts.domain.DtsSystem;
import com.ruoyi.dts.domain.DtsSystemExample;
import com.ruoyi.dts.mapper.DtsSystemMapper;
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
