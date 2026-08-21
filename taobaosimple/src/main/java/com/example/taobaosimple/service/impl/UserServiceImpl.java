package com.example.taobaosimple.service.impl;

import com.example.taobaosimple.common.constant.ErrorCodeEnum;
import com.example.taobaosimple.common.exception.BusinessException;
import com.example.taobaosimple.common.security.JwtService;
import com.example.taobaosimple.dao.user.User;
import com.example.taobaosimple.dto.UserRegisterDTO;
import com.example.taobaosimple.mapper.UserMapper;
import com.example.taobaosimple.service.IUserService;
import com.example.taobaosimple.vo.LoginResponseVo;
import com.example.taobaosimple.vo.UserProfileVo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * 注册新用户
     */
    @Override
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        // 参数校验
        if (userRegisterDTO == null
                || userRegisterDTO.getUsername() == null
                || userRegisterDTO.getPassword() == null
                || userRegisterDTO.getUsername().isBlank()
                || userRegisterDTO.getPassword().isBlank()) {
            throw new BusinessException(ErrorCodeEnum.AUTH_CREDENTIALS_EMPTY);
        }

        // 校验用户名是否已存在
        if (!checkUsername(userRegisterDTO.getUsername())) {
            throw new BusinessException(ErrorCodeEnum.USERNAME_EXISTS);
        }

        // 组装用户实体，密码加密
        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setVirtualName("tb" + userRegisterDTO.getUsername());

        try {
            // 执行插入并捕获唯一索引冲突
            if (userMapper.insertUser(user) != 1) {
                throw new BusinessException(ErrorCodeEnum.REGISTER_FAILED);
            }
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCodeEnum.USERNAME_EXISTS);
        }
    }

    /**
     * 检查用户名是否可用（已存在返回 false）
     */
    @Override
    public Boolean checkUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return userMapper.selectUser(username) == null;
    }

    /**
     * 用户登录并生成 token
     */
    @Override
    public LoginResponseVo login(String username, String password) {
        // 入参校验
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new BusinessException(ErrorCodeEnum.AUTH_CREDENTIALS_EMPTY);
        }

        // 查询账号是否存在
        User databaseUser = userMapper.selectUser(username);
        if (databaseUser == null) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_FAILED);
        }

        // 校验密码
        if (!passwordEncoder.matches(password, databaseUser.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_FAILED);
        }

        // 签发 token 并返回
        String token = jwtService.generateToken(databaseUser);
        return new LoginResponseVo(
                token,
                databaseUser.getId(),
                databaseUser.getUsername()
        );
    }

    /**
     * 根据账号查询用户资料
     */
    @Override
    public UserProfileVo selectByUsername(String username) {
        User user = userMapper.selectUser(username);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        return UserProfileVo.from(user);
    }

    /**
     * 更新用户资料
     */
    @Override
    public void update(User user) {
        if (user == null || user.getUsername() == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        if (userMapper.updateUser(user) <= 0) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
    }

    /**
     * 注销账号（软删）
     */
    @Override
    public void delete(String username, String password) {
        // 查询用户
        User databaseUser = userMapper.selectUser(username);
        if (databaseUser == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        // 密码二次校验
        if (!passwordEncoder.matches(password, databaseUser.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_FAILED);
        }
        // 执行软删
        if (userMapper.deleteUser(username) <= 0) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
    }

    /**
     * 上传用户头像
     */
    @Override
    public void insertAvatar(String username, MultipartFile file) throws IOException {
        // 基础校验
        if (username == null || username.isBlank()) {
            throw new BusinessException(ErrorCodeEnum.AUTH_CREDENTIALS_EMPTY);
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.AVATAR_UPLOAD_FAILED);
        }
        // 校验文件大小
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(ErrorCodeEnum.AVATAR_TOO_LARGE);
        }

        // 校验文件类型并确定后缀
        String contentType = file.getContentType();
        String suffix;
        if ("image/jpeg".equals(contentType)) {
            suffix = ".jpg";
        } else if ("image/png".equals(contentType)) {
            suffix = ".png";
        } else {
            throw new BusinessException(ErrorCodeEnum.AVATAR_TYPE_NOT_SUPPORTED);
        }

        // 确保上传目录存在
        String uploadPath = "D:/sjava/taobaosimple/images";
        File path = new File(uploadPath);
        if (!path.exists() && !path.mkdirs()) {
            throw new BusinessException(ErrorCodeEnum.AVATAR_UPLOAD_FAILED);
        }

        // 生成文件名并写入磁盘
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dest = new File(uploadPath + File.separator + fileName);
        file.transferTo(dest);

        // 写回数据库的头像路径
        String avatarPath = "/images/" + fileName;
        if (userMapper.insertAvatar(username, avatarPath) <= 0) {
            throw new BusinessException(ErrorCodeEnum.AVATAR_UPLOAD_FAILED);
        }
    }
}
