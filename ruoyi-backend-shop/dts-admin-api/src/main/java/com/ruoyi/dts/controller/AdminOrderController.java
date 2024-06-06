package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.service.AdminDataAuthService;
import com.ruoyi.dts.service.AdminOrderService;
import com.ruoyi.dts.db.domain.DtsOrder;
import com.ruoyi.dts.db.service.DtsOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
@Validated
public class AdminOrderController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderController.class);

    @Autowired
    private AdminOrderService adminOrderService;
    @Autowired
    private DtsOrderService orderService;
    @Autowired
    private AdminDataAuthService adminDataAuthService;

    /**
     * 查询订单
     *
     * @param userId
     * @param orderSn
     * @param orderStatusArray
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:order:list')")
    @GetMapping("/list")
    public Object list(Integer userId, String orderSn, @RequestParam(required = false) List<Short> orderStatusArray) {

        // 需要区分数据权限，如果属于品牌商管理员，则需要获取当前用户管理品牌店铺
        List<Integer> brandIds = null;
        if (adminDataAuthService.isBrandManager()) {
            brandIds = adminDataAuthService.getBrandIds();
            logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}", JSONObject.toJSONString(brandIds));

            if (brandIds == null || brandIds.size() == 0) {//如果尚未管理任何入驻店铺，则返回空数据
                Map<String, Object> data = new HashMap<>();
                data.put("total", 0L);
                data.put("items", null);

                logger.info("【请求结束】商场管理->订单管理->查询,响应结果:{}", JSONObject.toJSONString(data));
                return data;
            }
        }
        List<DtsOrder> orderList = null;
        if (brandIds == null || brandIds.size() == 0) {
            startPage();
            orderList = orderService.querySelective(userId, orderSn, orderStatusArray);
        } else {
            startPage();
            orderList = orderService.queryBrandSelective(brandIds, userId, orderSn, orderStatusArray);
        }
        return getDataTable(orderList);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:order:read')")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {

        return adminOrderService.detail(id);
    }

    /**
     * 订单退款
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:order:refund')")
    @PostMapping("/refund")
    public Object refund(@RequestBody String body) {

        return adminOrderService.refund(body);
    }

    /**
     * 发货
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:order:ship')")
    @PostMapping("/ship")
    public Object ship(@RequestBody String body) {
        return adminOrderService.ship(body);
    }

    /**
     * 回复订单商品
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:order:reply')")
    @PostMapping("/reply")
    public Object reply(@RequestBody String body) {

        return adminOrderService.reply(body);
    }

    /**
     * 回复订单商品
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:order:listShip')")
    @GetMapping("/listShipChannel")
    public Object listShipChannel() {
        return adminOrderService.listShipChannel();
    }
}
