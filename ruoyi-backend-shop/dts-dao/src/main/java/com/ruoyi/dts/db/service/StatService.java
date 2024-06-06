package com.ruoyi.dts.db.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ruoyi.dts.db.mapper.ex.StatMapper;

@Service
@SuppressWarnings("rawtypes")
public class StatService {
	@Resource
	private StatMapper statMapper;

	public List<Map> statUser() {
		return statMapper.statUser();
	}

	public List<Map> statOrder() {
		return statMapper.statOrder();
	}

	public List<Map> statGoods() {
		return statMapper.statGoods();
	}
}
