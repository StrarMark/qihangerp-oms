package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpWarehouseStockTake;
import cn.qihangerp.model.entity.ErpWarehouseStockTakeItem;
import cn.qihangerp.model.request.WarehouseStockTakeAddItemRequest;
import cn.qihangerp.model.request.WarehouseStockTakeCreateRequest;
import cn.qihangerp.model.request.WarehouseStockTakeSaveItemRequest;
import cn.qihangerp.model.request.WarehouseStockTakeSaveRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_warehouse_stock_take(仓库盘点表)】的数据库操作Service
* @createDate 2025-10-15 21:54:31
*/
public interface ErpWarehouseStockTakeService extends IService<ErpWarehouseStockTake> {
    PageResult<ErpWarehouseStockTake> queryPageList(ErpWarehouseStockTake bo, PageQuery pageQuery);
    ErpWarehouseStockTake getDetailAndItemById(Long id);
    ResultVo createEntry(Long userId, String userName, WarehouseStockTakeCreateRequest request);

    /***
     * 添加盘点商品
     * @param request
     * @return
     */
    ResultVo<ErpWarehouseStockTakeItem> addTakeGoods(WarehouseStockTakeSaveItemRequest request,String userName);

    /**
     * 删除盘点商品
     * @param takeItemId
     * @return
     */
    ResultVo<Long> deleteTakeGoods(Long takeItemId);

    /**
     * 保存盘点
     * @param request
     * @param userName
     * @return
     */
    ResultVo<Long> saveTake(WarehouseStockTakeSaveRequest request, String userName);
    ResultVo addItem(Long userId, String userName, WarehouseStockTakeAddItemRequest request);
    ResultVo complete(Long id,Long userId,String userName);
}
