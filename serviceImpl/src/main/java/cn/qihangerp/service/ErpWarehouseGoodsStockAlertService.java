package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStockAlert;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ErpWarehouseGoodsStockAlertService extends IService<ErpWarehouseGoodsStockAlert> {
    PageResult<ErpWarehouseGoodsStockAlert> queryPageList(ErpWarehouseGoodsStockAlert bo, PageQuery pageQuery);

    List<ErpWarehouseGoodsStockAlert> queryAlertList(Long warehouseId);

    ResultVo<Long> saveAlert(Long warehouseId, Long goodsId, Integer alertQty, String userName);

    ResultVo<Long> updateAlert(Long id, Integer alertQty, String userName);

    ResultVo<Long> deleteAlert(Long id);

    ResultVo<Long> setStatus(Long id, Integer status);
}
