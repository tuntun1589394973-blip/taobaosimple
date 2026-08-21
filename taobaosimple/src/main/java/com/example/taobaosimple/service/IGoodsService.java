package com.example.taobaosimple.service;

import com.example.taobaosimple.dao.goods.Goods;
import com.example.taobaosimple.vo.GoodsVo;

import java.util.List;

/**
 * 商品服务接口
 */
public interface IGoodsService {

    /**
     * 根据名称搜索商品
     */
    List<Goods> selectGoodsByName(String goodsName);

    /**
     * 获取随机推荐商品（排除已在 ids 中的商品）
     */
    List<GoodsVo> selectRandomGoods(Integer limit, List<Long> ids);

    /**
     * 根据 ID 查询商品详情
     */
    GoodsVo selectGoodsById(Long goodsId);

    /**
     * 根据分类 ID 分页查询商品
     */
    List<GoodsVo> selectGoodsByTypeId(Integer page, Integer limit, Long typeId);
}
