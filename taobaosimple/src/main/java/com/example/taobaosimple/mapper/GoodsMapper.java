package com.example.taobaosimple.mapper;

import com.example.taobaosimple.dao.goods.Goods;
import com.example.taobaosimple.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsMapper {
    boolean insertGoods(Goods goods);
    boolean updateGoods(Goods goods);
    boolean deleteGoods(Goods goods);
    GoodsVo selectGoodsById(Long goodsId);
    List<GoodsVo> selectGoodsByTypeId(@Param("page") Integer page,@Param("limit") Integer limit,@Param("typeId") Long typeId);
    List<Goods> selectAllGoods();
    List<Goods> selectGoodsByName(String name);
    List<GoodsVo> selectRandomGoods(@Param("limit") Integer limit,
                                    @Param("ids") List<Long> ids);

}
