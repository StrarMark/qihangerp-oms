package cn.qihangerp.services.dou.controller;


import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.BaseController;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.module.open.dou.domain.DouOrder;
import cn.qihangerp.module.open.dou.domain.OmsDouLogisticsTemplate;
import cn.qihangerp.module.open.dou.domain.OmsDouWaybillAccount;
import cn.qihangerp.module.open.dou.service.DouOrderService;
import cn.qihangerp.module.open.dou.service.OmsDouLogisticsTemplateService;
import cn.qihangerp.module.open.dou.service.OmsDouWaybillAccountService;
import cn.qihangerp.module.order.domain.OShipWaybill;
import cn.qihangerp.module.order.service.OOrderService;
import cn.qihangerp.module.order.service.OShipWaybillService;
import cn.qihangerp.open.common.ApiResultVo;

import cn.qihangerp.sdk.dou.DouTokenApiHelper;
import cn.qihangerp.sdk.dou.DouWaybillAccountApiHelper;
import cn.qihangerp.sdk.dou.DouWaybillApiHelper;
import cn.qihangerp.sdk.dou.model.*;
import cn.qihangerp.sdk.dou.request.WaybillApplyRequest;
import cn.qihangerp.sdk.dou.request.WaybillCodeRequest;
import cn.qihangerp.services.dou.DouApiCommon;
import cn.qihangerp.services.dou.request.DouRequest;
import cn.qihangerp.services.dou.request.DouWaybillAccountUpdateRequest;
import cn.qihangerp.services.dou.request.DouWaybillGetBo;
import cn.qihangerp.services.dou.request.ShareSupplierRequest;
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
@RequestMapping("/dou/ewaybill")
public class DouWaybillController extends BaseController {
    private final DouApiCommon apiCommon;
    private final OmsDouWaybillAccountService waybillAccountService;
    private final DouOrderService orderService;
    private final OShipWaybillService erpShipWaybillService;
    private final OOrderService oOrderService;
    private final OmsDouLogisticsTemplateService logisticsTemplateService;
    @GetMapping(value = "/get_waybill_account_list")
    public AjaxResult getWaybillAccountList(Long shopId) throws Exception {
        List<OmsDouWaybillAccount> list = waybillAccountService.list(
                new LambdaQueryWrapper<OmsDouWaybillAccount>()
                        .eq(OmsDouWaybillAccount::getShopId, shopId)
                        .eq(OmsDouWaybillAccount::getIsShow, 1));
        return AjaxResult.success(list);
    }

    @RequestMapping(value = "/shareSupplier", method = RequestMethod.POST)
    public AjaxResult shareSupplier(@RequestBody ShareSupplierRequest params) throws Exception {
        if (params.getId() == null || params.getId() <= 0) return AjaxResult.error("参数错误，没有Id");
        if (params.getSupplierIds() == null || params.getSupplierIds().length == 0) return AjaxResult.error("参数错误，没有供应商");
        OmsDouWaybillAccount account = new OmsDouWaybillAccount();
        account.setId(params.getId());
        String result = Arrays.stream(params.getSupplierIds()).mapToObj(String::valueOf).collect(Collectors.joining(","));
        account.setSupplierIds(","+result+",");
        waybillAccountService.updateById(account);

        return AjaxResult.success();
    }

    @RequestMapping(value = "/updateAccount", method = RequestMethod.POST)
    public AjaxResult updateAccount(@RequestBody DouWaybillAccountUpdateRequest params) throws Exception {
        if (params.getId() == null || params.getId() <= 0) return AjaxResult.error("参数错误，没有Id");
        if (!StringUtils.hasText(params.getName())) return AjaxResult.error("缺少参数");
        if (!StringUtils.hasText(params.getMobile())) return AjaxResult.error("缺少参数");
        if (!StringUtils.hasText(params.getNetsiteName())) return AjaxResult.error("缺少参数");
        if (!StringUtils.hasText(params.getNetsiteCode())) return AjaxResult.error("缺少参数");

        OmsDouWaybillAccount account = new OmsDouWaybillAccount();
        account.setId(params.getId());
        account.setNetsiteName(params.getNetsiteName());
        account.setNetsiteCode(params.getNetsiteCode());
        account.setName(params.getName());
        account.setMobile(params.getMobile());
        account.setSellerShopId(params.getSellerShopId());
        waybillAccountService.updateById(account);

        return AjaxResult.success();
    }

        /**
         * 拉取电子面单账号
         * @param params
         * @return
         * @throws Exception
         */
    @RequestMapping(value = "/pull_waybill_account", method = RequestMethod.POST)
    public AjaxResult pullWaybillAccount(@RequestBody DouRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }

        var checkResult = apiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != ResultVoEnum.SUCCESS.getIndex()) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(), checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();
        Long sellId = checkResult.getData().getSellerId();
        ApiResultVo<Token> token = DouTokenApiHelper.getToken(appKey, appSecret,checkResult.getData().getSellerId());
        if(token.getCode()==0) {
            accessToken = token.getData().getAccessToken();
        }else{
            return AjaxResult.error(token.getMsg());
        }
        ApiResultVo<WaybillAccount> apiResultVo = DouWaybillAccountApiHelper.listWaybillAccount(appKey, appSecret, accessToken);
         List<OmsDouWaybillAccount> list  = new ArrayList<>();
        if(apiResultVo.getCode()==0) {
            for (var item : apiResultVo.getList()) {
                List<OmsDouLogisticsTemplate> logisticsCode = logisticsTemplateService.getByLogisticsCode(item.getCompany());

                if (item.getSenderAddress() != null && item.getSenderAddress().size() > 0) {

                    for(var sendAddress : item.getSenderAddress()) {
                        OmsDouWaybillAccount vo = new OmsDouWaybillAccount();
                        vo.setShopId(params.getShopId());
                        vo.setSellerId(sellId);
                        vo.setIsShow(1);
                        vo.setCompany(item.getCompany());
                        vo.setCompanyType(item.getCompanyType());
                        vo.setAmount(Integer.parseInt(item.getAmount()));
                        vo.setAllocatedQuantity(item.getAllocatedQuantity());
                        vo.setCancelledQuantity(item.getCancelledQuantity());
                        vo.setRecycledQuantity(item.getRecycledQuantity());
                        vo.setNetsiteCode(item.getNetsiteCode());
                        vo.setNetsiteName(item.getNetsiteName());
                        vo.setProvinceName(sendAddress.getProvinceName());
                        vo.setDistrictName(sendAddress.getDistrictName());
                        vo.setCityName(sendAddress.getCityName());
                        vo.setStreetName(sendAddress.getStreetName());
                        vo.setDetailAddress(sendAddress.getDetailAddress());
                        if(logisticsCode!=null&& logisticsCode.size()>0) {
                            vo.setTemplateUrl(logisticsCode.get(0).getTemplateUrl());
                        }
                        list.add(vo);
                    }
                }


                log.info("========组装dou电子面单账户信息==========");
            }
            waybillAccountService.saveAccountList(params.getShopId(),list);
        }
        return AjaxResult.success(list);
    }

    @PostMapping("/get_waybill_code")
    @ResponseBody
    public AjaxResult getWaybillCode(@RequestBody DouWaybillGetBo req) {
        if (req.getAccountId() == null || req.getAccountId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，请选择电子面单账户");
        }
        if (req.getShopId() == null || req.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        if(req.getIds()==null || req.getIds().length<=0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有选择订单");
        }
        var checkResult = apiCommon.checkBefore(req.getShopId());
        if (checkResult.getCode() != ResultVoEnum.SUCCESS.getIndex()) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(), checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();
        Long sellerShopId = checkResult.getData().getSellerId();

        ApiResultVo<Token> token = DouTokenApiHelper.getToken(appKey, appSecret,checkResult.getData().getSellerId());
        if(token.getCode()==0) {
            accessToken = token.getData().getAccessToken();
        }else{
            return AjaxResult.error(token.getMsg());
        }

        // 获取电子面单账户信息(包含了发货地址信息)
         OmsDouWaybillAccount waybillAccount = waybillAccountService.getById(req.getAccountId());

        WaybillCodeRequest request = new WaybillCodeRequest();
        request.setLogistics_code(waybillAccount.getCompany());
        request.setOrder_channel("1");

        WaybillAddressInfo sender = new WaybillAddressInfo();
        WaybillAddress address = new WaybillAddress();
        address.setCountry_code("CHN");
        address.setProvince_name(waybillAccount.getProvinceName());
        address.setCity_name(waybillAccount.getCityName());
        address.setDistrict_name(waybillAccount.getDistrictName());
        address.setStreet_name(waybillAccount.getStreetName());
        address.setDetail_address(waybillAccount.getDetailAddress());
        sender.setAddress(address);

        WaybillContact contact = new WaybillContact();
        contact.setName(waybillAccount.getName());
        contact.setMobile(waybillAccount.getMobile());

        sender.setContact(contact);

        request.setSender_info(sender);


        // 开始组装订单
        List<WaybillOrderInfo> orderInfos=new ArrayList<>();

        for(String orderId:req.getIds()){
            if(StringUtils.hasText(orderId)){
                DouOrder order = orderService.queryDetailByOrderId(orderId);
                if(order!=null) {

                    WaybillOrderInfo orderInfo = new WaybillOrderInfo();
                    orderInfo.setOrder_id(order.getOrderId());

                    WaybillAddressInfo receiver = new WaybillAddressInfo();
                    WaybillAddress address1 = new WaybillAddress();
                    address1.setCountry_code("CHN");
                    address1.setProvince_name(order.getProvinceName());
                    address1.setCity_name(order.getCityName());
                    address1.setDistrict_name(order.getTownName());
                    address1.setStreet_name(order.getStreetName());
                    address1.setDetail_address(order.getMaskPostAddress());
                    receiver.setAddress(address1);

                    WaybillContact contact1 = new WaybillContact();
                    contact1.setName(order.getMaskPostReceiver());
                    contact1.setMobile("-");
                    receiver.setContact(contact1);

                    orderInfo.setReceiver_info(receiver);

                    //
                    List<WaybillOrderItem> items=new ArrayList<>();
                    if(order.getItems()!=null&&order.getItems().size()>0) {
                        for (var it: order.getItems()) {
                            WaybillOrderItem item = new WaybillOrderItem();
                            item.setItem_count(it.getItemNum().intValue());
                            item.setItem_name(it.getProductName());
                            item.setItem_specs(it.getSpec());
                            items.add(item);
                        }
                        orderInfo.setItems(items);
                    }

                    orderInfos.add(orderInfo);
                }
            }
        }

        request.setOrder_infos(orderInfos);
        ApiResultVo<WaybillCode> apiResultVo = DouWaybillApiHelper.getWaybillCode(appKey, appSecret, token.getData().getAccessToken(), request);

        if(apiResultVo.getCode()==0){
            // 保持数据
            for(var result: apiResultVo.getList()){
                OShipWaybill waybill = new OShipWaybill();
                waybill.setShopId(req.getShopId());
                waybill.setShopType(EnumShopType.DOU.getIndex());
                waybill.setOrderId(result.getOrderId());
                waybill.setWaybillCode(result.getTrackNo());
                waybill.setLogisticsCode(result.getCompany());
//                waybill.setPrintData(result.getPrint_data());
                erpShipWaybillService.waybillUpdate(waybill);
                log.info("====保存電子面單信息========"+result.getOrderId());
//                oOrderService.saveWaybillCode(result.getOrderId(), req.getShopId(), EnumShopType.DOU.getIndex(),result.getTrackNo());
            }
        }else{
            return AjaxResult.error(apiResultVo.getMsg());
        }

        return success();
    }

@PostMapping("/get_print_data")
@ResponseBody
public AjaxResult getPrintData(@RequestBody DouWaybillGetBo req) {
    if (req.getShopId() == null || req.getShopId() <= 0) {
        return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
    }
    if (req.getIds() == null || req.getIds().length <= 0) {
        return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有选择订单");
    }
    var checkResult = apiCommon.checkBefore(req.getShopId());
    if (checkResult.getCode() != 0) {
        return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(), checkResult.getData());
    }
    String accessToken = checkResult.getData().getAccessToken();
    String appKey = checkResult.getData().getAppKey();
    String appSecret = checkResult.getData().getAppSecret();
    Long sellerShopId = checkResult.getData().getSellerId();
    ApiResultVo<Token> token = DouTokenApiHelper.getToken(appKey, appSecret,checkResult.getData().getSellerId());
    if(token.getCode()==0) {
        accessToken = token.getData().getAccessToken();
    }else{
        return AjaxResult.error(token.getMsg());
    }

    List<OShipWaybill> list = erpShipWaybillService.getListByOrderIds(req.getShopId(), req.getIds());
    WaybillApplyRequest request = new WaybillApplyRequest();
    List<WaybillApply> waybillApplyList = new ArrayList<>();
    if(list!=null && list.size()>0) {
        for (var ship:list) {
            if(!StringUtils.hasText(ship.getPrintData())) {
                WaybillApply dto = new WaybillApply();
                dto.setLogistics_code(ship.getLogisticsCode());
                dto.setTrack_no(ship.getWaybillCode());
                waybillApplyList.add(dto);
            }
        }
    }
    request.setWaybill_applies(waybillApplyList);
    ApiResultVo<WaybillPrintData> apiResultVo = DouWaybillApiHelper.pullWaybillPrintData(appKey, appSecret, accessToken, request);
    if(apiResultVo.getCode()==0){
        // 更新数据
        for (var item:apiResultVo.getList()) {
            OShipWaybill waybillNew = new OShipWaybill();
            waybillNew.setPrintData(item.getPrintData());
            waybillNew.setUpdateBy("获取打印数据");
            waybillNew.setUpdateTime(new Date());
            erpShipWaybillService.update(waybillNew,new LambdaQueryWrapper<OShipWaybill>().eq(OShipWaybill::getWaybillCode,item.getTrackNo()));
            log.info("====保存電子面單打印信息jd========"+item.getOrderId());
            OShipWaybill list1 =  list.stream().filter(x -> x.getWaybillCode().equals(item.getTrackNo())).findFirst().get();
            if(list1!=null){
                list1.setPrintData(item.getPrintData());
            }
        }

    }else{
        return AjaxResult.error(apiResultVo.getMsg());
    }
    return AjaxResult.success(list);
}

    @PostMapping("/push_print_success")
    @ResponseBody
    public AjaxResult pushPrintSuccess(@RequestBody DouWaybillGetBo req) {
        if (req.getShopId() == null || req.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        if (req.getIds() == null || req.getIds().length <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有选择订单");
        }
        erpShipWaybillService.printSuccess(req.getShopId(), req.getIds());
        return AjaxResult.success();
    }

    /**
     * 发货
     * @param req
     * @return
     */
    @PostMapping("/push_ship_send")
    @ResponseBody
    public AjaxResult pushShipSend(@RequestBody DouWaybillGetBo req) {
        if (req.getShopId() == null || req.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        if (req.getIds() == null || req.getIds().length <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有选择订单");
        }
        erpShipWaybillService.pushShipSend(req.getShopId(), req.getIds());

        return AjaxResult.success();
    }
}
