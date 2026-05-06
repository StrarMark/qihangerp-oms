package cn.qihangerp.model.vo;

import lombok.Data;

/**
 * 分配供应商发货结果
 */
@Data
public class PushOrderToShipperResult {
    private int total;
    private int success;
    private int fail;
    private int exist;
}
