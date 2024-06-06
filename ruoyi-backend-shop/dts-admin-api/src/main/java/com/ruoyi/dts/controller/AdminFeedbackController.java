package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.db.domain.DtsFeedback;
import com.ruoyi.dts.db.service.DtsFeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  suichj
 *
 * @date 2018/8/26 01:11
 */
@RestController
@RequestMapping("/admin/feedback")
@Validated
public class AdminFeedbackController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminFeedbackController.class);

    @Autowired
    private DtsFeedbackService feedbackService;

    @PreAuthorize("@ss.hasPermi('admin:feedback:list')")
    @GetMapping("/list")
    public Object list(Integer userId, String username) {
        startPage();
        List<DtsFeedback> feedbackList = feedbackService.querySelective(userId, username);

        return getDataTable(feedbackList);
    }
}
