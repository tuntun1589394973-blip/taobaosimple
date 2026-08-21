package com.example.taobaosimple.mapper;

import com.example.taobaosimple.dao.user.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    int insertUserAddress(UserAddress userAddress);

    List<UserAddress> selectUserAddressByUserId(@Param("userId") Long userId);

    int updateUserAddress(UserAddress userAddress);

    int clearDefaultByUserId(@Param("userId") Long userId);

    int setDefaultById(@Param("userId") Long userId,
                       @Param("id") Long id,
                       @Param("isDefault") Integer isDefault);

    int setIsTopById(@Param("userId") Long userId,
                     @Param("id") Long id,
                     @Param("isTop") Integer isTop);

    int deleteUserAddressById(@Param("userId") Long userId,
                              @Param("id") Long id);






}
