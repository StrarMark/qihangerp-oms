package cn.qihangerp.model.request;

import lombok.Data;

import java.util.List;

@Data
public class MerchantCloudStockInApplyRequest {
    private Long vendorId;//供应商云仓id
    private String stockInNum;
//    private Integer stockInType;
//    private String sourceNo;
    private String applyMan;
    private String applyMobile;
    private String remark;
    private Long merchantId;
    private List<StockInCreateItem> itemList;
}
