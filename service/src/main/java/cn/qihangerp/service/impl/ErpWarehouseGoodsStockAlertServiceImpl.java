package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.mapper.ErpWarehouseGoodsStockAlertMapper;
import cn.qihangerp.model.entity.ErpWarehouseGoods;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStock;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStockAlert;
import cn.qihangerp.service.ErpWarehouseGoodsService;
import cn.qihangerp.service.ErpWarehouseGoodsStockAlertService;
import cn.qihangerp.service.ErpWarehouseGoodsStockService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
public class ErpWarehouseGoodsStockAlertServiceImpl extends ServiceImpl<ErpWarehouseGoodsStockAlertMapper, ErpWarehouseGoodsStockAlert>
    implements ErpWarehouseGoodsStockAlertService {

    private final ErpWarehouseGoodsService warehouseGoodsService;
    private final ErpWarehouseGoodsStockService goodsStockService;

    @Override
    public PageResult<ErpWarehouseGoodsStockAlert> queryPageList(ErpWarehouseGoodsStockAlert bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseGoodsStockAlert> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoodsStockAlert>()
                .eq(bo.getWarehouseId() != null, ErpWarehouseGoodsStockAlert::getWarehouseId, bo.getWarehouseId())
                .eq(bo.getGoodsId() != null, ErpWarehouseGoodsStockAlert::getGoodsId, bo.getGoodsId())
                .eq(bo.getStatus() != null, ErpWarehouseGoodsStockAlert::getStatus, bo.getStatus())
                .like(StringUtils.hasText(bo.getGoodsName()), ErpWarehouseGoodsStockAlert::getGoodsName, bo.getGoodsName())
                .orderByDesc(ErpWarehouseGoodsStockAlert::getCreateTime);

        Page<ErpWarehouseGoodsStockAlert> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public List<ErpWarehouseGoodsStockAlert> queryAlertList(Long warehouseId) {
        LambdaQueryWrapper<ErpWarehouseGoodsStockAlert> queryWrapper = new LambdaQueryWrapper<ErpWarehouseGoodsStockAlert>()
                .eq(ErpWarehouseGoodsStockAlert::getWarehouseId, warehouseId)
                .eq(ErpWarehouseGoodsStockAlert::getStatus, 1)
                .orderByDesc(ErpWarehouseGoodsStockAlert::getCreateTime);

        List<ErpWarehouseGoodsStockAlert> list = this.list(queryWrapper);

        for (ErpWarehouseGoodsStockAlert alert : list) {
            ErpWarehouseGoodsStock stock = goodsStockService.getOne(
                    new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                            .eq(ErpWarehouseGoodsStock::getWarehouseId, warehouseId)
                            .eq(ErpWarehouseGoodsStock::getGoodsId, alert.getGoodsId())
            );
            if (stock != null) {
                alert.setCurrentQty(stock.getTotalNum());
            } else {
                alert.setCurrentQty(0);
            }
        }
        return list;
    }

    @Override
    @Transactional
    public ResultVo<Long> saveAlert(Long warehouseId, Long goodsId, Integer alertQty, String userName) {
        if (warehouseId == null) return ResultVo.error(ResultVoEnum.ParamsError, "仓库ID不能为空");
        if (goodsId == null) return ResultVo.error(ResultVoEnum.ParamsError, "商品ID不能为空");
        if (alertQty == null || alertQty < 0) return ResultVo.error(ResultVoEnum.ParamsError, "预警数量不能为空且不能为负数");

        ErpWarehouseGoods goods = warehouseGoodsService.getById(goodsId);
        if (goods == null) return ResultVo.error("商品不存在");

        ErpWarehouseGoodsStockAlert existAlert = this.getOne(
                new LambdaQueryWrapper<ErpWarehouseGoodsStockAlert>()
                        .eq(ErpWarehouseGoodsStockAlert::getWarehouseId, warehouseId)
                        .eq(ErpWarehouseGoodsStockAlert::getGoodsId, goodsId)
        );
        if (existAlert != null) {
            return ResultVo.error("该商品已设置预警，请修改已有记录");
        }

        ErpWarehouseGoodsStockAlert alert = new ErpWarehouseGoodsStockAlert();
        alert.setWarehouseId(warehouseId);
        alert.setGoodsId(goodsId);
        alert.setGoodsName(goods.getGoodsName());
        alert.setSkuId(goods.getErpGoodsSkuId());
        alert.setSkuCode(goods.getErpGoodsNo());
        alert.setSkuName(goods.getStandard());
        alert.setAlertQty(alertQty);
        alert.setStatus(1);
        alert.setCreateBy(userName);
        alert.setCreateTime(new Date());

        this.save(alert);
        return ResultVo.success(alert.getId());
    }

    @Override
    @Transactional
    public ResultVo<Long> updateAlert(Long id, Integer alertQty, String userName) {
        if (id == null) return ResultVo.error(ResultVoEnum.ParamsError, "ID不能为空");
        if (alertQty == null || alertQty < 0) return ResultVo.error(ResultVoEnum.ParamsError, "预警数量不能为空且不能为负数");

        ErpWarehouseGoodsStockAlert alert = this.getById(id);
        if (alert == null) return ResultVo.error("记录不存在");

        alert.setAlertQty(alertQty);
        alert.setUpdateBy(userName);
        alert.setUpdateTime(new Date());

        this.updateById(alert);
        return ResultVo.success(alert.getId());
    }

    @Override
    @Transactional
    public ResultVo<Long> deleteAlert(Long id) {
        if (id == null) return ResultVo.error(ResultVoEnum.ParamsError, "ID不能为空");

        ErpWarehouseGoodsStockAlert alert = this.getById(id);
        if (alert == null) return ResultVo.error("记录不存在");

        this.removeById(id);
        return ResultVo.success();
    }

    @Override
    @Transactional
    public ResultVo<Long> setStatus(Long id, Integer status) {
        if (id == null) return ResultVo.error(ResultVoEnum.ParamsError, "ID不能为空");
        if (status == null || (status != 0 && status != 1)) return ResultVo.error(ResultVoEnum.ParamsError, "状态值不正确");

        ErpWarehouseGoodsStockAlert alert = this.getById(id);
        if (alert == null) return ResultVo.error("记录不存在");

        alert.setStatus(status);
        alert.setUpdateTime(new Date());

        this.updateById(alert);
        return ResultVo.success();
    }
}
