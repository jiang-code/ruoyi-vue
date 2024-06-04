package com.ruoyi.dts.mapper.ex;

import com.ruoyi.dts.domain.CategorySellAmts;
import com.ruoyi.dts.domain.DayStatis;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public interface StatMapper {
	
	/**
	 * 统计近多少天之内的用户增长量
	 * @param daysAgo
	 * @return
	 */
	List<DayStatis> statisIncreaseUserCnt(@Param("daysAgo") int daysAgo);
	
	/**
	 * 统计近多少天之内的订单增长量
	 * @param daysAgo
	 * @return
	 */
	List<DayStatis> statisIncreaseOrderCnt(@Param("daysAgo") int daysAgo);

	/**
	 * 类目销售额统计
	 * @return
	 */
	List<CategorySellAmts> categorySellStatis();
	
	/**
	 * 用户数统计
	 * @return
	 */
	List<Map> statUser();

	/**
	 * 订单数统计
	 * @return
	 */
	List<Map> statOrder();

	/**
	 * 商品数统计
	 * @return
	 */
	List<Map> statGoods();

	
}