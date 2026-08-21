package com.example.taobaosimple.mapper;

import com.example.taobaosimple.dao.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int insertUser(User user);

    User selectUser(@Param("username") String username);

    int updateUser(User user);

    int deleteUser(@Param("username") String username);

    int insertAvatar(@Param("username") String username,
                     @Param("avatar") String avatar);
}
