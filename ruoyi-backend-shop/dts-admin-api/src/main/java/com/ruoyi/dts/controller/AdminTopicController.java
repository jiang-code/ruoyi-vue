package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.core.qcode.QCodeService;
import com.ruoyi.dts.db.domain.DtsTopic;
import com.ruoyi.dts.db.service.DtsTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/topic")
@Validated
public class AdminTopicController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminTopicController.class);

    @Autowired
    private DtsTopicService topicService;

    @Autowired
    private QCodeService qCodeService;

    @PreAuthorize("@ss.hasPermi('admin:topic:list')")
    @GetMapping("/list")
    public Object list(String title, String subtitle) {
        startPage();
        List<DtsTopic> topicList = topicService.querySelective(title, subtitle);
        return getDataTable(topicList);
    }


    @PreAuthorize("@ss.hasPermi('admin:topic:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsTopic topic) {
        try {
            //生成主题的分享URL
            String shareUrl = qCodeService.createShareTopicImage(topic.getId(), topic.getPicUrl(),
                    topic.getSubtitle(), topic.getPrice());
            topic.setShareUrl(shareUrl);
        } catch (Exception e) {
            logger.error("专题生成分享图URL出错：{}", e.getMessage());
            e.printStackTrace();
        }

        topicService.add(topic);

        logger.info("【请求结束】推广管理->专题管理->添加:响应结果:{}", JSONObject.toJSONString(topic));
        return AjaxResult.success(topic);
    }

    @PreAuthorize("@ss.hasPermi('admin:topic:read')")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        DtsTopic topic = topicService.findById(id);
        logger.info("【请求结束】推广管理->专题管理->详情:响应结果:{}", JSONObject.toJSONString(topic));
        return AjaxResult.success(topic);
    }

    @PreAuthorize("@ss.hasPermi('admin:topic:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsTopic topic) {
        try {
            //生成主题的分享URL
            String shareUrl = qCodeService.createShareTopicImage(topic.getId(), topic.getPicUrl(),
                    topic.getSubtitle(), topic.getPrice());
            topic.setShareUrl(shareUrl);
        } catch (Exception e) {
            logger.error("专题生成分享图URL出错：{}", e.getMessage());
            e.printStackTrace();
        }

        if (topicService.updateById(topic) == 0) {
            logger.error("推广管理->专题管理->编辑 错误:{}", "更新数据失败!");
            return "更新数据失败";
        }

        logger.info("【请求结束】推广管理->专题管理->编辑,响应结果:{}", JSONObject.toJSONString(topic));
        return AjaxResult.success(topic);
    }

    @PreAuthorize("@ss.hasPermi('admin:topic:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsTopic topic) {
        topicService.deleteById(topic.getId());

        logger.info("【请求结束】推广管理->专题管理->删除,响应结果:{}", "成功!");
        return AjaxResult.success();
    }

}
