package com.example.taobaosimple.mapper;

import com.example.taobaosimple.dao.goods.CartItem;
import com.example.taobaosimple.vo.CartItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {

    int insertCartItem(CartItem cartItem);

    CartItem selectByUserIdAndGoodsId(
            @Param("userId") Long userId,
            @Param("goodsId") Long goodsId);

    int updateQuantityById(
            @Param("userId") Long userId,
            @Param("goodsId") Long goodsId,
            @Param("quantity") Integer quantity);

    List<CartItemVo> selectCartItemByUserId(
            @Param("userId") Long userId);

    int deleteCartItemByGoodsById(
            @Param("goodsId") Long goodsId,
            @Param("userId") Long userId);



}
