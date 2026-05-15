package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;

import cn.qihangerp.model.entity.ErpStockIn;
import cn.qihangerp.model.request.StockInCreateRequest;
import cn.qihangerp.model.request.StockInItemInRequest;
import cn.qihangerp.model.request.StockInLocalRequest;
import cn.qihangerp.model.request.StockInRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【wms_stock_in(入库单)】的数据库操作Service
* @createDate 2024-09-22 16:10:08
*/
public interface ErpStockInService extends IService<ErpStockIn> {
    PageResult<ErpStockIn> queryPageList(ErpStockIn bo, PageQuery pageQuery);
    PageResult<ErpStockIn> queryCloudWarehouseStockInPageList(ErpStockIn bo, PageQuery pageQuery);
    ResultVo<Long> createEntry(Long userId, String userName, StockInCreateRequest request);
    ResultVo<Long> stockIn(Long userId, String userName, StockInRequest request);

    ResultVo<Long> StockInItemIn(Long userId, String userName, StockInItemInRequest request);

    /**
     * 本地仓 入库操作
     * @param userId
     * @param userName
     * @param request
     * @return
     */
    ResultVo<Long> localStockIn(StockInLocalRequest request, Long userId, String userName );


    ErpStockIn getDetailAndItemById(Long id);

    /**
     * 商户采购入库到云仓入库单确认并生成云仓仓库入库单
     * @param stockInId 商户入库单ID（erp_stock_in）
     * @param warehouseId 云仓仓库ID（erp_warehouse）
     * @return
     */
    ResultVo<Long> merchantStockInConfirmAndCreateCloudWarehouseStockIn(Long stockInId,Long warehouseId);
}
