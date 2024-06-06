package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.service.AdminDataAuthService;
import com.ruoyi.dts.db.domain.DtsComment;
import com.ruoyi.dts.db.service.DtsCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/comment")
@Validated
public class AdminCommentController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminCommentController.class);

    @Autowired
    private DtsCommentService commentService;

    @Autowired
    private AdminDataAuthService adminDataAuthService;

    @PreAuthorize("@ss.hasPermi('admin:comment:list')")
    @GetMapping("/list")
    public Object list(String userId, String valueId) {

        // 需要区分数据权限，如果属于品牌商管理员，则需要获取当前用户管理品牌店铺
        List<Integer> brandIds = null;
        if (adminDataAuthService.isBrandManager()) {
            brandIds = adminDataAuthService.getBrandIds();
            logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}", JSONObject.toJSONString(brandIds));

            if (brandIds == null || brandIds.size() == 0) {// 如果尚未管理任何入驻店铺，则返回空数据
                Map<String, Object> data = new HashMap<>();
                data.put("total", 0L);
                data.put("items", null);

                logger.info("【请求结束】商品管理->评论管理->查询:{}", JSONObject.toJSONString(data));
                return AjaxResult.success(data);
            }
        }
        List<DtsComment> commentList = null;
        long total = 0L;
        if (brandIds == null || brandIds.size() == 0) {
            startPage();
            commentList = commentService.querySelective(userId, valueId);
        } else {
            startPage();
            commentList = commentService.queryBrandCommentSelective(brandIds, userId, valueId);
        }
        logger.info("【请求结束】商品管理->评论管理->查询:total:{}", total);
        return getDataTable(commentList);
    }

    @PreAuthorize("@ss.hasPermi('admin:comment:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsComment comment) {

        Integer id = comment.getId();
        commentService.deleteById(id);

        logger.info("【请求结束】商品管理->评论管理->删除:响应结果:{}", "成功!");
        return AjaxResult.success();
    }

}
