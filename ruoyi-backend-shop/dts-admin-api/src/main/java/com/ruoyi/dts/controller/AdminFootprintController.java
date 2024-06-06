package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.db.domain.DtsFootprint;
import com.ruoyi.dts.db.service.DtsFootprintService;
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
@RequestMapping("/admin/footprint")
@Validated
public class AdminFootprintController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminFootprintController.class);

    @Autowired
    private DtsFootprintService footprintService;

    @PreAuthorize("@ss.hasPermi('admin:footprint:list')")
    @GetMapping("/list")
    public Object list(String userId, String goodsId) {
        startPage();
        List<DtsFootprint> footprintList = footprintService.querySelective(userId, goodsId);
        return getDataTable(footprintList);
    }
}
