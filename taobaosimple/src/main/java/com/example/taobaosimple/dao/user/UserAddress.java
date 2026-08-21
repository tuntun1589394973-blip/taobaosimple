package com.example.taobaosimple.dao.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAddress {
    private Long id;
    private Long userId;

    private String receiverName;
    private String receiverPhone;

    private String region;
    private String detailAddress;
    private String postalCode;

    private Integer isDefault;
    private Integer isTop;
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
