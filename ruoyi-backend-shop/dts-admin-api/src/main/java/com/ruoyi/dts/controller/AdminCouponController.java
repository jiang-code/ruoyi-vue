package com.ruoyi.dts.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.db.domain.DtsCoupon;
import com.ruoyi.dts.db.domain.DtsCouponUser;
import com.ruoyi.dts.db.service.DtsCouponService;
import com.ruoyi.dts.db.service.DtsCouponUserService;
import com.ruoyi.dts.db.util.CouponConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/coupon")
@Validated
public class AdminCouponController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminCouponController.class);

    @Autowired
    private DtsCouponService couponService;
    @Autowired
    private DtsCouponUserService couponUserService;

    @PreAuthorize("@ss.hasPermi('admin:coupon:list')")
    @GetMapping("/list")
    public Object list(String name, Short type, Short status) {
        startPage();
        List<DtsCoupon> couponList = couponService.querySelective(name, type, status);
        return getDataTable(couponList);
    }

    @PreAuthorize("@ss.hasPermi('admin:coupon:listuser')")
    @GetMapping("/listuser")
    public Object listuser(Integer userId, Integer couponId, Short status) {

        startPage();
        List<DtsCouponUser> couponList = couponUserService.queryList(userId, couponId, status);
        return getDataTable(couponList);
    }


    @PreAuthorize("@ss.hasPermi('admin:coupon:create')")
    @PostMapping("/create")
    public Object create(@RequestBody DtsCoupon coupon) {
        // 如果是兑换码类型，则这里需要生存一个兑换码
        if (coupon.getType().equals(CouponConstant.TYPE_CODE)) {
            String code = couponService.generateCode();
            coupon.setCode(code);
        }

        couponService.add(coupon);
        return AjaxResult.success(coupon);
    }

    @PreAuthorize("@ss.hasPermi('admin:coupon:read')")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {

        DtsCoupon coupon = couponService.findById(id);
        return AjaxResult.success(coupon);
    }

    @PreAuthorize("@ss.hasPermi('admin:coupon:update')")
    @PostMapping("/update")
    public Object update(@RequestBody DtsCoupon coupon) {

        couponService.updateById(coupon);
        return AjaxResult.success(coupon);
    }

    @PreAuthorize("@ss.hasPermi('admin:coupon:delete')")
    @PostMapping("/delete")
    public Object delete(@RequestBody DtsCoupon coupon) {
        couponService.deleteById(coupon.getId());
        return AjaxResult.success();
    }

}
