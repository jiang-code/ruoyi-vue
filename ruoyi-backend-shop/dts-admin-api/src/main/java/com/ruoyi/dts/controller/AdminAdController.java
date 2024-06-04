package com.ruoyi.dts.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.domain.DtsAd;
import com.ruoyi.dts.service.DtsAdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/ad")
@Validated
public class AdminAdController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminAdController.class);

    @Autowired
    private DtsAdService adService;

    @PreAuthorize("@ss.hasPermi('admin:ad:list')")
    @GetMapping("/list")
    public Object list(String name, String content) {
        startPage();
        List<DtsAd> adList = adService.querySelective(name, content);
        return getDataTable(adList);
    }

    @PreAuthorize("@ss.hasPermi('admin:ad:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsAd ad) {
        adService.add(ad);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('admin:ad:read')")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        startPage();
        DtsAd brand = adService.findById(id);
        return success(brand);
    }

    @PreAuthorize("@ss.hasPermi('admin:ad:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsAd ad) {
        int i = adService.updateById(ad);
        if (i == 0) {
            logger.info("推广管理->广告管理->编辑,更新广告数据失败!");
            return error("推广管理->广告管理->编辑,更新广告数据失败!");
        }
        logger.info("【请求结束】推广管理->广告管理->编辑,响应结果:{}", JSONObject.toJSONString(ad));
        return success(ad);
    }

    @PreAuthorize("@ss.hasPermi('admin:ad:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsAd ad) {
        Integer id = ad.getId();
        if (id == null) {
            return error("id参数为空");
        }
        adService.deleteById(id);

        logger.info("【请求结束】推广管理->广告管理->删除,响应结果:{}", "成功");
        return success();
    }

}
