package com.ruoyi.dts.db.mapper.ex;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.dts.db.domain.DtsOrder;

public interface OrderMapper {
	
	int updateWithOptimisticLocker(@Param("lastUpdateTime") LocalDateTime lastUpdateTime,
			@Param("order") DtsOrder order);
	
	/**
	 * 根据条件获取入驻店铺的订单
	 * @param userId
	 * @param orderSn
	 * @param orderStatusSql
	 * @param orderBySql
	 * @param brandIdsSql
	 * @return
	 */
	List<DtsOrder> selectBrandOrdersByExample(@Param("userId") Integer userId, @Param("orderSn") String orderSn, @Param("orderStatusSql") String orderStatusSql, @Param("orderBySql") String orderBySql,
			@Param("brandIdsSql") String brandIdsSql);
}