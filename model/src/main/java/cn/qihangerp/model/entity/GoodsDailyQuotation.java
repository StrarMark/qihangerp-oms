package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 金价表
 * @TableName erp_gold_price
 */
@TableName(value ="o_goods_daily_quotation")
@Data
public class GoodsDailyQuotation {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报价类型：0采购价；1零售价
     */
    private Integer priceType;

    /**
     * 报价日期
     */
    private String priceDate;

    /**
     * 金价(g)
     */
    private Double price1;

    /**
     * 银价(g)
     */
    private Double price2;

    /**
     * 工费
     */
    private Double price3;

    /**
     * 状态：0启用 1禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}