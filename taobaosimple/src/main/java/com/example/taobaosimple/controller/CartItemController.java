package com.example.taobaosimple.controller;

import com.example.taobaosimple.common.resp.RestResp;
import com.example.taobaosimple.common.security.LoginUser;
import com.example.taobaosimple.dto.AddCartDTO;
import com.example.taobaosimple.service.ICartItemService;
import com.example.taobaosimple.vo.CartItemVo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车模块控制器
 */
@RestController
@RequestMapping("/api/v1/cart")
public class CartItemController {

    private final ICartItemService cartItemService;

    public CartItemController(ICartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("addCartItem")
    public RestResp<Void> addCartItem(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody AddCartDTO addCartDTO) {
        cartItemService.insert(loginUser.userId(), addCartDTO);
        return RestResp.success();
    }

    /**
     * 查询当前用户购物车列表
     */
    @GetMapping("selectCartItemByUserId")
    public RestResp<List<CartItemVo>> selectCartItemByUserId(
            @AuthenticationPrincipal LoginUser loginUser) {
        return RestResp.success(
                cartItemService.selectCartItemByUserId(loginUser.userId())
        );
    }

    /**
     * 根据商品 ID 从购物车删除
     */
    @PostMapping("deleteCartItemByGoodsById")
    public RestResp<Void> deleteCartItemByGoodsById(
            @RequestParam Long goodsId,
            @AuthenticationPrincipal LoginUser loginUser) {
        cartItemService.deleteCartItemByGoodsById(goodsId, loginUser.userId());
        return RestResp.success();
    }

    /**
     * 修改购物车项数量
     */
    @PostMapping("updateQuantityById")
    public RestResp<Void> updateQuantityById(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Long goodsId,
            @RequestParam Integer quantity) {
        cartItemService.updateQuantityById(goodsId, quantity, loginUser.userId());
        return RestResp.success();
    }
}
