package cn.qihangerp.model.vo;

import cn.qihangerp.utils.poi.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 供应商发货订单
 * @TableName o_supplier_ship_order
 */

@Data
public class CloudWarehouseShipOrderExcelVo implements Serializable {
    /**
     * 
     */
    private Long id;


    /**
     * 订单编号（第三方平台订单号）
     */
    @Excel(name = "订单号",sort = 1)
    private String orderNum;

    /**
     * 快递单号
     */
    @Excel(name = "快递单号",sort = 4)
    private String shippingNumber;
    @Excel(name = "仓库发货单号",sort = 2)
    private String shippingOrderCode;

    /**
     * 物流公司
     */
    @Excel(name = "物流公司",sort = 3)
    private String shippingCompany;
    private String shippingCompanyCode;


    /**
     * 创建时间
     */
    @Excel(name = "推送时间",sort = 5,dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}