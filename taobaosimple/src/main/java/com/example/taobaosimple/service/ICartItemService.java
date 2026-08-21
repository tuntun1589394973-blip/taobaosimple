package com.example.taobaosimple.service;

import com.example.taobaosimple.dto.AddCartDTO;
import com.example.taobaosimple.vo.CartItemVo;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface ICartItemService {

    /**
     * 添加商品到购物车（库存不足时抛异常）
     */
    void insert(Long userId, AddCartDTO addCartDTO);

    /**
     * 查询用户购物车列表
     */
    List<CartItemVo> selectCartItemByUserId(Long userId);

    /**
     * 根据商品 ID 从购物车删除
     */
    void deleteCartItemByGoodsById(Long goodsId, Long userId);

    /**
     * 修改购物车项数量（受库存限制）
     */
    void updateQuantityById(Long goodsId, Integer quantity, Long userId);
}
