package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpWarehouseStockIn;
import cn.qihangerp.model.request.StockInRequest;
import cn.qihangerp.model.request.WarehouseStockInCreateRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_vendor_stock_in(入库单)】的数据库操作Service
* @createDate 2025-06-14 12:47:54
*/
public interface ErpWarehouseStockInService extends IService<ErpWarehouseStockIn> {
    PageResult<ErpWarehouseStockIn> queryPageList(ErpWarehouseStockIn bo, PageQuery pageQuery);

    /**
     * 云仓自己创建入库单
     * @param userId
     * @param userName
     * @param request
     * @return
     */
    ResultVo<Long> createEntry(Long userId, String userName, WarehouseStockInCreateRequest request);



    /**
     * 入库操作（仅云仓自己能操作）
     * @param warehouseId
     * @param userName
     * @param request
     * @return
     */
    ResultVo<Long> stockIn(Long warehouseId, String userName, StockInRequest request);
    ErpWarehouseStockIn getDetailAndItemById(Long id);

    /**
     * 手动更新库存
     * @param warehouseId
     * @param skuCode
     * @param qty
     * @param initBatch 是否初始化batch
     * @return
     */
    ResultVo updateInventory(Long warehouseId,String skuCode,Integer qty,Boolean initBatch);
}
