package com.example.taobaosimple.controller;

import com.example.taobaosimple.common.resp.RestResp;
import com.example.taobaosimple.dao.goods.GoodsType;
import com.example.taobaosimple.service.IGoodsTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类模块控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/goods-types")
public class GoodsTypeController {

    private final IGoodsTypeService goodsTypeService;

    public GoodsTypeController(IGoodsTypeService goodsTypeService) {
        this.goodsTypeService = goodsTypeService;
    }

    /**
     * 查询所有商品分类
     */
    @PostMapping("getgoodstype")
    public RestResp<List<GoodsType>> getGoodsType() {
        return RestResp.success(goodsTypeService.getGoodsType());
    }
}
