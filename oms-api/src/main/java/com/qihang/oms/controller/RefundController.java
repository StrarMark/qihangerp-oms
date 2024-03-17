package com.qihang.oms.controller;


import com.qihang.common.common.AjaxResult;
import com.qihang.common.common.TableDataInfo;
import com.qihang.oms.common.BaseController;
import com.qihang.oms.domain.ORefund;
import com.qihang.oms.service.ORefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 退换货Controller
 * 
 * @author qihang
 * @date 2024-01-13
 */
@RestController
@RequestMapping("/refund")
public class RefundController extends BaseController
{
    @Autowired
    private ORefundService refundService;

    /**
     * 查询退换货列表
     */
    @PreAuthorize("@ss.hasPermi('api:returned:list')")
    @GetMapping("/list")
    public TableDataInfo list(ORefund refund)
    {
        List<ORefund> list = refundService.selectList(refund);
        return getDataTable(list);
    }


    /**
     * 获取退换货详细信息
     */
    @PreAuthorize("@ss.hasPermi('api:returned:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(refundService.selectById(id));
    }
    /**
     * 修改退换货
     */
    @PreAuthorize("@ss.hasPermi('api:returned:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody ORefund refund)
    {
        //erpOrderReturnedService.updateErpOrderReturned(erpOrderReturned)
        return toAjax(1);
    }

}