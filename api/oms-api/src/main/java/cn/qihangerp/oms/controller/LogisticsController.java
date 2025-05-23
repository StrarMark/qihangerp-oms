package cn.qihangerp.oms.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.domain.OLogisticsCompany;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.service.OLogisticsCompanyService;
import cn.qihangerp.module.service.OShopPlatformService;
import cn.qihangerp.module.service.OShopService;
import cn.qihangerp.oms.request.ShopBo;
import cn.qihangerp.security.common.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 店铺Controller
 * 
 * @author qihang
 * @date 2023-12-31
 */
@AllArgsConstructor
@RestController
@RequestMapping("/shop")
public class LogisticsController extends BaseController {
    private final OLogisticsCompanyService logisticsCompanyService;
    private final OShopService shopService;
    private final OShopPlatformService platformService;



    @GetMapping("/logistics_status")
    public TableDataInfo logisticsStatusList(Integer status, Integer shopType, Integer shopId)
    {
        if(status==null) status=1;
        if(shopType==null)shopType=0;
        return getDataTable(logisticsCompanyService.queryListByStatus(status,shopType, shopId));
    }
    /**
     * 查询店铺列表logistics
     */
    @GetMapping("/logistics")
    public TableDataInfo logisticsList(Integer type, Integer shopId, PageQuery pageQuery)
    {
        if(type==null)type=0;
        PageResult<OLogisticsCompany> result = logisticsCompanyService.queryPageList(type, shopId, pageQuery);
        return getDataTable(result);
    }

    @PostMapping("/logistics/add")
    public AjaxResult add(@RequestBody OLogisticsCompany company)
    {
        company.setPlatformId(0);
        return toAjax(logisticsCompanyService.save(company));
    }
}
