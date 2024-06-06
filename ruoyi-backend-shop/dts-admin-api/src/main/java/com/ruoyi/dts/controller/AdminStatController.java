package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.db.service.StatService;
import com.ruoyi.dts.util.StatVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@RestController
@RequestMapping("/admin/stat")
@Validated
public class AdminStatController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminStatController.class);

    @Autowired
    private StatService statService;

    @PreAuthorize("@ss.hasPermi('admin:stat:user')")
    @GetMapping("/user")
    public Object statUser() {
        List<Map> rows = statService.statUser();
        String[] columns = new String[]{"day", "users"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);

        logger.info("【请求结束】统计管理->用户统计->查询,响应结果:{}", JSONObject.toJSONString(statVo));
        return AjaxResult.success(statVo);
    }

    @PreAuthorize("@ss.hasPermi('admin:stat:order')")
    @GetMapping("/order")
    public Object statOrder() {

        List<Map> rows = statService.statOrder();
        String[] columns = new String[]{"day", "orders", "customers", "amount", "pcr"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        logger.info("【请求结束】统计管理->订单统计->查询,响应结果:{}", JSONObject.toJSONString(statVo));
        return AjaxResult.success(statVo);
    }

    @PreAuthorize("@ss.hasPermi('admin:stat:goods')")
    @GetMapping("/goods")
    public Object statGoods() {

        List<Map> rows = statService.statGoods();
        String[] columns = new String[]{"day", "orders", "products", "amount"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);

        logger.info("【请求结束】统计管理->商品统计->查询,响应结果:{}", JSONObject.toJSONString(statVo));
        return AjaxResult.success(statVo);
    }

}
