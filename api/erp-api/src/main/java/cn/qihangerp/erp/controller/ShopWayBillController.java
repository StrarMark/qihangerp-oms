package cn.qihangerp.erp.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/ewaybill")
@RestController
@AllArgsConstructor
public class ShopWayBillController extends BaseController {

    /**
     * 获取电子面单账户
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/get_waybill_account_list", method = RequestMethod.GET)
    public AjaxResult getWaybillAccountList() throws Exception {
        return AjaxResult.error("开源版本不支持电子面单相关功能");
    }

    /**
     * 拉取电子面单账户
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_waybill_account", method = RequestMethod.POST)
    public AjaxResult pull_waybill_account() throws Exception {
        return AjaxResult.error("开源版本不支持电子面单相关功能");
    }

    @RequestMapping(value = "/updateAccount", method = RequestMethod.POST)
    public AjaxResult updateAccount(  ) throws Exception {
        return AjaxResult.error("开源版本不支持电子面单相关功能");
    }

}
