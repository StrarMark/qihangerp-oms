package cn.qihangerp.model.bo;

import lombok.Data;

@Data
public class ShipStockUpCompleteBo {
    private Long stockingId;//备货单ID
    private String stockOutNum;
//    private Date completeTime;
    private Long warehouseId;
    private Long[] ids;
    private String[] orderNums;
}
