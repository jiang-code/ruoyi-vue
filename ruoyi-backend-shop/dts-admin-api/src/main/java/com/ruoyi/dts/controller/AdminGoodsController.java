package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.dao.GoodsAllinone;
import com.ruoyi.dts.service.AdminDataAuthService;
import com.ruoyi.dts.service.AdminGoodsService;
import com.ruoyi.dts.db.domain.DtsGoods;
import com.ruoyi.dts.db.service.DtsGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/goods")
@Validated
public class AdminGoodsController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminGoodsController.class);

    @Autowired
    private AdminGoodsService adminGoodsService;

    @Autowired
    private AdminDataAuthService adminDataAuthService;

    @Autowired
    private DtsGoodsService goodsService;

    /**
     * 查询商品
     *
     * @param goodsSn
     * @param name
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:goods:list')")
    @GetMapping("/list")
    public Object list(String goodsSn, String name) {

        //需要区分数据权限，如果属于品牌商管理员，则需要获取当前用户管理品牌店铺
        List<Integer> brandIds = null;
        if (adminDataAuthService.isBrandManager()) {
            brandIds = adminDataAuthService.getBrandIds();
            logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}", JSONObject.toJSONString(brandIds));

            if (brandIds == null || brandIds.size() == 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("total", 0L);
                data.put("items", null);

                logger.info("【请求结束】商品管理->商品管理->查询,响应结果:{}", JSONObject.toJSONString(data));
                return AjaxResult.success(data);
            }
        }
        startPage();
        List<DtsGoods> goodsList = goodsService.querySelective(goodsSn, name,brandIds);

        return getDataTable(goodsList);
    }

    @GetMapping("/catAndBrand")
    public Object catAndBrand() {
        return adminGoodsService.catAndBrand();
    }

    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:goods:update')")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsAllinone goodsAllinone) {

        return adminGoodsService.update(goodsAllinone);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:goods:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsGoods goods) {
        adminGoodsService.delete(goods);
        return AjaxResult.success();
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:goods:create')")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsAllinone goodsAllinone) {
        adminGoodsService.create(goodsAllinone);
        return AjaxResult.success() ;
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('admin:goods:read')")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        Object detail = adminGoodsService.detail(id);
        return AjaxResult.success(detail);
    }

}
