package com.example.taobaosimple.service.impl;

import com.example.taobaosimple.common.constant.ErrorCodeEnum;
import com.example.taobaosimple.common.exception.BusinessException;
import com.example.taobaosimple.dao.goods.Goods;
import com.example.taobaosimple.mapper.GoodsMapper;
import com.example.taobaosimple.service.IGoodsService;
import com.example.taobaosimple.vo.GoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    private final GoodsMapper goodsMapper;

    public GoodsServiceImpl(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    /**
     * 根据名称搜索商品
     */
    @Override
    public List<Goods> selectGoodsByName(String goodsName) {
        // 空值校验
        if (goodsName == null || goodsName.isBlank()) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        return goodsMapper.selectGoodsByName(goodsName);
    }

    /**
     * 随机推荐商品
     */
    @Override
    public List<GoodsVo> selectRandomGoods(Integer limit, List<Long> ids) {
        // 默认 8 条，最多 100 条
        int safeLimit = limit == null || limit < 1 ? 8 : Math.min(limit, 100);
        return goodsMapper.selectRandomGoods(safeLimit, ids);
    }

    /**
     * 根据 ID 查询商品详情
     */
    @Override
    public GoodsVo selectGoodsById(Long goodsId) {
        // 校验 ID 合法性
        if (goodsId == null || goodsId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        GoodsVo goods = goodsMapper.selectGoodsById(goodsId);
        if (goods == null) {
            throw new BusinessException(ErrorCodeEnum.GOODS_NOT_FOUND);
        }
        return goods;
    }

    /**
     * 根据分类 ID 分页查询
     */
    @Override
    public List<GoodsVo> selectGoodsByTypeId(Integer page, Integer limit, Long typeId) {
        // 分类 ID 校验
        if (typeId == null || typeId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAM_INVALID);
        }
        // 计算安全的分页参数
        int safePage = page == null || page < 1 ? 1 : page;
        int safeLimit = limit == null || limit < 1
                ? 8
                : Math.min(limit, 100);
        int offset = (safePage - 1) * safeLimit;

        return goodsMapper.selectGoodsByTypeId(offset, safeLimit, typeId);
    }
}
