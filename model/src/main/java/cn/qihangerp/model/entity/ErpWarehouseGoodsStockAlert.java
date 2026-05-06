package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 仓库商品库存预警设置表
 * @TableName erp_warehouse_goods_stock_alert
 */
@TableName(value = "erp_warehouse_goods_stock_alert")
@Data
public class ErpWarehouseGoodsStockAlert implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库商品ID(erp_warehouse_goods.id)
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * SKU规格
     */
    private String skuName;

    /**
     * 预警数量
     */
    private Integer alertQty;

    /**
     * 当前库存数量
     */
    private Integer currentQty;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}
