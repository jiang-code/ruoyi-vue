package com.ruoyi.dts.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.dts.service.AdminDataAuthService;
import com.ruoyi.dts.db.domain.DtsGoods;
import com.ruoyi.dts.db.domain.DtsGroupon;
import com.ruoyi.dts.db.domain.DtsGrouponRules;
import com.ruoyi.dts.db.service.DtsGoodsService;
import com.ruoyi.dts.db.service.DtsGrouponRulesService;
import com.ruoyi.dts.db.service.DtsGrouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/groupon")
@Validated
public class AdminGrouponController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminGrouponController.class);

    @Autowired
    private DtsGrouponRulesService rulesService;
    @Autowired
    private DtsGoodsService goodsService;
    @Autowired
    private DtsGrouponService grouponService;
    @Autowired
    private AdminDataAuthService adminDataAuthService;

    @PreAuthorize("@ss.hasPermi('admin:groupon:read')")
    @GetMapping("/listRecord")
    public Object listRecord(String rulesId) {
        // 需要区分数据权限，如果属于品牌商管理员，则需要获取当前用户管理品牌店铺
        List<Integer> brandIds = null;
        if (adminDataAuthService.isBrandManager()) {
            brandIds = adminDataAuthService.getBrandIds();
            logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}", JSONObject.toJSONString(brandIds));

            if (brandIds == null || brandIds.size() == 0) {// 如果尚未管理任何入驻店铺，则返回空数据
                Map<String, Object> data = new HashMap<>();
                data.put("total", 0L);
                data.put("items", null);

                logger.info("【请求结束】推广管理->团购管理->详情,响应结果:{}", JSONObject.toJSONString(data));
                return AjaxResult.success(data);
            }
        }

        List<DtsGroupon> grouponList = null;
        if (brandIds == null || brandIds.size() == 0) {
            startPage();
            grouponList = grouponService.querySelective(rulesId);
        } else {
            startPage();
            grouponList = grouponService.queryBrandGroupons(brandIds, rulesId);
        }

        List<Map<String, Object>> records = new ArrayList<>();
        for (DtsGroupon groupon : grouponList) {
            try {
                Map<String, Object> RecordData = new HashMap<>();
                List<DtsGroupon> subGrouponList = grouponService.queryJoinRecord(groupon.getId());
                DtsGrouponRules rules = rulesService.queryById(groupon.getRulesId());
                DtsGoods goods = null;
                if (rules != null) {
                    goods = goodsService.findById(rules.getGoodsId().intValue());
                }

                RecordData.put("groupon", groupon);
                RecordData.put("subGroupons", subGrouponList);
                RecordData.put("rules", rules);
                RecordData.put("goods", goods);

                records.add(RecordData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return getDataTable(records);
    }

    @PreAuthorize("@ss.hasPermi('admin:groupon:list')")
    @GetMapping("/list")
    public Object list(String goodsId) {
        // 需要区分数据权限，如果属于品牌商管理员，则需要获取当前用户管理品牌店铺
        List<Integer> brandIds = null;
        if (adminDataAuthService.isBrandManager()) {
            brandIds = adminDataAuthService.getBrandIds();
            logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}", JSONObject.toJSONString(brandIds));

            if (brandIds == null || brandIds.size() == 0) {// 如果尚未管理任何入驻店铺，则返回空数据
                Map<String, Object> data = new HashMap<>();
                data.put("total", 0L);
                data.put("items", null);

                logger.info("【请求结束】推广管理->团购管理->查询,响应结果:{}", JSONObject.toJSONString(data));
                return AjaxResult.success(data);
            }
        }

        List<DtsGrouponRules> rulesList = null;
        if (brandIds == null || brandIds.size() == 0) {
            startPage();
            rulesList = rulesService.querySelective(goodsId);
        } else {
            startPage();
            rulesList = rulesService.queryBrandGrouponRules(brandIds, goodsId);
        }

        return getDataTable(rulesList);
    }


    @PreAuthorize("@ss.hasPermi('admin:groupon:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsGrouponRules grouponRules) {


        Integer goodsId = grouponRules.getGoodsId().intValue();
        DtsGoods goods = goodsService.findById(goodsId);

        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());

        rulesService.updateById(grouponRules);

        logger.info("【请求结束】推广管理->团购管理->编辑,响应结果:{}", "成功！");
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('admin:groupon:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsGrouponRules grouponRules) {
        Long goodsId = grouponRules.getGoodsId();
        DtsGoods goods = null;
        /**
         * 如果输入的值为INT范围内，则先用goodsId找,如果超出范围，
         * 如果未找到，则转换为goodsSn找再找商品
         */
        if (goodsId.intValue() < Integer.MAX_VALUE) {
            goods = goodsService.findById(goodsId.intValue());
        }
        if (goods == null) {
            goods = goodsService.findByGoodsSn(goodsId.toString());
        }


        grouponRules.setGoodsId(goods.getId().longValue());//最终存库只存商品id
        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());

        rulesService.createRules(grouponRules);

        logger.info("【请求结束】推广管理->团购管理->添加,响应结果:{}", JSONObject.toJSONString(grouponRules));
        return AjaxResult.success(grouponRules);
    }

    @PreAuthorize("@ss.hasPermi('admin:groupon:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsGrouponRules grouponRules) {

        Integer id = grouponRules.getId();
        rulesService.delete(id);

        return AjaxResult.success();
    }
}
