package com.ruoyi.dts.db.service;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.db.domain.DtsCouponUser;
import com.ruoyi.dts.db.domain.DtsCouponUserExample;
import com.ruoyi.dts.db.mapper.DtsCouponUserMapper;
import com.ruoyi.dts.db.util.CouponUserConstant;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DtsCouponUserService {
	@Resource
	private DtsCouponUserMapper couponUserMapper;

	public Integer countCoupon(Integer couponId) {
		DtsCouponUserExample example = new DtsCouponUserExample();
		example.or().andCouponIdEqualTo(couponId).andDeletedEqualTo(false);
		return (int) couponUserMapper.countByExample(example);
	}

	public Integer countUserAndCoupon(Integer userId, Integer couponId) {
		DtsCouponUserExample example = new DtsCouponUserExample();
		example.or().andUserIdEqualTo(userId).andCouponIdEqualTo(couponId).andDeletedEqualTo(false);
		return (int) couponUserMapper.countByExample(example);
	}

	public void add(DtsCouponUser couponUser) {
		couponUser.setAddTime(LocalDateTime.now());
		couponUser.setUpdateTime(LocalDateTime.now());
		couponUserMapper.insertSelective(couponUser);
	}

	public List<DtsCouponUser> queryList(Integer userId, Integer couponId, Short status) {
		DtsCouponUserExample example = new DtsCouponUserExample();
		DtsCouponUserExample.Criteria criteria = example.createCriteria();
		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}
		if (couponId != null) {
			criteria.andCouponIdEqualTo(couponId);
		}
		if (status != null) {
			criteria.andStatusEqualTo(status);
		}
		criteria.andDeletedEqualTo(false);
		return couponUserMapper.selectByExample(example);
	}

	public List<DtsCouponUser> queryWxList(Integer userId, Integer couponId, Short status, Integer page, Integer size,
										 String sort, String order) {
		DtsCouponUserExample example = new DtsCouponUserExample();
		DtsCouponUserExample.Criteria criteria = example.createCriteria();
		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}
		if (couponId != null) {
			criteria.andCouponIdEqualTo(couponId);
		}
		if (status != null) {
			criteria.andStatusEqualTo(status);
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		if (!StringUtils.isEmpty(page) && !StringUtils.isEmpty(size)) {
			PageHelper.startPage(page, size);
		}

		return couponUserMapper.selectByExample(example);
	}

	public List<DtsCouponUser> queryAll(Integer userId, Integer couponId) {
		return queryList(userId, couponId, CouponUserConstant.STATUS_USABLE);
	}

	public List<DtsCouponUser> queryAll(Integer userId) {
		return queryList(userId, null, CouponUserConstant.STATUS_USABLE);
	}

	public DtsCouponUser queryOne(Integer userId, Integer couponId) {
		List<DtsCouponUser> couponUserList = queryList(userId, couponId, CouponUserConstant.STATUS_USABLE);
		if (couponUserList.size() == 0) {
			return null;
		}
		return couponUserList.get(0);
	}

	public DtsCouponUser findById(Integer id) {
		return couponUserMapper.selectByPrimaryKey(id);
	}

	public int update(DtsCouponUser couponUser) {
		couponUser.setUpdateTime(LocalDateTime.now());
		return couponUserMapper.updateByPrimaryKeySelective(couponUser);
	}

	public List<DtsCouponUser> queryExpired() {
		DtsCouponUserExample example = new DtsCouponUserExample();
		example.or().andStatusEqualTo(CouponUserConstant.STATUS_USABLE).andEndTimeLessThan(LocalDate.now())
				.andDeletedEqualTo(false);
		return couponUserMapper.selectByExample(example);
	}
}
