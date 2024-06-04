package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.domain.DtsArticle;
import com.ruoyi.dts.service.DtsArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 公告管理
 */
@RestController
@RequestMapping("/admin/article")
@Validated
public class AdminArticleController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminArticleController.class);

    @Autowired
    private DtsArticleService articleService;

    /**
     * 查询公告列表
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:article:list')")
    @GetMapping("/list")
    public Object list(String title) {
        startPage();
        List<DtsArticle> articleList = articleService.querySelective(title);
        return getDataTable(articleList);
    }

    /**
     * 编辑公告
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:article:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsArticle article) {
//        if (StringUtils.isEmpty(article.getType())) {
//            article.setType(ArticleType.ANNOUNCE.type());//如果没有传入类型，默认为信息公告
//        }
        if (articleService.updateById(article) == 0) {
            logger.error("推广管理->公告管理->编辑错误:{}", "更新数据失败");
            throw new RuntimeException("更新数据失败");
        }
        logger.info("【请求结束】推广管理->公告管理->编辑,响应结果:{}", "成功!");
        return success();
    }

    /**
     * 删除商品
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:article:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsArticle article) {
        Integer id = article.getId();
        if (id == null) {
            return error("参数为空");
        }

        articleService.deleteById(id);

        logger.info("【请求结束】推广管理->公告管理->删除,响应结果:{}", "成功");
        return success();
    }


    /**
     * 文章公告信息
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @PreAuthorize("@ss.hasPermi('admin:article:read')")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        DtsArticle article = null;
        try {
            article = articleService.findById(id);
        } catch (NullPointerException e) {
            System.out.println("aaa");
        } catch (Exception e) {
            logger.error("获取文章公告失败,文章id：{}", id);
            e.printStackTrace();
        }
        // 这里不打印响应结果，文章内容信息较多
        // logger.info("【请求结束】获取公告文章,响应结果：{}",JSONObject.toJSONString(article));
        return success(article);
    }

    /**
     * 添加公告
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:article:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsArticle article) {

        String title = article.getTitle();
        articleService.add(article);

        logger.info("【请求结束】推广管理->公告管理->发布公告,响应结果:{}", "成功!");
        return success();
    }
}
