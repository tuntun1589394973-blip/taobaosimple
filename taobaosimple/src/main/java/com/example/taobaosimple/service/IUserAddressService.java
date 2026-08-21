package com.example.taobaosimple.service;

import com.example.taobaosimple.dao.user.UserAddress;

import java.util.List;

/**
 * 用户收货地址服务接口
 */
public interface IUserAddressService {

    /**
     * 新增收货地址
     */
    void addUserAddress(UserAddress userAddress);

    /**
     * 查询指定用户的收货地址列表
     */
    List<UserAddress> selectUserAddressByUserId(Long userId);

    /**
     * 更新收货地址
     */
    void updateUserAddress(UserAddress userAddress);

    /**
     * 设置/取消默认地址
     */
    void setDefaultAddress(Long userId, Long id, Integer isDefault);

    /**
     * 设置/取消置顶地址
     */
    void setIsTopById(Long userId, Long id, Integer isTop);

    /**
     * 根据 ID 删除收货地址
     */
    void deleteUserAddressById(Long userId, Long id);
}
