package cn.qihangerp.model.request;

import lombok.Data;

import java.util.List;

@Data
public class VMSStockOutCreateRequest {
    private Long shopId;
    private Long shopGroupId;
    private Long warehouseId;
    private String outNum;
    private Integer type;
    private String sourceNo;
    private String operator;
    private String remark;
    private Long merchantId;
    private Long vendorId;
    private List<VMSStockOutItem> itemList;
    @Data
    public static class VMSStockOutItem{
        private Long id;
        private Integer quantity;
    }
}
