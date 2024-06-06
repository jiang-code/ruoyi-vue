package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.db.domain.DtsSearchHistory;
import com.ruoyi.dts.db.service.DtsSearchHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/history")
public class AdminHistoryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminHistoryController.class);

    @Autowired
    private DtsSearchHistoryService searchHistoryService;

    @PreAuthorize("@ss.hasPermi('admin:history:list')")
    @GetMapping("/list")
    public Object list(String userId, String keyword) {
        startPage();
        List<DtsSearchHistory> footprintList = searchHistoryService.querySelective(userId, keyword);
        return getDataTable(footprintList);
    }
}
