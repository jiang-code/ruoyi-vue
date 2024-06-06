package com.ruoyi.dts.db.service;

import com.ruoyi.dts.db.domain.DtsStorage;
import com.ruoyi.dts.db.domain.DtsStorageExample;
import com.ruoyi.dts.db.mapper.DtsStorageMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DtsStorageService {
	@Resource
	private DtsStorageMapper storageMapper;

	public void deleteByKey(String key) {
		DtsStorageExample example = new DtsStorageExample();
		example.or().andKeyEqualTo(key);
		storageMapper.logicalDeleteByExample(example);
	}

	public void add(DtsStorage storageInfo) {
		storageInfo.setAddTime(LocalDateTime.now());
		storageInfo.setUpdateTime(LocalDateTime.now());
		storageMapper.insertSelective(storageInfo);
	}

	public DtsStorage findByKey(String key) {
		DtsStorageExample example = new DtsStorageExample();
		example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
		return storageMapper.selectOneByExample(example);
	}

	public int update(DtsStorage storageInfo) {
		storageInfo.setUpdateTime(LocalDateTime.now());
		return storageMapper.updateByPrimaryKeySelective(storageInfo);
	}

	public DtsStorage findById(Integer id) {
		return storageMapper.selectByPrimaryKey(id);
	}

	public List<DtsStorage> querySelective(String key, String name) {
		DtsStorageExample example = new DtsStorageExample();
		DtsStorageExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(key)) {
			criteria.andKeyEqualTo(key);
		}
		if (!StringUtils.isEmpty(name)) {
			criteria.andNameLike("%" + name + "%");
		}
		criteria.andDeletedEqualTo(false);

		return storageMapper.selectByExample(example);
	}
}
