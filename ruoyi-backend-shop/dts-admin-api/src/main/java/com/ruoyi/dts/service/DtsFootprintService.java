package com.ruoyi.dts.service;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.domain.DtsFootprint;
import com.ruoyi.dts.domain.DtsFootprintExample;
import com.ruoyi.dts.mapper.DtsFootprintMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DtsFootprintService {
	@Resource
	private DtsFootprintMapper footprintMapper;

	public List<DtsFootprint> queryByAddTime(Integer userId, Integer page, Integer size) {
		DtsFootprintExample example = new DtsFootprintExample();
		example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
		example.setOrderByClause(DtsFootprint.Column.addTime.desc());
		PageHelper.startPage(page, size);
		return footprintMapper.selectByExample(example);
	}

	public DtsFootprint findById(Integer id) {
		return footprintMapper.selectByPrimaryKey(id);
	}

	public void deleteById(Integer id) {
		footprintMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(DtsFootprint footprint) {
		footprint.setAddTime(LocalDateTime.now());
		footprint.setUpdateTime(LocalDateTime.now());
		footprintMapper.insertSelective(footprint);
	}

	public List<DtsFootprint> querySelective(String userId, String goodsId) {
		DtsFootprintExample example = new DtsFootprintExample();
		DtsFootprintExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(userId)) {
			criteria.andUserIdEqualTo(Integer.valueOf(userId));
		}
		if (!StringUtils.isEmpty(goodsId)) {
			criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
		}
		criteria.andDeletedEqualTo(false);

//		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
//			example.setOrderByClause(sort + " " + order);
//		}
//
//		PageHelper.startPage(page, size);
		return footprintMapper.selectByExample(example);
	}
}
