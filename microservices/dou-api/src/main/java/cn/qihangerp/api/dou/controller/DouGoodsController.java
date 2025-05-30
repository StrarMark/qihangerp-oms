package cn.qihangerp.api.dou.controller;


import cn.qihangerp.common.*;
import cn.qihangerp.domain.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.service.OGoodsSkuService;
import cn.qihangerp.module.open.dou.domain.DouGoods;
import cn.qihangerp.module.open.dou.domain.DouGoodsSku;
import cn.qihangerp.module.open.dou.domain.bo.DouGoodsBo;
import cn.qihangerp.module.open.dou.domain.vo.DouGoodsSkuListVo;
import cn.qihangerp.module.open.dou.service.DouGoodsService;
import cn.qihangerp.module.open.dou.service.DouGoodsSkuService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/dou/goods")
@RestController
@AllArgsConstructor
public class DouGoodsController extends BaseController {
    private final DouGoodsService goodsService;
    private final DouGoodsSkuService skuService;
    private final OGoodsSkuService oGoodsSkuService;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(DouGoodsBo bo, PageQuery pageQuery) {
        PageResult<DouGoods> result = goodsService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @RequestMapping(value = "/skuList", method = RequestMethod.GET)
    public TableDataInfo skuList(DouGoodsBo bo, PageQuery pageQuery) {
        PageResult<DouGoodsSku> result = skuService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    /**
     * 获取店铺订单详细信息
     */
    @GetMapping(value = "/sku/{id}")
    public AjaxResult getSkuInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(skuService.getById(id));
    }

    /**
     * 关联
     * @param bo
     * @return
     */
    @PostMapping(value = "/sku/linkErp")
    public AjaxResult linkErp(@RequestBody LinkErpGoodsSkuBo bo)
    {
        if(bo.getId()==null){
            return AjaxResult.error(500,"缺少参数Id");
        }
        if(StringUtils.isBlank(bo.getErpGoodsSkuId())){
            return AjaxResult.error(500,"缺少参数oGoodsSkuId");
        }
        ResultVo resultVo = skuService.linkErpGoodsSku(bo);
        if(resultVo.getCode()==0)
            return success();
        else return AjaxResult.error(resultVo.getMsg());

//        OGoodsSku oGoodsSku = oGoodsSkuService.getById(bo.getErpGoodsSkuId());
//        if(oGoodsSku == null) return AjaxResult.error(1500,"未找到系统商品sku");
//        DouGoodsSku sku = new DouGoodsSku();
//        sku.setId(bo.getId());
//        sku.setOGoodsSkuId(bo.getErpGoodsSkuId());
//        skuService.updateById(sku);
//        return success();
    }

    /**
     * 推送商品到OMS
     * @param ids
     * @return
     */
    @PostMapping("/push_oms")
    @ResponseBody
    public AjaxResult pushOms(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) return AjaxResult.error("缺少参数");
        int success = 0;
        int isExist = 0;
        int fail = 0;
        for (String id : ids) {
            ResultVo resultVo = goodsService.pushToOms(Long.parseLong(id));
            if(resultVo.getCode()==0) success++;
            else if(resultVo.getCode()==ResultVoEnum.DataExist.getIndex()) isExist++;
            else fail++;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("isExist", isExist);
        map.put("fail", fail);
        map.put("total", success + isExist+fail);
        return success(map);
    }

}
