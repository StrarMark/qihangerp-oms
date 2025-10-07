package cn.qihangerp.module.order.domain.bo;

import lombok.Data;

@Data
public class OrderShipBo {
    private Integer shipType;
    private Long shopId;
    private String orderNum;
    private String shipCompany;
    private String shipCode;
    private String receiverName;
    private String receiverMobile;
    private String province;
    private String city;
    private String town;
    private String address;
    private String shipOperator;
    private String[] itemIds;

}
