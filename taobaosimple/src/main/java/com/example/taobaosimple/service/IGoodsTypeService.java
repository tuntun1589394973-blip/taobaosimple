package com.example.taobaosimple.service;

import com.example.taobaosimple.dao.goods.GoodsType;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface IGoodsTypeService {

    /**
     * 查询所有商品分类
     */
    List<GoodsType> getGoodsType();
}
