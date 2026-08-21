package com.example.taobaosimple.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVo {

    private Long cartId;

    private Long goodsId;
    private String goodsName;
    private BigDecimal price;
    private String photoUrl;
    private Integer inventory;

    private Integer quantity;
    private Integer selected;

    private Integer status;
    private Integer deleted;
}
