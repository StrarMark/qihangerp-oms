package cn.qihangerp.erp.controller.open;

import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.security.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 商品相关openApi
 */
@Slf4j
@RestController
@RequestMapping("/api/open-api/v1")
public class GoodsApiController extends BaseController {


    @PostMapping("/goods/sku_list")
    public TableDataInfo skuList()
    {
        log.info("=====查询商品库商品SKU=====");
        return getDataTableError("开源版本暂不支持商品查询功能");
    }

    @PostMapping("/goods/list")
    public TableDataInfo list()
    {
        log.info("=====查询商品库商品列表=====");
        return getDataTableError("开源版本暂不支持商品查询功能");
    }


    /**
     * 响应请求分页数据
     */
    protected TableDataInfo getDataTableError(String message)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(500);
        rspData.setMsg(message);
        rspData.setRows(new ArrayList<>());
        rspData.setTotal(0);
        return rspData;
    }

}
