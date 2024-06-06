package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.db.domain.DtsIssue;
import com.ruoyi.dts.db.service.DtsIssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/issue")
@Validated
public class AdminIssueController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminIssueController.class);

    @Autowired
    private DtsIssueService issueService;

    @PreAuthorize("@ss.hasPermi('admin:issue:list')")
    @GetMapping("/list")
    public Object list(String question) {
        startPage();
        List<DtsIssue> issueList = issueService.querySelective(question);
        return getDataTable(issueList);
    }

    @PreAuthorize("@ss.hasPermi('admin:issue:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsIssue issue) {
        issueService.add(issue);
        return AjaxResult.success(issue);
    }

    @PreAuthorize("@ss.hasPermi('admin:issue:read')")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        DtsIssue issue = issueService.findById(id);
        return AjaxResult.success(issue);
    }

    @PreAuthorize("@ss.hasPermi('admin:issue:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsIssue issue) {
        issueService.updateById(issue);
        logger.info("【请求结束】商场管理->通用问题->编辑,响应结果:{}", JSONObject.toJSONString(issue));
        return AjaxResult.success(issue);
    }

    @PreAuthorize("@ss.hasPermi('admin:issue:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsIssue issue) {

        Integer id = issue.getId();
        issueService.deleteById(id);

        logger.info("【请求结束】商场管理->通用问题->删除,响应结果:{}", "成功!");
        return AjaxResult.success();
    }

}
