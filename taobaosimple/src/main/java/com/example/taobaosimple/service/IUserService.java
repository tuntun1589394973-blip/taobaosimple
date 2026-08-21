package com.example.taobaosimple.service;

import com.example.taobaosimple.dao.user.User;
import com.example.taobaosimple.dto.UserRegisterDTO;
import com.example.taobaosimple.vo.LoginResponseVo;
import com.example.taobaosimple.vo.UserProfileVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户服务接口
 */
public interface IUserService {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 校验用户名是否可用
     */
    Boolean checkUsername(String username);

    /**
     * 用户登录并返回 token 等信息
     */
    LoginResponseVo login(String username, String password);

    /**
     * 根据账号查询用户信息
     */
    UserProfileVo selectByUsername(String username);

    /**
     * 更新用户资料
     */
    void update(User user);

    /**
     * 注销账号（密码二次校验）
     */
    void delete(String username, String password);

    /**
     * 上传头像
     */
    void insertAvatar(String username, MultipartFile file) throws IOException;
}
