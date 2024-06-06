package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.core.qcode.QCodeService;
import com.ruoyi.dts.core.util.JacksonUtil;
import com.ruoyi.dts.db.domain.DtsUser;
import com.ruoyi.dts.db.domain.DtsUserAccount;
import com.ruoyi.dts.db.service.DtsAccountService;
import com.ruoyi.dts.db.service.DtsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private DtsUserService userService;

    @Autowired
    private QCodeService qCodeService;

    @Autowired
    private DtsAccountService accountService;

    @PreAuthorize("@ss.hasPermi('admin:user:list')")
    @GetMapping("/list")
    public Object list(String username, String mobile) {
        startPage();
        List<DtsUser> userList = userService.querySelective(username, mobile);
        return getDataTable(userList);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:user:read')")
    @GetMapping("/detailApprove")
    public Object detailApprove(@NotNull Integer id) {

        DtsUserAccount dbAccount = userService.detailApproveByUserId(id);
        logger.info("【请求结束】用户管理->会员管理->代理详情:响应结果:{}", JSONObject.toJSONString(dbAccount));
        return AjaxResult.success(dbAccount);
    }

    @PreAuthorize("@ss.hasPermi('admin:user:approveAgency')")
    @PostMapping("/approveAgency")
    public Object approveAgency(@RequestBody String body) {
        Integer userId = JacksonUtil.parseInteger(body, "userId");
        Integer settlementRate = JacksonUtil.parseInteger(body, "settlementRate");

        if (userId == null || settlementRate == null || settlementRate.intValue() <= 0) {
            return "参数错误";
        }
        try {
            /*
             * 生成代理用户独有分享的二维码需要小程序已经上线，所以未上线小程序这里调用会异常
             * 建议通过后台参数控制，因为定制用户对这里的特殊性要求，本程序暂不做调整
             */
            String shareUrl = qCodeService.createShareUserImage(userId);

            /**
             * 结算当前用户的订单佣金给其代理
             * 在用户审批通过成为代理用户之前下的订单，结算佣金应归属于前一个代理用户
             * 后续的订单由用户申请或系统自动结算给，代理用户直接会将佣金结算给自己
             */
            boolean result = accountService.settlementPreviousAgency(userId);
            if (!result) {
                logger.warn("用户管理->会员管理->代理审批 存在异常：{}", "当前用户订单佣金交割给代理用户时出错！");
            }

            userService.approveAgency(userId, settlementRate, shareUrl);

        } catch (Exception e) {
            logger.error("用户管理->会员管理->代理审批 出错：{}", e.getMessage());
            e.printStackTrace();
        }

        logger.info("【请求结束】用户管理->会员管理->代理审批:响应结果:{}", "成功!");
        return AjaxResult.success();
    }

}
