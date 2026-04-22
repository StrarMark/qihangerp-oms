package cn.qihangerp.open.response;

import lombok.Data;

@Data
public class MerchantResponse {
    /**
     * 商户ID(tenantId)
     */
    private Long id;
    /**
     * 备注
     */
    private String remark;

    /**
     * 商户名称
     */
    private String name;

    /**
     * 商户编码
     */
    private String number;

    /**
     * 社会信用代码
     */
    private String usci;
    /**
     * 联系地址
     */
    private String address;
}
