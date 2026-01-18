package cn.qihangerp.oms.wei.controller;

import cn.qihangerp.common.*;
import cn.qihangerp.model.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.module.service.OGoodsSkuService;
import cn.qihangerp.model.entity.WeiGoods;
import cn.qihangerp.model.entity.WeiGoodsSku;
import cn.qihangerp.module.service.WeiGoodsService;
import cn.qihangerp.module.service.WeiGoodsSkuService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/wei/goods")
@RestController
@AllArgsConstructor
public class WeiGoodsController extends BaseController {
    private final WeiGoodsSkuService skuService;
    private final WeiGoodsService goodsService;
    private final OGoodsSkuService oGoodsSkuService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo list(WeiGoods bo, PageQuery pageQuery) {
        PageResult<WeiGoods> result = goodsService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @RequestMapping(value = "/skuList", method = RequestMethod.GET)
    public TableDataInfo skuList(WeiGoodsSku bo, PageQuery pageQuery) {
        PageResult<WeiGoodsSku> result = skuService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    /**
     *
     */
    @GetMapping(value = "/sku/{id}")
    public AjaxResult getSkuInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(skuService.getById(id));
    }
    @PostMapping(value = "/sku/linkErp")
    public AjaxResult linkErp(@RequestBody LinkErpGoodsSkuBo bo)
    {
        if(bo.getId()==null||bo.getId()==0){
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
//        WeiGoodsSku sku = new WeiGoodsSku();
//        sku.setId(bo.getId());
//        sku.setOGoodsSkuId(Long.parseLong(bo.getErpGoodsSkuId()));
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
