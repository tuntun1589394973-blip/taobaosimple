package com.example.taobaosimple.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseVo {
    private String token;

    private Long userId;

    private String username;
}
