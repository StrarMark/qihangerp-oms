package cn.qihangerp.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName
 */

@Data
public class MerchantWarehouseVo implements Serializable {
    /**
     * 
     */
    private Long id;


    /**
     * 类型（LOCAL本地仓CLOUD系统云仓JDYC京东云仓Other其他）
     */
    private String warehouseType;

    /**
     * 云仓编码
     */
    private String warehouseNo;

    /**
     * 云仓名称
     */
    private String warehouseName;
    private Integer warehouseSource;
    private String erpWarehouseName;

    /**
     * 登陆名
     */
    private String loginName;

    /**
     * 类型（1本地仓2云仓）
     */
    private Integer type;

    /**
     * status
     */
    private String status;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String county;

    /**
     * 街道
     */
    private String town;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 联系电话
     */
    private String phone;


    /**
     * 商户id
     */
    private Long merchantId;
    /**
     * 商户店铺id，0代表商户自己
     */
    private Long shopId;
    private String remark;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}