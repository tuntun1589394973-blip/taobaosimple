package com.example.taobaosimple.controller;


import com.example.taobaosimple.common.resp.RestResp;
import com.example.taobaosimple.common.security.LoginUser;
import com.example.taobaosimple.dao.user.User;
import com.example.taobaosimple.dto.LoginDTO;
import com.example.taobaosimple.dto.UserRegisterDTO;
import com.example.taobaosimple.service.IUserService;
import com.example.taobaosimple.vo.LoginResponseVo;
import com.example.taobaosimple.vo.UserProfileVo;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户模块控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public RestResp<Void> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return RestResp.success();
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/checkUsername")
    public RestResp<Boolean> checkUsername(@RequestParam String username) {
        return RestResp.success(userService.checkUsername(username));
    }

    /**
     * 用户登录（返回 token 和基础信息）
     */
    @PostMapping("/login")
    public RestResp<LoginResponseVo> login(@RequestBody @Valid LoginDTO loginDTO) {
        return RestResp.success(
                userService.login(loginDTO.getUsername(), loginDTO.getPassword())
        );
    }

    /**
     * 更新当前登录用户的资料
     */
    @PostMapping("/update")
    public RestResp<Void> update(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody User user) {
        // 强制使用当前登录账号作为更新目标，防止越权
        user.setUsername(loginUser.username());
        userService.update(user);
        return RestResp.success();
    }

    /**
     * 查询当前登录用户的资料
     */
    @PostMapping("/select")
    public RestResp<UserProfileVo> select(
            @AuthenticationPrincipal LoginUser loginUser) {
        return RestResp.success(
                userService.selectByUsername(loginUser.username())
        );
    }

    /**
     * 注销当前账号（需密码二次确认）
     */
    @PostMapping("/logout")
    public RestResp<Void> logOut(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam String password) {
        userService.delete(loginUser.username(), password);
        return RestResp.success();
    }

    /**
     * 上传当前登录用户的头像
     */
    @PostMapping("/uploadAvatar")
    public RestResp<Void> uploadAvatar(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam MultipartFile file) throws IOException {
        userService.insertAvatar(loginUser.username(), file);
        return RestResp.success();
    }
}
