package cn.qihangerp.model.request;

import cn.qihangerp.model.entity.ErpWarehouseStockTakeItem;
import lombok.Data;

import java.util.List;

@Data
public class WarehouseStockTakeSaveRequest {
    private Long id;//盘点ID
    private List<ErpWarehouseStockTakeItem> itemList;//盘点明细

}
