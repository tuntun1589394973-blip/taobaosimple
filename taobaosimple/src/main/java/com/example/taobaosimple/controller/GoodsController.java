package com.example.taobaosimple.controller;

import com.example.taobaosimple.common.resp.RestResp;
import com.example.taobaosimple.dao.goods.Goods;
import com.example.taobaosimple.service.IGoodsService;
import com.example.taobaosimple.vo.GoodsVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品模块控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/goods")
public class GoodsController {

    private final IGoodsService goodsService;

    public GoodsController(IGoodsService goodsService) {
        this.goodsService = goodsService;
    }

    /**
     * 根据名称搜索商品
     */
    @PostMapping("selectGoodsByName")
    public RestResp<List<Goods>> selectGoodsByName(@RequestParam String name) {
        return RestResp.success(goodsService.selectGoodsByName(name));
    }

    /**
     * 获取随机推荐商品
     */
    @PostMapping("selectRandomGoods")
    public RestResp<List<GoodsVo>> selectRandomGoods(
            @RequestParam Integer limit,
            @RequestParam(required = false) List<Long> ids) {
        return RestResp.success(goodsService.selectRandomGoods(limit, ids));
    }

    /**
     * 根据 ID 查询商品详情
     */
    @PostMapping("selectGoodsById")
    public RestResp<GoodsVo> selectGoodsById(@RequestParam Long id) {
        return RestResp.success(goodsService.selectGoodsById(id));
    }

    /**
     * 根据分类 ID 分页查询商品
     */
    @PostMapping("selectGoodsByTypeId")
    public RestResp<List<GoodsVo>> selectGoodsByTypeId(
            @RequestParam Long typeId,
            @RequestParam Integer page,
            @RequestParam Integer limit) {
        return RestResp.success(goodsService.selectGoodsByTypeId(page, limit, typeId));
    }
}
