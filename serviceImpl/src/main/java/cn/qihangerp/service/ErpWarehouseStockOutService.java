package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpWarehouseStockOut;
import cn.qihangerp.model.request.StockOutItemRequest;
import cn.qihangerp.model.request.VMSStockOutCreateRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_vendor_stock_out(出库单)】的数据库操作Service
* @createDate 2025-06-14 13:26:41
*/
public interface ErpWarehouseStockOutService extends IService<ErpWarehouseStockOut> {
    PageResult<ErpWarehouseStockOut> queryPageList(ErpWarehouseStockOut bo, PageQuery pageQuery);
    ResultVo<Long> createEntry(Long ownerId, String userName, VMSStockOutCreateRequest request);

    ErpWarehouseStockOut getDetailAndItemById(Long id);

    ResultVo<Long> stockOut(Long userId, String userName, StockOutItemRequest request);
}
