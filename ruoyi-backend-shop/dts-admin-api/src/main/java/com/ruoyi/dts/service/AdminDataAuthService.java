package com.ruoyi.dts.service;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.dts.db.domain.DtsBrand;
import com.ruoyi.dts.db.service.DtsBrandService;
import com.ruoyi.system.domain.SysUserRoles;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminDataAuthService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private DtsBrandService brandService;

    /**
     * 是否属于运营商管理员，超级管理员除外
     *
     * @return
     */
    public boolean isBrandManager() {
        Integer[] roleIds = null;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            List<SysUserRoles> roleList = sysUserRoleMapper.findRoleListByUserId(loginUser.getUserId());
            Set<String> roles = new HashSet<String>();
            for (SysUserRoles role : roleList) {
                roles.add(role.getRoleKey());
            }
            //仅仅只是品牌管理员且不属于超级管理员
            if (roles.contains("brand") && !roles.contains("admin")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户的管理的品牌商铺
     *
     * @return
     */
    public List<Integer> getBrandIds() {
        List<Integer> brandIds = null;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<DtsBrand> brands = brandService.getAdminBrands(loginUser.getUserId());
        if (brands != null && brands.size() > 0) {
            brandIds = new ArrayList<Integer>();
            for (DtsBrand brand : brands) {
                brandIds.add(brand.getId());
            }
        }
        return brandIds;
    }
}
