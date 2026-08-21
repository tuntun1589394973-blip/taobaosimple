package com.example.taobaosimple.service.impl;

import com.example.taobaosimple.common.constant.ErrorCodeEnum;
import com.example.taobaosimple.common.exception.BusinessException;
import com.example.taobaosimple.dao.goods.CartItem;
import com.example.taobaosimple.dto.AddCartDTO;
import com.example.taobaosimple.mapper.CartItemMapper;
import com.example.taobaosimple.mapper.GoodsMapper;
import com.example.taobaosimple.service.ICartItemService;
import com.example.taobaosimple.vo.CartItemVo;
import com.example.taobaosimple.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartItemServiceImpl implements ICartItemService {

    private final CartItemMapper cartItemMapper;
    private final GoodsMapper goodsMapper;

    public CartItemServiceImpl(CartItemMapper cartItemMapper,
                               GoodsMapper goodsMapper) {
        this.cartItemMapper = cartItemMapper;
        this.goodsMapper = goodsMapper;
    }

    /**
     * 添加商品到购物车
     */
    @Override
    @Transactional
    public void insert(Long userId, AddCartDTO addCartDTO) {
        // 基础参数校验
        if (userId == null || addCartDTO == null
                || addCartDTO.getGoodsId() == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }

        // 查询商品及库存
        Long goodsId = addCartDTO.getGoodsId();
        GoodsVo goods = goodsMapper.selectGoodsById(goodsId);
        if (goods == null || goods.getInventory() == null
                || goods.getInventory() <= 0) {
            throw new BusinessException(ErrorCodeEnum.GOODS_OUT_OF_STOCK);
        }

        // 默认数量为 1
        Integer addQuantity = addCartDTO.getQuantity();
        if (addQuantity == null || addQuantity <= 0) {
            addQuantity = 1;
        }

        // 查询购物车中是否已存在该商品
        CartItem cartItem = cartItemMapper.selectByUserIdAndGoodsId(userId, goodsId);

        if (cartItem == null) {
            // 不存在则新增一条购物车记录
            CartItem newCartItem = new CartItem();
            newCartItem.setGoodsId(goodsId);
            newCartItem.setUserId(userId);
            newCartItem.setQuantity(Math.min(addQuantity, goods.getInventory()));
            newCartItem.setSelected(1);
            if (cartItemMapper.insertCartItem(newCartItem) <= 0) {
                throw new BusinessException(ErrorCodeEnum.CART_OPERATION_FAILED);
            }
            return;
        }

        // 已存在则累加数量，但不超过库存
        Integer currentQuantity = cartItem.getQuantity() == null
                ? 0
                : cartItem.getQuantity();
        Integer newQuantity = Math.min(
                currentQuantity + addQuantity,
                goods.getInventory()
        );

        if (cartItemMapper.updateQuantityById(userId, goodsId, newQuantity) <= 0) {
            throw new BusinessException(ErrorCodeEnum.CART_OPERATION_FAILED);
        }
    }

    /**
     * 查询用户购物车列表
     */
    @Override
    public List<CartItemVo> selectCartItemByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        return cartItemMapper.selectCartItemByUserId(userId);
    }

    /**
     * 从购物车删除指定商品
     */
    @Override
    public void deleteCartItemByGoodsById(Long goodsId, Long userId) {
        if (goodsId == null || userId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        if (cartItemMapper.deleteCartItemByGoodsById(goodsId, userId) <= 0) {
            throw new BusinessException(ErrorCodeEnum.CART_ITEM_NOT_FOUND);
        }
    }

    /**
     * 修改购物车项数量
     */
    @Override
    public void updateQuantityById(Long goodsId, Integer quantity, Long userId) {
        if (userId == null || goodsId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }

        // 查询商品并获取库存上限
        GoodsVo goods = goodsMapper.selectGoodsById(goodsId);
        if (goods == null || goods.getInventory() == null
                || goods.getInventory() <= 0) {
            throw new BusinessException(ErrorCodeEnum.GOODS_OUT_OF_STOCK);
        }

        // 规范化目标数量
        int target;
        if (quantity == null || quantity <= 0) {
            target = 1;
        } else if (quantity > goods.getInventory()) {
            target = goods.getInventory();
        } else {
            target = quantity;
        }

        if (cartItemMapper.updateQuantityById(userId, goodsId, target) <= 0) {
            throw new BusinessException(ErrorCodeEnum.CART_ITEM_NOT_FOUND);
        }
    }
}
