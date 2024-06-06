package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.db.domain.DtsRegion;
import com.ruoyi.dts.db.service.DtsRegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/region")
@Validated
public class AdminRegionController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminRegionController.class);

    @Autowired
    private DtsRegionService regionService;

    @GetMapping("/clist")
    public Object clist(@NotNull Integer id) {
        startPage();
        List<DtsRegion> regionList = regionService.queryByPid(id);
        return getDataTable(regionList);
    }

    @GetMapping("/list")
    public Object list(String name, Integer code) {

        startPage();
        List<DtsRegion> regionList = regionService.querySelective(name, code);

        return getDataTable(regionList);
    }
}
