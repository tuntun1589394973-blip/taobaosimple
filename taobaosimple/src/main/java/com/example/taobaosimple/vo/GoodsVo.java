package com.example.taobaosimple.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsVo {
    private Long id;

    private String name;

    private String typeName;

    private BigDecimal price;

    private Long typeId;

    private String introduction;

    private String photoUrl;

    private Integer inventory;

    private Integer status;

    private Integer sales;

    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
