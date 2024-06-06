package com.ruoyi.dts.db.mapper.ex;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.dts.db.domain.DtsOrder;

/**
 * 用户账户统计扩展的dao层
 * 
 *  suichj
 * @since 1.0.0
 */
public interface AccountMapper {

	List<Integer> getShareUserId();

	/**
	  * 获取用户时间范围内待结算的金额
	 * @param sharedUserId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	BigDecimal getToSettleMoney(@Param("sharedUserId") Integer sharedUserId,
			@Param("startTime") String startTime, @Param("endTime") String endTime);

	/**
	 * 将结算订单的状态调整为已结算
	 * 
	 * @param sharedUserId
	 * @param startTime
	 * @param endTime
	 */
	void setLastMonthOrderSettleStaus(@Param("sharedUserId") Integer sharedUserId, @Param("startTime") String startTime,
			@Param("endTime") String endTime);

	/**
	 * 统计结算，不需要考虑结算状态
	 * 
	 * @param sharedUserId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	BigDecimal staticMonthSettleMoney(@Param("sharedUserId") Integer sharedUserId, @Param("startTime") String startTime,
			@Param("endTime") String endTime);

	/**
	 * 统计订单比数,考虑有付款的订单即可
	 * 
	 * @param sharedUserId
	 * @param statTime
	 * @return
	 */
	Long countOrderSharedUser(@Param("sharedUserId") Integer sharedUserId, @Param("startTime") LocalDateTime startTime);

	/**
	 * 获取最近时间范围内的预算金额,不考虑状态为已确认
	 * 
	 * @param sharedUserId
	 * @param statTime
	 * @return
	 */
	BigDecimal sumOrderSettleAmtSharedUser(@Param("sharedUserId") Integer sharedUserId,
			@Param("startTime") LocalDateTime startTime);

	/**
	 * 根据条件查询推广订单列表
	 * 
	 * @param sharedUserId
	 * @param conditionSql
	 * @return
	 */
	List<DtsOrder> querySettlementOrder(@Param("sharedUserId") Integer sharedUserId,
			@Param("conditionSql") String conditionSql);

	/**
	 * 获取用户的未结算佣金
	 * @param userId
	 * @return
	 */
	BigDecimal getUserUnOrderSettleMoney(@Param("userId") Integer userId);

	/**
	 * 将用户订单的的状态调整为已结算
	 * @param userId
	 */
	void setUserOrderSettleStaus(@Param("userId") Integer userId);
}
