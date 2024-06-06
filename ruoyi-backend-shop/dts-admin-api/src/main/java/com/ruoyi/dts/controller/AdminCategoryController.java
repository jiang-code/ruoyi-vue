package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.db.domain.DtsCategory;
import com.ruoyi.dts.db.service.DtsCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
@Validated
public class AdminCategoryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminCategoryController.class);

    @Autowired
    private DtsCategoryService categoryService;

    @PreAuthorize("@ss.hasPermi('admin:category:list')")
    @GetMapping("/list")
    public Object list(String id, String name) {
        startPage();
        List<DtsCategory> collectList = categoryService.querySelective(id, name);
        return getDataTable(collectList);
    }

    @PreAuthorize("@ss.hasPermi('admin:category:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsCategory category) {
        categoryService.add(category);

        return AjaxResult.success(category);
    }

    @PreAuthorize("@ss.hasPermi('admin:category:read')")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        DtsCategory category = categoryService.findById(id);

        logger.info("【请求结束】商场管理->类目管理->详情:响应结果:{}", JSONObject.toJSONString(category));
        return AjaxResult.success(category);
    }

    @PreAuthorize("@ss.hasPermi('admin:category:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsCategory category) {

        if (categoryService.updateById(category) == 0) {
            logger.error("商场管理->类目管理->编辑 失败，更新数据失败！");
            return "更新数据失败";
        }

        logger.info("【请求结束】商场管理->类目管理->编辑:响应结果:{}", "成功!");
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('admin:category:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsCategory category) {


        Integer id = category.getId();
        categoryService.deleteById(id);

        logger.info("【请求结束】商场管理->类目管理->删除:响应结果:{}", "成功!");
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('admin:category:list')")
    @GetMapping("/l1")
    public Object catL1() {
        // 所有一级分类目录
        List<DtsCategory> l1CatList = categoryService.queryL1();
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (DtsCategory category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }

        logger.info("【请求结束】商场管理->类目管理->一级分类目录查询:total:{}", JSONObject.toJSONString(data));
        return AjaxResult.success(data);
    }
}
