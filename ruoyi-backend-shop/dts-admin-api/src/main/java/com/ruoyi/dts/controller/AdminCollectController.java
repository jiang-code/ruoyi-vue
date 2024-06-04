package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.domain.DtsCollect;
import com.ruoyi.dts.service.DtsCollectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/collect")
@Validated
public class AdminCollectController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminCollectController.class);

    @Autowired
    private DtsCollectService collectService;

    @PreAuthorize("@ss.hasPermi('admin:collect:list')")
    @GetMapping("/list")
    public Object list(String userId, String valueId) {

        startPage();
        List<DtsCollect> collectList = collectService.querySelective(userId, valueId);

        return getDataTable(collectList);
    }
}
