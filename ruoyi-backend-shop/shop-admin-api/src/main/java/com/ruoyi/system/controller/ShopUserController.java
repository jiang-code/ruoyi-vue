package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.ShopUser;
import com.ruoyi.system.service.IShopUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author suichj
 * @date 2024-05-16
 */
@RestController
@RequestMapping("/shop/user")
public class ShopUserController extends BaseController {
    @Autowired
    private IShopUserService shopUserService;

    /**
     * 查询用户列表
     */
    @PreAuthorize("@ss.hasPermi('admin:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopUser shopUser) {
        startPage();
        List<ShopUser> list = shopUserService.selectShopUserList(shopUser);
        return getDataTable(list);
    }


    @PreAuthorize("@ss.hasPermi('admin:user:approveAgency')")
    @PostMapping("/approveAgency")
    public Object approveAgency(@Param("settlementRate") String settlementRate, @Param("userId") String userId) {

        if (userId == null || settlementRate == null || Integer.valueOf(settlementRate) <= 0) {
            return AjaxResult.error("参数不对");
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
        return ResponseUtil.ok();
    }
}
