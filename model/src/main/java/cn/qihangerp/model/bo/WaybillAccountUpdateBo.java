package cn.qihangerp.model.bo;

import lombok.Data;

@Data
public class WaybillAccountUpdateBo {
    private Long id;//电子面单Id
    private String branchName;
    private String branchCode;
    private String sellerShopId;
    private String deliverName;
    private String deliverMobile;//点三使用
    private String deliverPhone;//点三使用
    private String name;//平台使用
    private String mobile;//平台使用
    /**
     * 打印模版url
     */
    private String templateUrl;
    private Long templateId;
}
