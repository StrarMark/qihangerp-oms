package cn.qihangerp.open.request;

import cn.qihangerp.common.PageQuery;
import lombok.Data;

/**
 * 商品SKU
 *
 */

@Data
public class WarehouseGoodsQueryRequest extends PageQuery {

    // 仓库id
    private Long warehouseId;
    // 商品id
    private Long goodsId;

    private static final long serialVersionUID = 1L;
}