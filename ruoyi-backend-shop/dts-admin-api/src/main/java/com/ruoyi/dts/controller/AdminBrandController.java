package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.service.AdminBrandService;
import com.ruoyi.dts.util.DtsBrandVo;
import com.ruoyi.dts.core.qcode.QCodeService;
import com.ruoyi.dts.db.domain.DtsBrand;
import com.ruoyi.dts.db.domain.DtsCategory;
import com.ruoyi.dts.db.service.DtsBrandService;
import com.ruoyi.dts.db.service.DtsCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/brand")
@Validated
public class AdminBrandController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminBrandController.class);

    @Autowired
    private DtsBrandService brandService;

    @Autowired
    private DtsCategoryService categoryService;

    @Autowired
    private AdminBrandService adminBrandService;

    @Autowired
    private QCodeService qCodeService;

    @PreAuthorize("@ss.hasPermi('admin:brand:list')")
    @GetMapping("/list")
    public Object list(String id, String name) {
        startPage();
        List<DtsBrand> brandList = brandService.querySelective(id, name);

        List<DtsBrandVo> brandVoList = new ArrayList<DtsBrandVo>(brandList == null ? 0 : brandList.size());
        for (DtsBrand brand : brandList) {
            DtsBrandVo brandVo = new DtsBrandVo();
            BeanUtils.copyProperties(brand, brandVo);//对象属性的复制
            // 用于展示商品归属的类目（页面级联下拉控件数据展示）
            Integer categoryId = brand.getDefaultCategoryId();
            DtsCategory category = categoryService.findById(categoryId);
            Integer[] categoryIds = new Integer[]{};
            if (category != null) {
                Integer parentCategoryId = category.getPid();
                categoryIds = new Integer[]{parentCategoryId, categoryId};
                brandVo.setCategoryIds(categoryIds);
            }
            brandVoList.add(brandVo);
        }
        return getDataTable(brandVoList);
    }

    @PreAuthorize("@ss.hasPermi('admin:brand:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsBrand brand) {

        try {
            //生成店铺的分享URL
            String defaultCategory = brandService.getBrandCategory(brand.getDefaultCategoryId());
            String shareUrl = qCodeService.createBrandImage(null, brand.getId(), brand.getPicUrl(), brand.getName(),
                    defaultCategory);
            brand.setShareUrl(shareUrl);
        } catch (Exception e) {
            logger.error("生成品牌商铺分享图URL出错：{}", e.getMessage());
            e.printStackTrace();
        }

        brandService.add(brand);
        logger.info("【请求结束】商场管理->品牌管理->添加:响应结果:{}", JSONObject.toJSONString(brand));
        return AjaxResult.success(brand);
    }

    @PreAuthorize("@ss.hasPermi('admin:brand:read')")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        DtsBrand brand = brandService.findById(id);

        logger.info("【请求结束】商场管理->品牌管理->详情:响应结果:{}", JSONObject.toJSONString(brand));
        return AjaxResult.success(brand);
    }

    @PreAuthorize("@ss.hasPermi('admin:brand:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsBrand brand) {

        try {
            //生成店铺的分享URL
            String defaultCategory = brandService.getBrandCategory(brand.getDefaultCategoryId());
            String shareUrl = qCodeService.createBrandImage(null, brand.getId(), brand.getPicUrl(), brand.getName(),
                    defaultCategory);
            brand.setShareUrl(shareUrl);
        } catch (Exception e) {
            logger.error("生成品牌商铺分享图URL出错：{}", e.getMessage());
            e.printStackTrace();
        }

        logger.info("【请求结束】商场管理->品牌管理->编辑:响应结果:{}", JSONObject.toJSONString(brand));
        return AjaxResult.success(brand);
    }

    @PreAuthorize("@ss.hasPermi('admin:brand:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsBrand brand) {
        Integer id = brand.getId();
        brandService.deleteById(id);

        logger.info("【请求结束】商场管理->品牌管理->删除,响应结果:{}", "成功！");
        return AjaxResult.success();
    }


    @GetMapping("/catAndAdmin")
    public Object catAndAdmin() {
        return adminBrandService.catAndAdmin();
    }

}
