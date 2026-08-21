package com.example.taobaosimple.service.impl;

import com.example.taobaosimple.common.constant.ErrorCodeEnum;
import com.example.taobaosimple.common.exception.BusinessException;
import com.example.taobaosimple.dao.user.UserAddress;
import com.example.taobaosimple.mapper.UserAddressMapper;
import com.example.taobaosimple.service.IUserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户收货地址服务实现类
 */
@Service
public class UserAddressServiceImpl implements IUserAddressService {

    private final UserAddressMapper userAddressMapper;

    public UserAddressServiceImpl(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    /**
     * 新增收货地址
     */
    @Override
    @Transactional
    public void addUserAddress(UserAddress userAddress) {
        // 基础信息校验
        if (!isValidAddress(userAddress)) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_INFO_INVALID);
        }

        // 规范化默认/置顶标志位
        normalizeFlags(userAddress);

        // 如果设为默认地址，先清除其他默认
        if (Integer.valueOf(1).equals(userAddress.getIsDefault())) {
            userAddressMapper.clearDefaultByUserId(userAddress.getUserId());
        }

        if (userAddressMapper.insertUserAddress(userAddress) <= 0) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_UPDATE_FAILED);
        }
    }

    /**
     * 查询用户收货地址列表
     */
    @Override
    public List<UserAddress> selectUserAddressByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        return userAddressMapper.selectUserAddressByUserId(userId);
    }

    /**
     * 更新收货地址
     */
    @Override
    @Transactional
    public void updateUserAddress(UserAddress userAddress) {
        // 基础信息 + ID 校验
        if (!isValidAddress(userAddress) || userAddress.getId() == null) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_INFO_INVALID);
        }

        // 规范化标志位
        normalizeFlags(userAddress);

        // 如果设为默认地址，先清除其他默认
        if (Integer.valueOf(1).equals(userAddress.getIsDefault())) {
            userAddressMapper.clearDefaultByUserId(userAddress.getUserId());
        }

        if (userAddressMapper.updateUserAddress(userAddress) <= 0) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_UPDATE_FAILED);
        }
    }

    /**
     * 设置/取消默认地址
     */
    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long id, Integer isDefault) {
        if (userId == null || id == null || !isFlag(isDefault)) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        // 设为默认时先清除其他默认
        if (isDefault == 1) {
            userAddressMapper.clearDefaultByUserId(userId);
        }
        if (userAddressMapper.setDefaultById(userId, id, isDefault) <= 0) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND);
        }
    }

    /**
     * 设置/取消置顶地址
     */
    @Override
    public void setIsTopById(Long userId, Long id, Integer isTop) {
        if (userId == null || id == null || !isFlag(isTop)) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        if (userAddressMapper.setIsTopById(userId, id, isTop) <= 0) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND);
        }
    }

    /**
     * 删除收货地址
     */
    @Override
    public void deleteUserAddressById(Long userId, Long id) {
        if (userId == null || id == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        if (userAddressMapper.deleteUserAddressById(userId, id) <= 0) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND);
        }
    }

    /**
     * 校验地址基础信息是否完整
     */
    private boolean isValidAddress(UserAddress address) {
        return address != null
                && address.getUserId() != null
                && address.getReceiverName() != null
                && !address.getReceiverName().isBlank()
                && address.getReceiverPhone() != null
                && !address.getReceiverPhone().isBlank()
                && address.getRegion() != null
                && !address.getRegion().isBlank()
                && address.getDetailAddress() != null
                && !address.getDetailAddress().isBlank();
    }

    /**
     * 将非法的标志位值归零
     */
    private void normalizeFlags(UserAddress address) {
        if (!isFlag(address.getIsDefault())) {
            address.setIsDefault(0);
        }
        if (!isFlag(address.getIsTop())) {
            address.setIsTop(0);
        }
    }

    /**
     * 判断值是否为合法标志位（0 或 1）
     */
    private boolean isFlag(Integer value) {
        return Integer.valueOf(0).equals(value)
                || Integer.valueOf(1).equals(value);
    }
}
