package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ShopUserMapper;
import com.ruoyi.system.domain.ShopUser;
import com.ruoyi.system.service.IShopUserService;

/**
 * 用户Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-05-16
 */
@Service
public class ShopUserServiceImpl implements IShopUserService 
{
    @Autowired
    private ShopUserMapper shopUserMapper;

    /**
     * 查询用户
     * 
     * @param id 用户主键
     * @return 用户
     */
    @Override
    public ShopUser selectShopUserById(Long id)
    {
        return shopUserMapper.selectShopUserById(id);
    }

    /**
     * 查询用户列表
     * 
     * @param shopUser 用户
     * @return 用户
     */
    @Override
    public List<ShopUser> selectShopUserList(ShopUser shopUser)
    {
        return shopUserMapper.selectShopUserList(shopUser);
    }

    /**
     * 新增用户
     * 
     * @param shopUser 用户
     * @return 结果
     */
    @Override
    public int insertShopUser(ShopUser shopUser)
    {
        return shopUserMapper.insertShopUser(shopUser);
    }

    /**
     * 修改用户
     * 
     * @param shopUser 用户
     * @return 结果
     */
    @Override
    public int updateShopUser(ShopUser shopUser)
    {
        shopUser.setUpdateTime(DateUtils.getNowDate());
        return shopUserMapper.updateShopUser(shopUser);
    }

    /**
     * 批量删除用户
     * 
     * @param ids 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteShopUserByIds(Long[] ids)
    {
        return shopUserMapper.deleteShopUserByIds(ids);
    }

    /**
     * 删除用户信息
     * 
     * @param id 用户主键
     * @return 结果
     */
    @Override
    public int deleteShopUserById(Long id)
    {
        return shopUserMapper.deleteShopUserById(id);
    }
}
