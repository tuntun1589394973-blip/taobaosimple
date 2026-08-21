package com.example.taobaosimple.controller;

import com.example.taobaosimple.common.resp.RestResp;
import com.example.taobaosimple.common.security.LoginUser;
import com.example.taobaosimple.dao.user.UserAddress;
import com.example.taobaosimple.service.IUserAddressService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址模块控制器
 */
@RestController
@RequestMapping("/api/v1/addresses")
public class UserAddressController {

    private final IUserAddressService userAddressService;

    public UserAddressController(IUserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    /**
     * 新增收货地址
     */
    @PostMapping("addUserAddress")
    public RestResp<Void> addUserAddress(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody UserAddress userAddress) {
        // 强制使用当前登录用户，防止越权
        userAddress.setId(null);
        userAddress.setUserId(loginUser.userId());
        userAddressService.addUserAddress(userAddress);
        return RestResp.success();
    }

    /**
     * 查询当前用户收货地址列表
     */
    @PostMapping("selectUserAddressByUserId")
    public RestResp<List<UserAddress>> selectUserAddressByUserId(
            @AuthenticationPrincipal LoginUser loginUser) {
        return RestResp.success(
                userAddressService.selectUserAddressByUserId(loginUser.userId())
        );
    }

    /**
     * 更新收货地址
     */
    @PostMapping("updateUserAddress")
    public RestResp<Void> updateUserAddress(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody UserAddress userAddress) {
        // 强制绑定当前登录用户
        userAddress.setUserId(loginUser.userId());
        userAddressService.updateUserAddress(userAddress);
        return RestResp.success();
    }

    /**
     * 设置/取消默认地址
     */
    @PostMapping("setDefaultAddress")
    public RestResp<Void> setDefaultAddress(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Long id,
            @RequestParam Integer isDefault) {
        userAddressService.setDefaultAddress(loginUser.userId(), id, isDefault);
        return RestResp.success();
    }

    /**
     * 设置/取消置顶地址
     */
    @PostMapping("setIsTopById")
    public RestResp<Void> setIsTopById(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Long id,
            @RequestParam Integer isTop) {
        userAddressService.setIsTopById(loginUser.userId(), id, isTop);
        return RestResp.success();
    }

    /**
     * 删除收货地址
     */
    @PostMapping("deleteUserAddressById")
    public RestResp<Void> deleteUserAddressById(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Long id) {
        userAddressService.deleteUserAddressById(loginUser.userId(), id);
        return RestResp.success();
    }
}
