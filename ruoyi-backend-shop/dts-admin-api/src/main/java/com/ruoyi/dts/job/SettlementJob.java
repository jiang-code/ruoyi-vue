package com.ruoyi.dts.job;

import com.ruoyi.dts.core.type.BrokerageTypeEnum;
import com.ruoyi.dts.core.util.DateTimeUtil;
import com.ruoyi.dts.db.service.DtsAccountService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 代理结算job
 */
@Component
public class SettlementJob {

	private final Log logger = LogFactory.getLog(SettlementJob.class);

	@Autowired
	private DtsAccountService accountService;

	/**
	 * 自动结算代理佣金
	 * <p>
	 * 每月8-20号 凌晨1点半执行，自动计算代理用户的佣金到对应账户，作为系统结算。
	 * <p>
	 * 注意，仅统计上个月订单状态为已经完成，即订单状态为 401 402 的订单。
	 */
	@Scheduled(cron = "0 30 1 8-20 * ?")
	public void checkOrderUnconfirm() {
		List<Integer> sharedUserIds = accountService.findAllSharedUserId();
		logger.info("自动结算代理佣金定时任务,共找到  " + sharedUserIds.size() + " 位代理用户,开始统计佣金...");

		for (Integer sharedUserId : sharedUserIds) {
			try {
				accountService.setSettleMentAccount(sharedUserId, DateTimeUtil.getPrevMonthEndDay(),BrokerageTypeEnum.SYS_APPLY.getType().intValue());
			} catch (Exception e) {
				logger.error("自动结算出错:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
