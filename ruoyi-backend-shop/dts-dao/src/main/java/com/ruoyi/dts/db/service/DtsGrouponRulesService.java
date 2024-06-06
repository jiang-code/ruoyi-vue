package com.ruoyi.dts.db.service;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.db.mapper.DtsGoodsMapper;
import com.ruoyi.dts.db.mapper.DtsGrouponRulesMapper;
import com.ruoyi.dts.db.mapper.ex.GrouponMapper;
import com.ruoyi.dts.db.domain.DtsGoods;
import com.ruoyi.dts.db.domain.DtsGrouponRules;
import com.ruoyi.dts.db.domain.DtsGrouponRulesExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DtsGrouponRulesService {
	@Resource
	private DtsGrouponRulesMapper mapper;
	@Resource
	private DtsGoodsMapper goodsMapper;
	@Resource
	private GrouponMapper grouponMapper;
	
	private DtsGoods.Column[] goodsColumns = new DtsGoods.Column[] { DtsGoods.Column.id, DtsGoods.Column.name,
			DtsGoods.Column.brief, DtsGoods.Column.picUrl, DtsGoods.Column.counterPrice, DtsGoods.Column.retailPrice };

	public int createRules(DtsGrouponRules rules) {
		rules.setAddTime(LocalDateTime.now());
		rules.setUpdateTime(LocalDateTime.now());
		return mapper.insertSelective(rules);
	}

	/**
	 * 根据ID查找对应团购项
	 *
	 * @param id
	 * @return
	 */
	public DtsGrouponRules queryById(Integer id) {
		DtsGrouponRulesExample example = new DtsGrouponRulesExample();
		example.or().andIdEqualTo(id).andDeletedEqualTo(false);
		return mapper.selectOneByExample(example);
	}

	/**
	 * 查询某个商品关联的团购规则
	 *
	 * @param goodsId
	 * @return
	 */
	public List<DtsGrouponRules> queryByGoodsId(Long goodsId) {
		DtsGrouponRulesExample example = new DtsGrouponRulesExample();
		example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
		return mapper.selectByExample(example);
	}

	/**
	 * 获取首页团购活动列表
	 *
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> queryList(int offset, int limit) {
		return queryList(offset, limit, "add_time", "desc");
	}

	public List<Map<String, Object>> queryList(int offset, int limit, String sort, String order) {
		DtsGrouponRulesExample example = new DtsGrouponRulesExample();
		example.or().andDeletedEqualTo(false);
		example.setOrderByClause(sort + " " + order);
		PageHelper.startPage(offset, limit);
		List<DtsGrouponRules> grouponRules = mapper.selectByExample(example);

		List<Map<String, Object>> grouponList = new ArrayList<>(grouponRules.size());
		for (DtsGrouponRules rule : grouponRules) {
			Integer goodsId = rule.getGoodsId().intValue();
			DtsGoods goods = goodsMapper.selectByPrimaryKeySelective(goodsId, goodsColumns);
			if (goods == null)
				continue;

			Map<String, Object> item = new HashMap<>();
			item.put("goods", goods);
			item.put("groupon_price", goods.getRetailPrice().subtract(rule.getDiscount()));
			item.put("groupon_member", rule.getDiscountMember());
			grouponList.add(item);
		}

		return grouponList;
	}

	/**
	 * 判断某个团购活动是否已经过期
	 *
	 * @return
	 */
	public boolean isExpired(DtsGrouponRules rules) {
		return (rules == null || rules.getExpireTime().isBefore(LocalDateTime.now()));
	}

	/**
	 * 获取团购活动列表
	 *
	 * @param goodsId
	 * @param page
	 * @param size
	 * @param sort
	 * @param order
	 * @return
	 */
	public List<DtsGrouponRules> querySelective(String goodsId) {
		DtsGrouponRulesExample example = new DtsGrouponRulesExample();

		DtsGrouponRulesExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(goodsId)) {
			criteria.andGoodsIdEqualTo(Long.parseLong(goodsId));
		}
		criteria.andDeletedEqualTo(false);

		return mapper.selectByExample(example);
	}

	public void delete(Integer id) {
		mapper.logicalDeleteByPrimaryKey(id);
	}

	public int updateById(DtsGrouponRules grouponRules) {
		grouponRules.setUpdateTime(LocalDateTime.now());
		return mapper.updateByPrimaryKeySelective(grouponRules);
	}

	/**
	 * 查询品牌入驻管理员团购规则
	 * @param brandIds
	 * @param goodsId
	 * @return
	 */
	public List<DtsGrouponRules> queryBrandGrouponRules(List<Integer> brandIds, String goodsId) {
		String orderBySql = null;

		String brandIdsSql = null;
		if (brandIds != null) {
			brandIdsSql = "";
			for (Integer brandId : brandIds) {
				brandIdsSql += "," + brandId;
			}
			brandIdsSql = " and g.brand_id in (" + brandIdsSql.substring(1) + ") ";
		}

		return grouponMapper.queryBrandGrouponRules(goodsId,orderBySql,brandIdsSql);
	}
}