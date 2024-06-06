package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.db.domain.DtsKeyword;
import com.ruoyi.dts.db.service.DtsKeywordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/keyword")
@Validated
public class AdminKeywordController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminKeywordController.class);

    @Autowired
    private DtsKeywordService keywordService;

    @PreAuthorize("@ss.hasPermi('admin:keyword:list')")
    @GetMapping("/list")
    public Object list(String keyword, String url) {
        startPage();
        List<DtsKeyword> brandList = keywordService.querySelective(keyword, url);
        return getDataTable(brandList);
    }

    @PreAuthorize("@ss.hasPermi('admin:keyword:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsKeyword keywords) {
        keywordService.add(keywords);

        logger.info("【请求结束】商场管理->关键词->添加,响应结果:{}", JSONObject.toJSONString(keywords));
        return AjaxResult.success(keywords);
    }

    @PreAuthorize("@ss.hasPermi('admin:keyword:read')")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {

        DtsKeyword keywords = keywordService.findById(id);

        return AjaxResult.success(keywords);
    }

    @PreAuthorize("@ss.hasPermi('admin:keyword:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsKeyword keywords) {

        keywordService.updateById(keywords);
        logger.info("【请求结束】商场管理->关键词->编辑,响应结果:{}", JSONObject.toJSONString(keywords));
        return AjaxResult.success(keywords);
    }

    @PreAuthorize("@ss.hasPermi('admin:keyword:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsKeyword keyword) {
        Integer id = keyword.getId();
        keywordService.deleteById(id);

        logger.info("【请求结束】商场管理->关键词->删除,响应结果:{}", "成功!");
        return AjaxResult.success();
    }

}
