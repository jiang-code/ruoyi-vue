package com.ruoyi.dts.db.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.db.mapper.DtsAccountTraceMapper;
import com.ruoyi.dts.db.mapper.DtsUserAccountMapper;
import com.ruoyi.dts.db.mapper.DtsUserMapper;
import com.ruoyi.dts.db.mapper.ex.AccountMapper;
import com.ruoyi.dts.db.domain.DtsAccountTrace;
import com.ruoyi.dts.db.domain.DtsAccountTraceExample;
import com.ruoyi.dts.db.domain.DtsOrder;
import com.ruoyi.dts.db.domain.DtsUser;
import com.ruoyi.dts.db.domain.DtsUserAccount;
import com.ruoyi.dts.db.domain.DtsUserAccountExample;
import com.ruoyi.dts.db.domain.DtsUserExample;

@Service
public class DtsAccountService {
	private static final Logger logger = LoggerFactory.getLogger(DtsAccountService.class);

	public static final long TWO_MONTH_DAYS = 60;//近两个月,60天

	public static final long ONE_WEEK_DAYS = 7;//近一周

	@Resource
	private DtsUserAccountMapper userAccountMapper;

	@Resource
	private DtsAccountTraceMapper accountTraceMapper;

	@Resource
	private AccountMapper accountMapper;

	@Resource
	private DtsUserMapper userMapper;

	public DtsUserAccount findShareUserAccountByUserId(Integer shareUserId) {

		DtsUserAccountExample example = new DtsUserAccountExample();
		example.or().andUserIdEqualTo(shareUserId);
		List<DtsUserAccount> accounts = userAccountMapper.selectByExample(example);
		// Assert.state(accounts.size() < 2, "同一个用户存在两个账户");
		if (accounts.size() == 1) {
			return accounts.get(0);
		} else {
			logger.error("根据代理用户id：{},获取账号信息出错!!!",shareUserId);
			return null;
		}
	}

	public List<Integer> findAllSharedUserId() {
		return accountMapper.getShareUserId();
	}

	private String getRandomNum(Integer num) {
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public void setSettleMentAccount(Integer sharedUserId, String prevMonthEndDay,Integer type) throws Exception {
		// 1.获取用户的代理订单代理金额
		String endTime = prevMonthEndDay + " 23:59:59";
		String startTime = prevMonthEndDay.substring(0, 7) + "-01 00:00:00";
		BigDecimal toSettleMoney = accountMapper.getToSettleMoney(sharedUserId, startTime, endTime);
		if (toSettleMoney == null || toSettleMoney.compareTo(new BigDecimal(0)) <= 0) {//如果无佣金
			toSettleMoney = new BigDecimal(0);
		}
		logger.info("代理用户编号： {" + sharedUserId + "},日期：" + startTime + " - " + endTime + ",获取佣金: " + toSettleMoney
				+ "元");

		if (toSettleMoney.compareTo(new BigDecimal(0)) > 0) {
			settleApplyTrace(sharedUserId, startTime,endTime,type, toSettleMoney,null);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void settleApplyTrace(Integer sharedUserId, String startTime,String endTime,Integer type, BigDecimal toSettleMoney,String mobile) {
		
		Integer settlementRate = 5;
		// 获取用户账户信息并更新记录
		DtsUserAccount account = this.findShareUserAccountByUserId(sharedUserId);
		if (account != null && toSettleMoney.compareTo(new BigDecimal("0")) == 0) {// 如果该用户未产生推荐单，则降低结算比例
			settlementRate = account.getSettlementRate() > 8 ? 8 : account.getSettlementRate();
		}
		
		// 更新订单结算状态
		accountMapper.setLastMonthOrderSettleStaus(sharedUserId, startTime, endTime);
		
		//更新代理用户账号信息
		account.setRemainAmount(account.getRemainAmount().add(toSettleMoney));//剩余结算,尚未结算给用户
		account.setTotalAmount(account.getTotalAmount().add(toSettleMoney));
		account.setModifyTime(LocalDateTime.now());
		account.setSettlementRate(settlementRate);
		userAccountMapper.updateByPrimaryKeySelective(account);
		
		// 新增账户跟踪表，添加结算跟踪记录
		DtsAccountTrace trace = new DtsAccountTrace();
		trace.setAmount(account.getRemainAmount());//当前申请金额，直接将未结算的进行申请
		trace.setTotalAmount(account.getTotalAmount());//已提现总金额
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
		String now = df.format(LocalDate.now());
		String traceSn = now + getRandomNum(6);
		trace.setTraceSn(traceSn);
		trace.setAddTime(LocalDateTime.now());
		trace.setType(type);
		trace.setUserId(sharedUserId);
		trace.setStatus((byte) 0);//申请状态
		trace.setMobile(mobile);
		accountTraceMapper.insert(trace);
	}

	/**
	 * 统计某个用户时间段内的结算金额
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BigDecimal getMonthSettleMoney(Integer sharedUserId, String startTime, String endTime) {
		BigDecimal staticSettleMoney = accountMapper.staticMonthSettleMoney(sharedUserId, startTime, endTime);
		if (staticSettleMoney == null || staticSettleMoney.compareTo(new BigDecimal("0")) == 0) {// 如果该用户未产生推荐单，则降低结算比例
			staticSettleMoney = new BigDecimal(0.00);
		}
		return staticSettleMoney;
	}

	public Map<String, Object> getStatistics(Integer sharedUserId, int dayAgo) {
		Map<String, Object> result = new HashMap<String, Object>();
		LocalDateTime startTime = LocalDateTime.now().minusDays(dayAgo);

		DtsUserExample example = new DtsUserExample();
		example.or().andDeletedEqualTo(false).andShareUserIdEqualTo(sharedUserId)
				.andAddTimeGreaterThanOrEqualTo(startTime);
		Long userCnt = (Long) userMapper.countByExample(example);

		Long orderCnt = accountMapper.countOrderSharedUser(sharedUserId, startTime);
		BigDecimal orderSettleAmt = accountMapper.sumOrderSettleAmtSharedUser(sharedUserId, startTime);
		if (orderSettleAmt == null) {
			orderSettleAmt = new BigDecimal(0.00);
		}
		BigDecimal finalSettleAmt = orderSettleAmt; //默认就是设置的结算价格
		result.put("userCnt", userCnt);
		result.put("orderCnt", orderCnt);
		result.put("orderSettleAmt", orderSettleAmt);
		result.put("finalSettleAmt", finalSettleAmt);
		return result;
	}

	public List<DtsOrder> querySettlementOrder(Integer sharedUserId, List<Short> orderStatus,
			List<Short> settlementStatus, Integer page, Integer size) {

		String conditionSql = null;
		if (orderStatus != null) {
			conditionSql = "";
			for (Short orderStatu : orderStatus) {
				conditionSql += "," + orderStatu;
			}
			conditionSql = "and o.order_status in (" + conditionSql.substring(1) + ") ";
		}
		if (settlementStatus != null && settlementStatus.size() == 1) {
			conditionSql = conditionSql + " and o.settlement_status =" + settlementStatus.get(0) + " ";
		}

		PageHelper.startPage(page, size);
		return accountMapper.querySettlementOrder(sharedUserId, conditionSql);
	}

	public List<DtsAccountTrace> queryAccountTraceList(Integer userId, Integer page, Integer size) {
		DtsAccountTraceExample example = new DtsAccountTraceExample();
		example.setOrderByClause(DtsAccountTrace.Column.addTime.desc());
		DtsAccountTraceExample.Criteria criteria = example.or();
		criteria.andUserIdEqualTo(userId);
		PageHelper.startPage(page, size);
		return accountTraceMapper.selectByExample(example);
	}

	/**
	 * 新增申请提现记录
	 * 
	 * @param userId
	 * @param applyAmt
	 */
	public void addExtractRecord(Integer userId, BigDecimal applyAmt, String mobile, String smsCode,
			BigDecimal hasAmount) {
		DtsAccountTrace record = new DtsAccountTrace();
		record.setAmount(applyAmt);
		record.setMobile(mobile);
		record.setTotalAmount(applyAmt.add(hasAmount));
		record.setSmsCode(smsCode);

		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
		String now = df.format(LocalDate.now());
		String traceSn = now + getRandomNum(6);
		record.setTraceSn(traceSn);

		record.setAddTime(LocalDateTime.now());
		record.setType(1);// 申请中..
		record.setUserId(userId);
		accountTraceMapper.insert(record);
	}

	public void add(DtsUserAccount userAccount) {
		userAccount.setCreateTime(LocalDateTime.now());
		userAccount.setModifyTime(LocalDateTime.now());
		userAccountMapper.insert(userAccount);
	}

	/**
	 * 根据账号和状态，查询提现记录
	 * @param userId 
	 * @param types
	 * @return
	 */
	public List<DtsAccountTrace> getAccountTraceList(Integer userId, Byte... types) {
		if(userId == null || types == null || types.length < 1) {
			return null;
		}
		DtsAccountTraceExample example = new DtsAccountTraceExample();
		List<Byte> statusList = new ArrayList<Byte>();
		for (Byte type : types) {
			statusList.add(type);
		}
		example.or().andUserIdEqualTo(userId).andStatusIn(statusList);
		return accountTraceMapper.selectByExample(example);
	}

	public List<DtsAccountTrace> querySelectiveTrace(List<DtsUser> userList, List<Byte> status) {
		//是否有匹配到的用户,转用户id集合
		List<Integer> userIdArray = null;
		if (userList != null && userList.size() > 0) {
			userIdArray = new ArrayList<Integer>();
			for (DtsUser dtsUser : userList) {
				userIdArray.add(dtsUser.getId().intValue()) ;
			}
		}
		
		DtsAccountTraceExample example = new DtsAccountTraceExample();
		DtsAccountTraceExample.Criteria criteria = example.or();
		
		if (userIdArray != null && userIdArray.size() != 0) {
			criteria.andUserIdIn(userIdArray);
		}
		if (status != null && status.size() != 0) {
			criteria.andStatusIn(status);
		}

		return accountTraceMapper.selectByExample(example);
		
	}

	/**
	 * 只计算近两个月内未结算的订单佣金
	 * 时间范围两月内，且订单超过一周，原因，一周内可能发生退款，
	 * 减少退款订单对佣金结算的影响
	 * @param userId
	 * @return
	 */
	public BigDecimal getUnSettleAmount(Integer userId) {
		LocalDateTime startTime = LocalDateTime.now().minusDays(TWO_MONTH_DAYS);
		LocalDateTime endTime = LocalDateTime.now().minusDays(ONE_WEEK_DAYS);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return getUnSettleAmount(userId,startTime.format(df),endTime.format(df));
	}
	
	public BigDecimal getUnSettleAmount(Integer userId,String startDay,String endDay) {
		BigDecimal staticSettleMoney = accountMapper.getToSettleMoney(userId,startDay,endDay);
		if (staticSettleMoney == null || staticSettleMoney.compareTo(new BigDecimal("0")) == 0) {// 如果该用户未产生推荐单，则降低结算比例
			staticSettleMoney = new BigDecimal(0.00);
		}
		logger.info("获取开始时间：{} - 结束时间 ：{} 内 用户id:{} 的未结算佣金 :{}",startDay,endDay,userId,staticSettleMoney);
		return staticSettleMoney;
	}

	/**
	 * 为资金账户的安全，建议做线下销账处理，处理后执行该逻辑
	 * 这里只根据记录做状态调整和审批意见记录
	 * @param traceId
	 * @param status
	 * @param traceMsg
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public boolean approveAccountTrace(Integer traceId, Byte status, String traceMsg) {
		
		DtsAccountTrace trace = accountTraceMapper.selectByPrimaryKey(traceId);
		trace.setStatus(status);
		trace.setTraceMsg(traceMsg);
		if (status.intValue() == 1) {//如果是审批通过，需要消除账户中的可提现余额
			DtsUserAccount userAccount = findShareUserAccountByUserId(trace.getUserId());
			if (userAccount != null) {
				userAccount.setRemainAmount(userAccount.getRemainAmount().subtract(trace.getAmount()));
				logger.info("提现审批通过,调整账户可提现余额为{} - {} = {}",userAccount.getRemainAmount(),trace.getAmount(),userAccount.getRemainAmount().subtract(trace.getAmount()));
				if(userAccountMapper.updateByPrimaryKeySelective(userAccount) == 0) {
					return false;
				}
			} else {
				logger.error("审批提现，获取账号出错！请检查对应用户 userId:{} 的账户",trace.getUserId());
				return false;
			}
		}
		if (accountTraceMapper.updateByPrimaryKeySelective(trace) == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 根据用户userId,结算用户代理商的结算金额</br>
	 * <p>该方法主要提供给 某个用户从普通用户转代理时调用
	 * 在代理审批通过时，将申请代理人的订单结算金额结算给当前申请人归属的前一个代理<br>
	 *  原因：在没成为代理之前，用户归属为前一个代理用户之下，该用户产生的订单佣金归属于前一个代理用户</p>
	 *  <p>产生误差：因结算时间没有考虑退款情况(正常逻辑考虑了延迟时间，此处是实时结算），
	 *  可能造成这几天内如果发生退款，佣金确已结算给上一个代理用户的情况，因为这种情况产生的概率低，且本身
	 *  佣金数额低，此误差暂时忽略，后续通过定时任务去处理这种异常结算的佣金,联系代理协商</p>
	 * @param userId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public boolean settlementPreviousAgency(Integer userId){
		
		// 获取当前用户是否有未结算的订单(约束：已支付，且无退款，在正常流转的订单)，如果存在则结算给用户的代理人，如果不存在，则结束
		BigDecimal toSettleMoney = accountMapper.getUserUnOrderSettleMoney(userId);
		if (toSettleMoney == null || toSettleMoney.compareTo(new BigDecimal("0")) == 0) {// 如果该用户未产生订单
			logger.info("用户 userId:{} 不存在未结算的订单,给其代理人结算佣金结束!");
			return true;
		}
		// 获取当前用户的代理
		DtsUser user = userMapper.selectByPrimaryKey(userId);
		Integer sharedUserId = user.getShareUserId();
		// 获取用户账户信息并更新记录
		DtsUserAccount account = this.findShareUserAccountByUserId(sharedUserId);
		
		// 更新用户订单结算状态
		accountMapper.setUserOrderSettleStaus(userId);

		// 更新代理用户账号信息
		account.setRemainAmount(account.getRemainAmount().add(toSettleMoney));// 剩余结算,尚未结算给用户
		account.setTotalAmount(account.getTotalAmount().add(toSettleMoney));
		account.setModifyTime(LocalDateTime.now());
		userAccountMapper.updateByPrimaryKeySelective(account);
		
		return true;
	}
	
}
