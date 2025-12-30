package cn.qihangerp.erp.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.erp.domain.ErpLogistics;
import cn.qihangerp.module.erp.service.ErpLogisticsService;
import cn.qihangerp.security.common.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 商品管理Controller
 * 
 * @author qihang
 * @date 2023-12-29
 */
@AllArgsConstructor
@RestController
@RequestMapping("/erp/logistics")
public class PurchaseLogisticsController extends BaseController
{
    private final ErpLogisticsService logisticsService;
    /**
     *
     */
    @GetMapping("/list")
    public TableDataInfo list(Integer status)
    {
        List<ErpLogistics> list = logisticsService.list(new LambdaQueryWrapper<ErpLogistics>()
                .eq(status!=null,ErpLogistics::getStatus, status)
                .last(" ORDER BY id desc")
        );
        return getDataTable(list);
    }
    /**
     * 获取物流公司详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(logisticsService.getById(id));
    }

    /**
     * 修改物流公司
     */
    @PutMapping("/update")
    public AjaxResult edit(@RequestBody ErpLogistics bLogisticsCompany)
    {
        return toAjax(logisticsService.updateById(bLogisticsCompany));
    }


    @PutMapping("/updateStatus")
    public AjaxResult logisticsUpdateStatus(@RequestBody ErpLogistics company)
    {
        Integer newStatus = null;
        if(company.getStatus()==null || company.getStatus().intValue() ==0){
            newStatus = 1;
        }else{
            newStatus = 0;
        }
        return toAjax(logisticsService.updateStatus(company.getId(),newStatus));
    }
    /**
     * 新增物流公司
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ErpLogistics bLogisticsCompany)
    {

        return toAjax(logisticsService.save(bLogisticsCompany));
    }
    /**
     * 删除物流公司
     */
    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(logisticsService.removeBatchByIds(Arrays.stream(ids).toList()));
    }

}
