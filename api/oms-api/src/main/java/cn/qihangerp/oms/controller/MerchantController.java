package cn.qihangerp.oms.controller;

import cn.qihangerp.common.*;
import cn.qihangerp.model.bo.MerchantAddBo;
import cn.qihangerp.model.entity.ErpMerchant;
import cn.qihangerp.model.query.MerchantQuery;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.ErpMerchantService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@AllArgsConstructor
@RestController
@RequestMapping("/merchant")
public class MerchantController extends BaseController {
    private final ErpMerchantService merchantService;


    @GetMapping("/list")
    public TableDataInfo list(MerchantQuery bo, PageQuery pageQuery) {
        var list = merchantService.list(new LambdaQueryWrapper<ErpMerchant>().eq(ErpMerchant::getStatus, 0));
        ErpMerchant self = new ErpMerchant();
        self.setId(0L);
        self.setName("总部自营");
        list.add(0,self);
        return getDataTable(list);
    }

    @GetMapping("/pageList")
    public TableDataInfo pageList(MerchantQuery bo, PageQuery pageQuery) {
        // 管理员
        PageResult<ErpMerchant> pageResult = merchantService.queryPageList(bo, pageQuery);
        ErpMerchant self = new ErpMerchant();
        self.setId(0L);
        self.setName("总部自营");
        if (pageResult.getRecords() != null) {
            if (pageResult.getRecords().size() == 0) {
                pageResult.setRecords(new ArrayList<ErpMerchant>());
            }
            pageResult.getRecords().add(0, self);

        } else {
            pageResult.setRecords(new ArrayList<ErpMerchant>());
            pageResult.getRecords().add(0, self);
        }
        return getDataTable(pageResult);
    }

    /**
     * 全部商户（仅总部查询）
     * @return
     */
    @GetMapping("/list_all")
    public AjaxResult listAll() {
        var list = merchantService.list(new LambdaQueryWrapper<ErpMerchant>().eq(ErpMerchant::getStatus, 0));
        return AjaxResult.success(list);
    }


    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(merchantService.getById(id));
    }


    @PostMapping("/add")
    public AjaxResult add(@RequestBody MerchantAddBo bo) {
        ResultVo<ErpMerchant> resultVo = merchantService.add(bo, getUsername());
        if (resultVo.getCode() == 0) return AjaxResult.success();
        else return AjaxResult.error(resultVo.getMsg());
    }

    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody ErpMerchant bo)
    {
        if(bo.getId()==null) return AjaxResult.error("缺少参数：id");
        bo.setUpdateBy(getUsername());
        bo.setUpdateTime(new Date());
        merchantService.updateById(bo);
        return toAjax(1);
    }

    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        if(ids == null) return AjaxResult.error("缺少参数：id");
        merchantService.removeBatchByIds(Arrays.stream(ids).toList());
        return AjaxResult.success();
    }
}
