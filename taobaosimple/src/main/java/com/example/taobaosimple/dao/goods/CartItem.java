package com.example.taobaosimple.dao.goods;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long id;

    private Long userId;
    private Long goodsId;

    private Integer quantity;
    private Integer selected;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
