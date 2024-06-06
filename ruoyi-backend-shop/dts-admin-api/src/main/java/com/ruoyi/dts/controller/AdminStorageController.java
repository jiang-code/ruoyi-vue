package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.core.storage.StorageService;
import com.ruoyi.dts.db.domain.DtsStorage;
import com.ruoyi.dts.db.service.DtsStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/storage")
@Validated
public class AdminStorageController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminStorageController.class);

    @Autowired
    private StorageService storageService;
    @Autowired
    private DtsStorageService DtsStorageService;

    @PreAuthorize("@ss.hasPermi('admin:storage:list')")
    @GetMapping("/list")
    public Object list(String key, String name) {
        startPage();
        List<DtsStorage> storageList = DtsStorageService.querySelective(key, name);
        return getDataTable(storageList);
    }

    @PreAuthorize("@ss.hasPermi('admin:storage:create')")
    @PostMapping("/create")
    public Object create(@RequestParam("file") MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String url = storageService.store(file.getInputStream(), file.getSize(), file.getContentType(),
                originalFilename);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);

        logger.info("【请求结束】系统管理->对象存储->查询:响应结果:{}", JSONObject.toJSONString(data));
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('admin:storage:read')")
    @PostMapping("/read")
    public Object read(@NotNull Integer id) {

        DtsStorage storageInfo = DtsStorageService.findById(id);
        if (storageInfo == null) {
            return "参数错误";
        }

        logger.info("【请求结束】系统管理->对象存储->详情:响应结果:{}", JSONObject.toJSONString(storageInfo));
        return AjaxResult.success(storageInfo);
    }

    @PreAuthorize("@ss.hasPermi('admin:storage:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsStorage dtsStorage) {

        if (DtsStorageService.update(dtsStorage) == 0) {
            logger.error("系统管理->对象存储->编辑 错误:{}", "更新数据失败!");
            return "更新数据失败";
        }

        logger.info("【请求结束】系统管理->对象存储->编辑:响应结果:{}", JSONObject.toJSONString(dtsStorage));
        return AjaxResult.success(dtsStorage);
    }

    @PreAuthorize("@ss.hasPermi('admin:storage:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsStorage DtsStorage) {

        String key = DtsStorage.getKey();
        DtsStorageService.deleteByKey(key);
        storageService.delete(key);

        logger.info("【请求结束】系统管理->对象存储->删除:响应结果:{}", "成功!");
        return AjaxResult.success();
    }
}
