package com.example.taobaosimple.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @Schema(description = "账号",required = true)
    @NotBlank(message="账号不能为空！")
    private String username;

    @Schema(description = "密码",required = true)
    @NotBlank(message="密码不能为空！")
    private String password;

}
