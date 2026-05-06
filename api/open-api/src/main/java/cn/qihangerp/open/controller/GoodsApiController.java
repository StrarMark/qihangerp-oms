package cn.qihangerp.open.controller;

import cn.qihangerp.common.*;
import cn.qihangerp.model.entity.OGoods;
import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.model.query.GoodsQuery;
import cn.qihangerp.model.query.GoodsSkuQuery;
import cn.qihangerp.open.common.BaseController;
import cn.qihangerp.open.common.annotation.BusinessType;
import cn.qihangerp.open.common.annotation.OpenRequestLogger;
import cn.qihangerp.open.request.GoodsQueryRequest;
import cn.qihangerp.open.request.GoodsSkuQueryRequest;
import cn.qihangerp.open.response.GoodsResponse;
import cn.qihangerp.open.response.GoodsSkuResponse;
import cn.qihangerp.service.OGoodsService;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品相关openApi
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class GoodsApiController extends BaseController {
    private final OGoodsService oGoodsService;


    @OpenRequestLogger(title = "查询商品库商品SKU", businessType = BusinessType.SELECT)
    @PostMapping("/goods/sku_list")
    public TableDataInfo skuList(@RequestBody GoodsSkuQueryRequest request)
    {
        log.info("=====查询商品库商品SKU====={}", JSONObject.toJSONString(request));
        if(request.getIds()!=null && !request.getIds().isEmpty()){
            List<OGoodsSku> oGoodsSkus = oGoodsService.querySkuByIds(request.getIds());
            return getDataTable(oGoodsSkus);
        }

        if(request.getGoodsId()!=null && request.getGoodsId()>0){
            List<OGoodsSku> oGoodsSkus = oGoodsService.querySkuByGoodsId(request.getGoodsId());
            return getDataTable(oGoodsSkus);
        }


        if(request.getMerchantId()==null){
            return getDataTableError("MerchantId不能为空");
        }

        OGoodsSku bo = new OGoodsSku();
        BeanUtils.copyProperties(request,bo);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(request.getPageNum());
        pageQuery.setPageSize(request.getPageSize());
        pageQuery.setIsAsc(request.getIsAsc());
        pageQuery.setOrderByColumn(request.getOrderByColumn());

        GoodsSkuQuery  boQuery = new GoodsSkuQuery();
        BeanUtils.copyProperties(bo,boQuery);

        var pageResult = oGoodsService.querySkuPageList(boQuery,pageQuery);

        PageResult<GoodsSkuResponse> response = new PageResult<>();
        BeanUtils.copyProperties(pageResult,response);
        List<GoodsSkuResponse> list = new ArrayList<>();
        if(pageResult!=null&&pageResult.getRecords()!=null){
            for (var item : pageResult.getRecords()){
                GoodsSkuResponse res = new GoodsSkuResponse();
                BeanUtils.copyProperties(item,res);
                list.add(res);
            }
            response.setRecords(list);
        }

        return getDataTable(response);
    }

    /**
     * 查询商品列表
     */
    @OpenRequestLogger(title = "查询商品库商品", businessType = BusinessType.SELECT)
    @PostMapping("/goods/list")
    public TableDataInfo list(@RequestBody GoodsQueryRequest request)
    {
        log.info("=======查询商品库商品========");
        OGoods bo = new OGoods();
        BeanUtils.copyProperties(request,bo);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(request.getPageNum());
        pageQuery.setPageSize(request.getPageSize());
        pageQuery.setIsAsc(request.getIsAsc());
        pageQuery.setOrderByColumn(request.getOrderByColumn());

        GoodsQuery q = new GoodsQuery();
        BeanUtils.copyProperties(request,q);

        PageResult<OGoods> pageResult = oGoodsService.queryPageList(q, pageQuery);

        PageResult<GoodsResponse> response = new PageResult<>();
        BeanUtils.copyProperties(pageQuery,response);
        List<GoodsResponse> list = new ArrayList<>();
        if(pageResult!=null&&pageResult.getRecords()!=null){
            for (var item : pageResult.getRecords()){
                GoodsResponse res = new GoodsResponse();
                BeanUtils.copyProperties(item,res);
                if(item.getSkuList()!=null) {
                    List<GoodsSkuResponse> skuList = new ArrayList<>();
                    for(var sku:item.getSkuList()){
                        GoodsSkuResponse res1 = new GoodsSkuResponse();
                        BeanUtils.copyProperties(sku,res1);
                        skuList.add(res1);
                    }
                    res.setSkuList(skuList);
                }
                list.add(res);
            }
            response.setRecords(list);
        }

        return getDataTable(response);
    }


}
