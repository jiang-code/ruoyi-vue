package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.dts.domain.DtsAccountTrace;
import com.ruoyi.dts.domain.DtsUser;
import com.ruoyi.dts.service.DtsAccountService;
import com.ruoyi.dts.service.DtsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 佣金业务接口
 * 
 * @since 1.0.0
 * @author CHENBO
 * @QQ 623659388
 */
@RestController
@RequestMapping("/admin/brokerage")
@Validated
public class AdminBrokerageController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(AdminBrokerageController.class);

	@Autowired
	private DtsAccountService accountService;
	
	@Autowired
	private DtsUserService userService;

	@PreAuthorize("@ss.hasPermi('admin:brokerage:list')")
	@GetMapping("/list")
	public Object list(String username, String mobile,@RequestParam(required = false) List<Byte> statusArray) {
		startPage();
		List<DtsUser> userList = userService.queryDtsUserListByNickname(username, mobile);
		List<DtsAccountTrace> traceList = accountService.querySelectiveTrace(userList,statusArray);
		TableDataInfo dataTable = getDataTable(traceList);
		logger.info("【请求结束】用户管理->佣金管理->查询:记录数:{}", traceList == null ? 0 : traceList.size());
		return dataTable;
	}

	@PreAuthorize("@ss.hasPermi('admin:brokerage:approve')")
	@PostMapping("/approve")
	public Object approve(@RequestBody String body) {
		Integer traceId = JacksonUtil.parseInteger(body, "id");
		String traceMsg = JacksonUtil.parseString(body, "traceMsg");
		Byte status = JacksonUtil.parseByte(body, "status");
		boolean approveResult = accountService.approveAccountTrace(traceId,status, traceMsg);
		
		if (!approveResult) {
			logger.info("用户管理->佣金管理->审批销账失败：{}", "审批处理错误！");
			return "审批处理错误";
		}

		logger.info("【请求结束】用户管理->佣金管理->审批销账,响应结果:{}", "成功！");
		return success();
		
	}
}
