package cn.qihangerp.api.tao.controller;

import cn.qihangerp.api.tao.TaoApiCommon;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.security.common.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log
@AllArgsConstructor
@RestController
@RequestMapping("/tao/ewaybill")
public class TaoWaybillController extends BaseController {
    private final TaoApiCommon taoApiCommon;

    @GetMapping(value = "/get_waybill_account_list")
    public AjaxResult getWaybillAccountList(Long shopId) throws Exception {

        return AjaxResult.error("开源版本不支持电子面单取号功能");
    }

//    @RequestMapping(value = "/shareSupplier", method = RequestMethod.POST)
//    public AjaxResult shareSupplier(@RequestBody ShareSupplierRequest params) throws Exception {
//        if (params.getId() == null || params.getId() <= 0) return AjaxResult.error("参数错误，没有Id");
//        if (params.getSupplierIds() == null || params.getSupplierIds().length == 0) return AjaxResult.error("参数错误，没有供应商");
//        OmsDouWaybillAccount account = new OmsDouWaybillAccount();
//        account.setId(params.getId());
//        String result = Arrays.stream(params.getSupplierIds()).mapToObj(String::valueOf).collect(Collectors.joining(","));
//        account.setSupplierIds(","+result+",");
//        waybillAccountService.updateById(account);
//
//        return AjaxResult.success();
//    }
//
//    @RequestMapping(value = "/updateAccount", method = RequestMethod.POST)
//    public AjaxResult updateAccount(@RequestBody DouWaybillAccountUpdateRequest params) throws Exception {
//        if (params.getId() == null || params.getId() <= 0) return AjaxResult.error("参数错误，没有Id");
//        if (!StringUtils.hasText(params.getName())) return AjaxResult.error("缺少参数");
//        if (!StringUtils.hasText(params.getMobile())) return AjaxResult.error("缺少参数");
//        if (!StringUtils.hasText(params.getNetsiteName())) return AjaxResult.error("缺少参数");
//        if (!StringUtils.hasText(params.getNetsiteCode())) return AjaxResult.error("缺少参数");
//
//        OmsDouWaybillAccount account = new OmsDouWaybillAccount();
//        account.setId(params.getId());
//        account.setNetsiteName(params.getNetsiteName());
//        account.setNetsiteCode(params.getNetsiteCode());
//        account.setName(params.getName());
//        account.setMobile(params.getMobile());
//        account.setSellerShopId(params.getSellerShopId());
//        waybillAccountService.updateById(account);
//
//        return AjaxResult.success();
//    }

    /**
     * 拉取电子面单账号
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_waybill_account", method = RequestMethod.POST)
    public AjaxResult pullWaybillAccount() throws Exception {
        return AjaxResult.error("开源版本不支持电子面单功能");
    }

    @PostMapping("/get_waybill_code")
    @ResponseBody
    public AjaxResult getWaybillCode() {
        return AjaxResult.error("开源版本不支持电子面单功能");
    }

    @PostMapping("/get_print_data")
    @ResponseBody
    public AjaxResult getPrintData() {
        return AjaxResult.error("开源版本不支持电子面单功能");
    }

    @PostMapping("/push_print_success")
    @ResponseBody
    public AjaxResult pushPrintSuccess() {

        return AjaxResult.success();
    }

    /**
     * 发货
     *
     * @param
     * @return
     */
    @PostMapping("/push_ship_send")
    @ResponseBody
    public AjaxResult pushShipSend() {
        return AjaxResult.error("开源版本不支持电子面单发货功能");
    }
}
