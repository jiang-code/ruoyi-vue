package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.ShopUser;

/**
 * 用户Service接口
 * 
 * @author ruoyi
 * @date 2024-05-16
 */
public interface IShopUserService 
{
    /**
     * 查询用户
     * 
     * @param id 用户主键
     * @return 用户
     */
    public ShopUser selectShopUserById(Long id);

    /**
     * 查询用户列表
     * 
     * @param shopUser 用户
     * @return 用户集合
     */
    public List<ShopUser> selectShopUserList(ShopUser shopUser);

    /**
     * 新增用户
     * 
     * @param shopUser 用户
     * @return 结果
     */
    public int insertShopUser(ShopUser shopUser);

    /**
     * 修改用户
     * 
     * @param shopUser 用户
     * @return 结果
     */
    public int updateShopUser(ShopUser shopUser);

    /**
     * 批量删除用户
     * 
     * @param ids 需要删除的用户主键集合
     * @return 结果
     */
    public int deleteShopUserByIds(Long[] ids);

    /**
     * 删除用户信息
     * 
     * @param id 用户主键
     * @return 结果
     */
    public int deleteShopUserById(Long id);
}
