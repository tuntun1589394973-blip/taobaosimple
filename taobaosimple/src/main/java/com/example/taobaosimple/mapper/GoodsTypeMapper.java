package com.example.taobaosimple.mapper;

import com.example.taobaosimple.dao.goods.GoodsType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsTypeMapper {

    List<GoodsType> selectAll();

}
