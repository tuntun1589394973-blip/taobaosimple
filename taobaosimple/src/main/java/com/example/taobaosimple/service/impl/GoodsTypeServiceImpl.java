package com.example.taobaosimple.service.impl;

import com.example.taobaosimple.dao.goods.GoodsType;
import com.example.taobaosimple.mapper.GoodsTypeMapper;
import com.example.taobaosimple.service.IGoodsTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类服务实现类
 */
@Service
public class GoodsTypeServiceImpl implements IGoodsTypeService {

    private final GoodsTypeMapper goodsTypeMapper;

    public GoodsTypeServiceImpl(GoodsTypeMapper goodsTypeMapper) {
        this.goodsTypeMapper = goodsTypeMapper;
    }

    /**
     * 查询所有商品分类
     */
    @Override
    public List<GoodsType> getGoodsType() {
        return goodsTypeMapper.selectAll();
    }
}
