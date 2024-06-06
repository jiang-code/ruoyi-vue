package com.ruoyi.dts.db.service;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.db.mapper.DtsAddressMapper;
import com.ruoyi.dts.db.domain.DtsAddress;
import com.ruoyi.dts.db.domain.DtsAddressExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DtsAddressService {
	@Resource
	private DtsAddressMapper addressMapper;

	public List<DtsAddress> queryByUid(Integer uid) {
		DtsAddressExample example = new DtsAddressExample();
		example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
		return addressMapper.selectByExample(example);
	}

	public DtsAddress findById(Integer id) {
		return addressMapper.selectByPrimaryKey(id);
	}

	public int add(DtsAddress address) {
		address.setAddTime(LocalDateTime.now());
		address.setUpdateTime(LocalDateTime.now());
		return addressMapper.insertSelective(address);
	}

	public int update(DtsAddress address) {
		address.setUpdateTime(LocalDateTime.now());
		return addressMapper.updateByPrimaryKeySelective(address);
	}

	public void delete(Integer id) {
		addressMapper.logicalDeleteByPrimaryKey(id);
	}

	public DtsAddress findDefault(Integer userId) {
		DtsAddressExample example = new DtsAddressExample();
		example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
		return addressMapper.selectOneByExample(example);
	}

	/**
	 * 取消用户的默认地址配置
	 * 
	 * @param userId
	 */
	public void resetDefault(Integer userId) {
		DtsAddress address = new DtsAddress();
		address.setIsDefault(false);
		address.setUpdateTime(LocalDateTime.now());
		DtsAddressExample example = new DtsAddressExample();
		example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false).andIsDefaultEqualTo(true);
		addressMapper.updateByExampleSelective(address, example);
	}

	public List<DtsAddress> querySelective(Integer userId, String name) {
		DtsAddressExample example = new DtsAddressExample();
		DtsAddressExample.Criteria criteria = example.createCriteria();

		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}
		if (!StringUtils.isEmpty(name)) {
			criteria.andNameLike("%" + name + "%");
		}
		criteria.andDeletedEqualTo(false);

		return addressMapper.selectByExample(example);
	}
}