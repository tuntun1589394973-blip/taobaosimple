package com.example.taobaosimple.vo;

import com.example.taobaosimple.dao.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileVo {
    private Long id;
    private String username;
    private String virtualName;
    private String phone;
    private String sex;
    private String avatar;

    public static UserProfileVo from(User user) {
        if (user == null) {
            return null;
        }

        return new UserProfileVo(
                user.getId(),
                user.getUsername(),
                user.getVirtualName(),
                user.getPhone(),
                user.getSex(),
                user.getAvatar()
        );
    }
}
