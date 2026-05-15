package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.enums.EnumShipStatus;
import cn.qihangerp.enums.EnumShipType;
import cn.qihangerp.enums.EnumStockInType;
import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.AfterSalesExchangeConfirmRequest;
import cn.qihangerp.model.request.AfterSalesReturnedAndStockInRequest;
import cn.qihangerp.model.request.AfterSalesShipAgainConfirmRequest;
import cn.qihangerp.mapper.OOrderStockingMapper;
import cn.qihangerp.mapper.ORefundAfterSaleMapper;
import cn.qihangerp.service.*;
import cn.qihangerp.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.Date;

/**
* @author qilip
* @description 针对表【o_after_sale(OMS售后处理表)】的数据库操作Service实现
* @createDate 2024-09-15 21:30:30
*/
@Slf4j
@AllArgsConstructor
@Service
public class ORefundAfterSaleServiceImpl extends ServiceImpl<ORefundAfterSaleMapper, ORefundAfterSale>
    implements ORefundAfterSaleService {
    private final ORefundAfterSaleMapper mapper;
    private final OOrderService orderService;
    private final OOrderStockingMapper shipOrderMapper;
    private final OOrderStockingItemService shipOrderItemService;
    private final ErpStockInService erpStockInService;
    private final ErpStockInItemService erpStockInItemService;
    private final ErpWarehouseService erpWarehouseService;
    private final OGoodsSkuService oGoodsSkuService;
    @Override
    public PageResult<ORefundAfterSale> queryPageList(ORefundAfterSale bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ORefundAfterSale> queryWrapper = new LambdaQueryWrapper<ORefundAfterSale>()
        .eq( bo.getStatus()!=null, ORefundAfterSale::getStatus, bo.getStatus())
        .eq( bo.getSupplierId()!=null, ORefundAfterSale::getSupplierId, bo.getSupplierId())
                .eq( bo.getType()!=null, ORefundAfterSale::getType, bo.getType())
                .eq(StringUtils.isNotBlank(bo.getRefundNum()), ORefundAfterSale::getRefundNum, bo.getRefundNum())
                .eq(StringUtils.isNotBlank(bo.getOrderNum()), ORefundAfterSale::getOrderNum, bo.getOrderNum())
                .eq(StringUtils.isNotBlank(bo.getSkuCode()), ORefundAfterSale::getSkuCode, bo.getSkuCode())
                .eq(bo.getMerchantId() != null, ORefundAfterSale::getMerchantId, bo.getMerchantId())
                .eq(bo.getShopId() != null, ORefundAfterSale::getShopId, bo.getShopId());

        Page<ORefundAfterSale> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    /**
     * 退货确认
     * @param request
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> returnedConfirmAndStockIn(AfterSalesReturnedAndStockInRequest request, Long userId) {
        log.info("==========开始执行退货确认=========");
        ORefundAfterSale oRefundAfterSale = mapper.selectById(request.getId());
        if (oRefundAfterSale == null) {
            return ResultVo.error("数据错误：不存在的售后处理数据");
        }
        if(oRefundAfterSale.getSendShipType()==null) return ResultVo.error("数据错误：售后没有发货方式数据");

        if(oRefundAfterSale.getSendShipType().intValue() == EnumShipType.SUPPLIER.getIndex()){
            log.info("==========供应商发货退货========");
            // 退供应商
            // 更新自己
            ORefundAfterSale update = new ORefundAfterSale();
            update.setId(oRefundAfterSale.getId());
            update.setReturnType(300);//退回类型（0退回仓库；300退回供应商）
            update.setReturnWarehouseId(oRefundAfterSale.getSendWarehouseId());
            update.setReturnWarehouseName(oRefundAfterSale.getSendWarehouseName());
            update.setReissueType(-1);
            update.setReissueWarehouseId(-1L);
            update.setUpdateBy("确认退供应商："+userId);
            update.setResult("退供应商");
            update.setUpdateTime(new Date());
            update.setStatus(10);
            mapper.updateById(update);
            return ResultVo.success();
        }else if(oRefundAfterSale.getSendShipType().intValue() == EnumShipType.LOCAL.getIndex()
            || oRefundAfterSale.getSendShipType().intValue() == EnumShipType.CLOUD_WAREHOUSE.getIndex()
            ||oRefundAfterSale.getSendShipType().intValue() == EnumShipType.JD_CLOUD_WAREHOUSE.getIndex()
        ) {
            if(oRefundAfterSale.getSendShipType().intValue() == EnumShipType.LOCAL.getIndex()) {
                log.info("==========本地仓发货退货========");
            }else{
                log.info("==========云仓发货退货========");
            }
//            ErpWarehouse warehouse = erpWarehouseService.getById(request.getReturnWarehouseId());
//            if (warehouse == null) {
//                return ResultVo.error("仓库不存在");
//            }
            ErpWarehouse warehouse = erpWarehouseService.getById(oRefundAfterSale.getSendWarehouseId());
            if (warehouse == null) {
                return ResultVo.error("售后发货仓库不存在!请检查数据！");
            }
            //添加入库主表信息
            ErpStockIn stockIn = new ErpStockIn();
            stockIn.setMerchantId(oRefundAfterSale.getMerchantId());
            stockIn.setStockInNum("THRK-" + DateUtils.parseDateToStr("yyyyMMdd", new Date()) + "-" + System.currentTimeMillis() / 1000);
            stockIn.setStockInType(EnumStockInType.RET_STOCK_IN.getIndex());
            stockIn.setStockInOperator("");
            stockIn.setStockInOperatorId("0");
//        insert.setStockInTime(new Date());
            stockIn.setSourceNo(oRefundAfterSale.getRefundNum());
            stockIn.setRemark(request.getRemark());
            stockIn.setCreateBy("退货确认" + userId);
            stockIn.setCreateTime(new Date());
            stockIn.setSourceGoodsUnit(1);
            stockIn.setSourceSpecUnit(1);
            stockIn.setSourceSpecUnitTotal(oRefundAfterSale.getQuantity());
            stockIn.setStatus(0);//状态（0待入库1部分入库2全部入库）
//            stockIn.setWarehouseId(warehouse.getId());
//            stockIn.setWarehouseNo(warehouse.getWarehouseNo());
//            stockIn.setWarehouseName(warehouse.getWarehouseName());
//            stockIn.setWarehouseType(warehouse.getWarehouseType());
            stockIn.setWarehouseId(warehouse.getId());
            stockIn.setWarehouseNo(warehouse.getWarehouseNo());
            stockIn.setWarehouseName(warehouse.getWarehouseName());
            stockIn.setWarehouseType(warehouse.getWarehouseType());

            erpStockInService.save(stockIn);
            //添加子表信息

            OGoodsSku goodsSku = oGoodsSkuService.getById(oRefundAfterSale.getOGoodsSkuId());
            if (goodsSku != null) {
//            OGoods goods = goodsService.getById(goodsSku.getGoodsId());
                ErpStockInItem inItem = new ErpStockInItem();
                inItem.setMerchantId(stockIn.getMerchantId());
                inItem.setStockInId(stockIn.getId());
                inItem.setStockInType(stockIn.getStockInType());
                inItem.setSourceNo(stockIn.getSourceNo());
                inItem.setSourceId(0L);
                inItem.setSourceItemId(0L);
                inItem.setGoodsId(goodsSku.getGoodsId());
                inItem.setGoodsName(oRefundAfterSale.getTitle());
                inItem.setGoodsImage(goodsSku.getColorImage());
                inItem.setGoodsNum(goodsSku.getGoodsNum());
                inItem.setSkuName(goodsSku.getSkuName());
                inItem.setSkuId(goodsSku.getId());
                inItem.setSkuCode(goodsSku.getSkuCode());
                inItem.setQuantity(oRefundAfterSale.getQuantity());
                inItem.setInQuantity(0);
                inItem.setPurPrice(goodsSku.getPurPrice().doubleValue());
                inItem.setStatus(0);
                inItem.setCreateBy(stockIn.getCreateBy());
                inItem.setCreateTime(new Date());
                erpStockInItemService.save(inItem);
            } else {
                log.error("=====退货生成入库单=======没有找到商品Sku信息======");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultVo.error("退货入库==没有找到商品Sku信息");
            }

            // 更新自己
            ORefundAfterSale update = new ORefundAfterSale();
            update.setId(oRefundAfterSale.getId());
            update.setReturnType(0);//退回类型（0退回仓库；300退回供应商）
            update.setReturnWarehouseId(warehouse.getId());
            update.setReturnWarehouseName(warehouse.getWarehouseName());
            update.setReturnWarehouseType(warehouse.getWarehouseType());
            update.setReissueType(-1);
            update.setReissueWarehouseId(-1L);
            update.setUpdateBy("生成退货入库单：" + userId);
            update.setUpdateTime(new Date());
            update.setResult("生成退货入库单："+stockIn.getStockInNum());
            update.setStatus(10);
            mapper.updateById(update);
            return ResultVo.success();

        } else {
            log.error("==========不支持的发货类型========");
            return ResultVo.error("不支持的发货类型！请联系管理员！");
        }
    }

    /**
     * 补发确认
     * @param request
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> shipAgainConfirm(AfterSalesShipAgainConfirmRequest request, Long userId) {
        log.info("==========开始执行补发确认=========");
        ORefundAfterSale oRefundAfterSale = mapper.selectById(request.getId());
        if (oRefundAfterSale == null) {
            return ResultVo.error("数据错误：不存在的售后处理数据");
        }
        if(oRefundAfterSale.getSendShipType()==null) return ResultVo.error("数据错误：售后没有发货方式数据");

        if (oRefundAfterSale.getSendShipType().intValue() == EnumShipType.SUPPLIER.getIndex()) {
            log.info("==========供应商补发========");
            // 供应商补发
            OOrder oOrder1 = orderService.getById(oRefundAfterSale.getOOrderId());
//            log.info("=======新增补发发货单数据=============");
            OOrderStocking shipOrder = new OOrderStocking();
            shipOrder.setOrderType(80);//订单类型0正常订单20换货订单80补发订单
            shipOrder.setMerchantId(oRefundAfterSale.getMerchantId());
            shipOrder.setOOrderId(oRefundAfterSale.getOOrderId());
            shipOrder.setShipperId(oRefundAfterSale.getSendWarehouseId());
            shipOrder.setWarehouseId(oRefundAfterSale.getSendWarehouseId());
//            shipOrder.setWarehouseType(warehouse.getType());
//            shipOrder.setWarehouseName(warehouse.getWarehouseName());
//            shipOrder.setWarehouseNo(warehouse.getWarehouseNo());
            shipOrder.setType(oRefundAfterSale.getSendShipType());
            shipOrder.setShipMode(0);
            shipOrder.setOrderNum(oRefundAfterSale.getOrderNum());
            shipOrder.setOrderTime(LocalDateTime.now());
            shipOrder.setShopType(oRefundAfterSale.getShopType());
            shipOrder.setShopId(oRefundAfterSale.getShopId());
            shipOrder.setRemark("补发订单");
            shipOrder.setBuyerMemo("");
            shipOrder.setSellerMemo("");
            shipOrder.setSendStatus(1);
            shipOrder.setCreateTime(new Date());
            shipOrder.setProvince(oOrder1 != null ? oOrder1.getProvince() : "");
            shipOrder.setCity(oOrder1 != null ? oOrder1.getCity() : "");
            shipOrder.setTown(oOrder1 != null ? oOrder1.getTown() : "");
            shipOrder.setAddress(request.getReceiverAddress());
            shipOrder.setReceiverName(request.getReceiverName());
            shipOrder.setReceiverMobile(request.getReceiverTel());
            shipOrder.setOrderStatus(1);
            shipOrderMapper.insert(shipOrder);

            // 添加发货订单item
            OOrderStockingItem shipOrderItem = new OOrderStockingItem();
            shipOrderItem.setShipOrderId(shipOrder.getId());
            shipOrderItem.setMerchantId(shipOrder.getMerchantId());
            shipOrderItem.setOOrderId(shipOrder.getOOrderId());
            shipOrderItem.setOOrderItemId(oRefundAfterSale.getOOrderItemId());
            shipOrderItem.setOrderTime(shipOrder.getOrderTime());
            shipOrderItem.setOrderNum(shipOrder.getOrderNum());
            shipOrderItem.setSubOrderNum(oRefundAfterSale.getSubOrderNum());
            shipOrderItem.setSupplierId(shipOrder.getShipperId());
            shipOrderItem.setSkuId(org.springframework.util.StringUtils.hasText(oRefundAfterSale.getSkuId())?oRefundAfterSale.getSkuId():"");
            shipOrderItem.setProductId("");
            shipOrderItem.setGoodsId(oRefundAfterSale.getOGoodsId());
            shipOrderItem.setGoodsSkuId(oRefundAfterSale.getOGoodsSkuId());
            shipOrderItem.setRefundStatus(1);
            shipOrderItem.setGoodsName(oRefundAfterSale.getTitle());
            shipOrderItem.setGoodsNum("");
            shipOrderItem.setGoodsImg(oRefundAfterSale.getImg());
            shipOrderItem.setSkuName(oRefundAfterSale.getSkuInfo());
            shipOrderItem.setSkuCode(oRefundAfterSale.getSkuCode());
            shipOrderItem.setSendStatus(EnumShipStatus.NOT.getIndex());
            shipOrderItem.setCreateTime(new Date());
            shipOrderItem.setQuantity(oRefundAfterSale.getQuantity());
            shipOrderItem.setUnshippedQuantity(oRefundAfterSale.getQuantity());
            shipOrderItemService.save(shipOrderItem);

            // 更新自己
            ORefundAfterSale update = new ORefundAfterSale();
            update.setId(oRefundAfterSale.getId());
            update.setReturnType(-1);//退回类型（0退回仓库；300退回供应商）
            update.setReturnWarehouseId(-1L);
            update.setReissueType(300);//补发、换货类型（0仓库补发换货；300供应商补发换货）
            update.setReissueWarehouseId(oRefundAfterSale.getSendWarehouseId());
            update.setReissueWarehouseName(oRefundAfterSale.getSendWarehouseName());
            update.setUpdateBy("确认供应商补发：" + userId);
            update.setResult("供应商补发");
            update.setUpdateTime(new Date());
            update.setStatus(10);
            mapper.updateById(update);
//            return ResultVo.success();
        } else if (oRefundAfterSale.getSendShipType().intValue() == EnumShipType.LOCAL.getIndex()
                || oRefundAfterSale.getSendShipType().intValue() == EnumShipType.CLOUD_WAREHOUSE.getIndex()
                || oRefundAfterSale.getSendShipType().intValue() == EnumShipType.JD_CLOUD_WAREHOUSE.getIndex()
        ) {
            ErpWarehouse warehouse = erpWarehouseService.getById(oRefundAfterSale.getSendWarehouseId());
            if (warehouse == null) {
                return ResultVo.error("仓库不存在");
            }
            if(oRefundAfterSale.getSendShipType().intValue() == EnumShipType.LOCAL.getIndex()) {
                log.info("==========本地仓补发========");
            }else{
                log.info("==========云仓补发========");
            }
            OOrder oOrder1 = orderService.getById(oRefundAfterSale.getOOrderId());
            OOrderStocking shipOrder = new OOrderStocking();
            shipOrder.setOrderType(80);//订单类型0正常订单20换货订单80补发订单
            shipOrder.setMerchantId(oRefundAfterSale.getMerchantId());
            shipOrder.setOOrderId(oRefundAfterSale.getOOrderId());
            shipOrder.setShipperId(oRefundAfterSale.getSendWarehouseId());
            shipOrder.setWarehouseId(oRefundAfterSale.getSendWarehouseId());
            shipOrder.setWarehouseType(warehouse.getWarehouseType());
            shipOrder.setWarehouseName(warehouse.getWarehouseName());
            shipOrder.setWarehouseNo(warehouse.getWarehouseNo());
            shipOrder.setType(oRefundAfterSale.getSendShipType());
            shipOrder.setShipMode(0);
            shipOrder.setOrderNum(oRefundAfterSale.getOrderNum());
            shipOrder.setOrderTime(LocalDateTime.now());
            shipOrder.setShopType(oRefundAfterSale.getShopType());
            shipOrder.setShopId(oRefundAfterSale.getShopId());
            shipOrder.setRemark("补发订单");
            shipOrder.setBuyerMemo("");
            shipOrder.setSellerMemo("");
            shipOrder.setSendStatus(1);
            shipOrder.setCreateTime(new Date());
            shipOrder.setProvince(oOrder1 != null ? oOrder1.getProvince() : "");
            shipOrder.setCity(oOrder1 != null ? oOrder1.getCity() : "");
            shipOrder.setTown(oOrder1 != null ? oOrder1.getTown() : "");
            shipOrder.setAddress(request.getReceiverAddress());
            shipOrder.setReceiverName(request.getReceiverName());
            shipOrder.setReceiverMobile(request.getReceiverTel());
            shipOrder.setOrderStatus(1);
            shipOrderMapper.insert(shipOrder);

            // 添加发货订单item
            OOrderStockingItem shipOrderItem = new OOrderStockingItem();
            shipOrderItem.setShipOrderId(shipOrder.getId());
            shipOrderItem.setMerchantId(shipOrder.getMerchantId());
            shipOrderItem.setOOrderId(shipOrder.getOOrderId());
            shipOrderItem.setOOrderItemId(oRefundAfterSale.getOOrderItemId());
            shipOrderItem.setOrderTime(shipOrder.getOrderTime());
            shipOrderItem.setOrderNum(shipOrder.getOrderNum());
            shipOrderItem.setSubOrderNum(oRefundAfterSale.getSubOrderNum());
            shipOrderItem.setSupplierId(shipOrder.getShipperId());
            shipOrderItem.setSkuId(org.springframework.util.StringUtils.hasText(oRefundAfterSale.getSkuId())?oRefundAfterSale.getSkuId():"");
            shipOrderItem.setProductId("");
            shipOrderItem.setGoodsId(oRefundAfterSale.getOGoodsId());
            shipOrderItem.setGoodsSkuId(oRefundAfterSale.getOGoodsSkuId());
            shipOrderItem.setRefundStatus(1);
            shipOrderItem.setGoodsName(oRefundAfterSale.getTitle());
            shipOrderItem.setGoodsNum("");
            shipOrderItem.setGoodsImg(oRefundAfterSale.getImg());
            shipOrderItem.setSkuName(oRefundAfterSale.getSkuInfo());
            shipOrderItem.setSkuCode(oRefundAfterSale.getSkuCode());
            shipOrderItem.setSendStatus(EnumShipStatus.NOT.getIndex());
            shipOrderItem.setCreateTime(new Date());
            shipOrderItem.setQuantity(oRefundAfterSale.getQuantity());
            shipOrderItem.setUnshippedQuantity(oRefundAfterSale.getQuantity());
            shipOrderItemService.save(shipOrderItem);
            // 更新自己
            ORefundAfterSale update = new ORefundAfterSale();
            update.setId(oRefundAfterSale.getId());
            update.setReturnType(-1);//退回类型（0退回仓库；300退回供应商）
            update.setReturnWarehouseId(-1l);
            update.setReissueType(0);//补发、换货类型（0仓库补发换货；300供应商补发换货）
            update.setReissueWarehouseId(warehouse.getId());
            update.setReissueWarehouseName(warehouse.getWarehouseName());
            update.setReissueWarehouseType(warehouse.getWarehouseType());
            update.setUpdateBy("确认仓库补发：" + userId);
            update.setUpdateTime(new Date());
            update.setResult("仓库补发：" + oRefundAfterSale.getOrderNum());
            update.setStatus(10);
            mapper.updateById(update);

        }


        return ResultVo.success();
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> exchangeConfirm(AfterSalesExchangeConfirmRequest request, Long userId) {
        log.info("==========开始执行换货确认=========");
        ORefundAfterSale oRefundAfterSale = mapper.selectById(request.getId());
        if (oRefundAfterSale == null) {
            return ResultVo.error("数据错误：不存在的售后处理数据");
        }
        if(oRefundAfterSale.getExchangeErpGoodsSkuId()==null||oRefundAfterSale.getExchangeErpGoodsSkuId().longValue()<=0){
            return ResultVo.error("没有找到换货商品数据");
        }
        if(oRefundAfterSale.getSendShipType()==null) return ResultVo.error("数据错误：售后没有发货方式数据");

        if(oRefundAfterSale.getSendShipType().intValue() == EnumShipType.SUPPLIER.getIndex()){
            log.info("==========供应商补发========");
            // 供应商换货
            // 1、生成供应商退货入库（不需要）
            // 2、生成供应商发货（换货类型的发货）
            OOrder oOrder1 = orderService.getById(oRefundAfterSale.getOOrderId());
//            log.info("=======新增换货发货单数据=============");
            OOrderStocking shipOrder = new OOrderStocking();
            shipOrder.setOrderType(20);//订单类型0正常订单20换货订单80补发订单
            shipOrder.setMerchantId(oRefundAfterSale.getMerchantId());
            shipOrder.setOOrderId(oRefundAfterSale.getOOrderId());
            shipOrder.setShipperId(oRefundAfterSale.getSendWarehouseId());
            shipOrder.setWarehouseId(oRefundAfterSale.getSendWarehouseId());
//            shipOrder.setWarehouseType(warehouse.getType());
//            shipOrder.setWarehouseName(warehouse.getWarehouseName());
//            shipOrder.setWarehouseNo(warehouse.getWarehouseNo());
            shipOrder.setType(oRefundAfterSale.getSendShipType());
            shipOrder.setShipMode(0);
            shipOrder.setOrderNum(oRefundAfterSale.getOrderNum());
            shipOrder.setOrderTime(LocalDateTime.now());
            shipOrder.setShopType(oRefundAfterSale.getShopType());
            shipOrder.setShopId(oRefundAfterSale.getShopId());
            shipOrder.setRemark("换货订单");
            shipOrder.setBuyerMemo("");
            shipOrder.setSellerMemo("");
            shipOrder.setSendStatus(1);
            shipOrder.setCreateTime(new Date());
            shipOrder.setProvince(oOrder1 != null ? oOrder1.getProvince() : "");
            shipOrder.setCity(oOrder1 != null ? oOrder1.getCity() : "");
            shipOrder.setTown(oOrder1 != null ? oOrder1.getTown() : "");
            shipOrder.setAddress(request.getReceiverAddress());
            shipOrder.setReceiverName(request.getReceiverName());
            shipOrder.setReceiverMobile(request.getReceiverTel());
            shipOrder.setOrderStatus(1);
            shipOrderMapper.insert(shipOrder);

            // 添加发货订单item
            OOrderStockingItem shipOrderItem = new OOrderStockingItem();
            shipOrderItem.setShipOrderId(shipOrder.getId());
            shipOrderItem.setMerchantId(shipOrder.getMerchantId());
            shipOrderItem.setOOrderId(shipOrder.getOOrderId());
            shipOrderItem.setOOrderItemId(oRefundAfterSale.getOOrderItemId());
            shipOrderItem.setOrderTime(shipOrder.getOrderTime());
            shipOrderItem.setOrderNum(shipOrder.getOrderNum());
            shipOrderItem.setSubOrderNum(oRefundAfterSale.getSubOrderNum());
            shipOrderItem.setSupplierId(shipOrder.getShipperId());
            //TODO 换成换货数据
            shipOrderItem.setSkuId(oRefundAfterSale.getExchangeSkuId());
            shipOrderItem.setGoodsName(oRefundAfterSale.getExchangeGoodsName());
            shipOrderItem.setGoodsImg(oRefundAfterSale.getExchangeGoodsImg());
            shipOrderItem.setSkuCode(oRefundAfterSale.getExchangeGoodsSkuCode());
            shipOrderItem.setSkuName(oRefundAfterSale.getExchangeGoodsSkuName());
            shipOrderItem.setGoodsId(oRefundAfterSale.getExchangeErpGoodsId());
            shipOrderItem.setGoodsSkuId(oRefundAfterSale.getExchangeErpGoodsSkuId());
            shipOrderItem.setQuantity(oRefundAfterSale.getExchangeGoodsNum());
//            shipOrderItem.setSkuId(org.springframework.util.StringUtils.hasText(oRefundAfterSale.getSkuId())?oRefundAfterSale.getSkuId():"");
            shipOrderItem.setProductId("");
//            shipOrderItem.setGoodsId(oRefundAfterSale.getOGoodsId());
//            shipOrderItem.setGoodsSkuId(oRefundAfterSale.getOGoodsSkuId());
            shipOrderItem.setRefundStatus(1);
//            shipOrderItem.setGoodsName(oRefundAfterSale.getTitle());
            shipOrderItem.setGoodsNum("");
//            shipOrderItem.setGoodsImg(oRefundAfterSale.getImg());
//            shipOrderItem.setSkuName(oRefundAfterSale.getSkuInfo());
//            shipOrderItem.setSkuCode(oRefundAfterSale.getSkuCode());
            shipOrderItem.setSendStatus(EnumShipStatus.NOT.getIndex());
            shipOrderItem.setCreateTime(new Date());
//            shipOrderItem.setQuantity(oRefundAfterSale.getQuantity());
            shipOrderItem.setUnshippedQuantity(shipOrderItem.getQuantity());
            shipOrderItemService.save(shipOrderItem);
            // 3、更新自己
            ORefundAfterSale update = new ORefundAfterSale();
            update.setId(oRefundAfterSale.getId());
            update.setReturnType(300);
            update.setReturnWarehouseId(oRefundAfterSale.getSendWarehouseId());
            update.setReturnWarehouseName(oRefundAfterSale.getSendWarehouseName());
            update.setReissueType(300);//补发、换货类型（0仓库补发换货；300供应商补发换货）
            update.setReissueWarehouseId(oRefundAfterSale.getSendWarehouseId());
            update.setReissueWarehouseName(oRefundAfterSale.getSendWarehouseName());
            update.setUpdateBy("确认供应商换货："+userId);
            update.setResult("供应商换货");
            update.setUpdateTime(new Date());
            update.setStatus(10);
            update.setExchangeErpOrderId(shipOrder.getId());
            mapper.updateById(update);
            return ResultVo.success();
        }else if (oRefundAfterSale.getSendShipType().intValue() == EnumShipType.LOCAL.getIndex()
                || oRefundAfterSale.getSendShipType().intValue() == EnumShipType.CLOUD_WAREHOUSE.getIndex()
                || oRefundAfterSale.getSendShipType().intValue() == EnumShipType.JD_CLOUD_WAREHOUSE.getIndex()
        ) {
            ErpWarehouse warehouse = erpWarehouseService.getById(oRefundAfterSale.getSendWarehouseId());
            if (warehouse == null) {
                return ResultVo.error("仓库不存在");
            }
            if(oRefundAfterSale.getSendShipType().intValue() == EnumShipType.LOCAL.getIndex()) {
                log.info("==========本地仓补发========");
            }else{
                log.info("==========云仓补发========");
            }
            // 1、生成仓库发货（换货类型的发货）-上面已统一处理
            OOrder oOrder1 = orderService.getById(oRefundAfterSale.getOOrderId());
            OOrderStocking shipOrder = new OOrderStocking();
            shipOrder.setOrderType(20);//订单类型0正常订单20换货订单80补发订单
            shipOrder.setMerchantId(oRefundAfterSale.getMerchantId());
            shipOrder.setOOrderId(oRefundAfterSale.getOOrderId());
            shipOrder.setShipperId(oRefundAfterSale.getSendWarehouseId());
            shipOrder.setWarehouseId(oRefundAfterSale.getSendWarehouseId());
            shipOrder.setWarehouseType(warehouse.getWarehouseType());
            shipOrder.setWarehouseName(warehouse.getWarehouseName());
            shipOrder.setWarehouseNo(warehouse.getWarehouseNo());
            shipOrder.setType(oRefundAfterSale.getSendShipType());
            shipOrder.setShipMode(0);
            shipOrder.setOrderNum(oRefundAfterSale.getOrderNum());
            shipOrder.setOrderTime(LocalDateTime.now());
            shipOrder.setShopType(oRefundAfterSale.getShopType());
            shipOrder.setShopId(oRefundAfterSale.getShopId());
            shipOrder.setRemark("换货订单");
            shipOrder.setBuyerMemo("");
            shipOrder.setSellerMemo("");
            shipOrder.setSendStatus(1);
            shipOrder.setCreateTime(new Date());
            shipOrder.setProvince(oOrder1 != null ? oOrder1.getProvince() : "");
            shipOrder.setCity(oOrder1 != null ? oOrder1.getCity() : "");
            shipOrder.setTown(oOrder1 != null ? oOrder1.getTown() : "");
            shipOrder.setAddress(request.getReceiverAddress());
            shipOrder.setReceiverName(request.getReceiverName());
            shipOrder.setReceiverMobile(request.getReceiverTel());
            shipOrder.setOrderStatus(1);
            shipOrderMapper.insert(shipOrder);

            // 添加发货订单item
            OOrderStockingItem shipOrderItem = new OOrderStockingItem();
            shipOrderItem.setShipOrderId(shipOrder.getId());
            shipOrderItem.setMerchantId(shipOrder.getMerchantId());
            shipOrderItem.setOOrderId(shipOrder.getOOrderId());
            shipOrderItem.setOOrderItemId(oRefundAfterSale.getOOrderItemId());
            shipOrderItem.setOrderTime(shipOrder.getOrderTime());
            shipOrderItem.setOrderNum(shipOrder.getOrderNum());
            shipOrderItem.setSubOrderNum(oRefundAfterSale.getSubOrderNum());
            shipOrderItem.setSupplierId(shipOrder.getShipperId());
            //TODO 换成换货数据
            shipOrderItem.setSkuId(oRefundAfterSale.getExchangeSkuId());
            shipOrderItem.setGoodsName(oRefundAfterSale.getExchangeGoodsName());
            shipOrderItem.setGoodsImg(oRefundAfterSale.getExchangeGoodsImg());
            shipOrderItem.setSkuCode(oRefundAfterSale.getExchangeGoodsSkuCode());
            shipOrderItem.setSkuName(oRefundAfterSale.getExchangeGoodsSkuName());
            shipOrderItem.setGoodsId(oRefundAfterSale.getExchangeErpGoodsId());
            shipOrderItem.setGoodsSkuId(oRefundAfterSale.getExchangeErpGoodsSkuId());
            shipOrderItem.setQuantity(oRefundAfterSale.getExchangeGoodsNum());
//            shipOrderItem.setSkuId(org.springframework.util.StringUtils.hasText(oRefundAfterSale.getSkuId())?oRefundAfterSale.getSkuId():"");
            shipOrderItem.setProductId("");
//            shipOrderItem.setGoodsId(oRefundAfterSale.getOGoodsId());
//            shipOrderItem.setGoodsSkuId(oRefundAfterSale.getOGoodsSkuId());
            shipOrderItem.setRefundStatus(1);
//            shipOrderItem.setGoodsName(oRefundAfterSale.getTitle());
            shipOrderItem.setGoodsNum("");
//            shipOrderItem.setGoodsImg(oRefundAfterSale.getImg());
//            shipOrderItem.setSkuName(oRefundAfterSale.getSkuInfo());
//            shipOrderItem.setSkuCode(oRefundAfterSale.getSkuCode());
            shipOrderItem.setSendStatus(EnumShipStatus.NOT.getIndex());
            shipOrderItem.setCreateTime(new Date());
//            shipOrderItem.setQuantity(oRefundAfterSale.getQuantity());
            shipOrderItem.setUnshippedQuantity(shipOrderItem.getQuantity());
            shipOrderItemService.save(shipOrderItem);
            // 2、生成仓库退货入库数据

            //添加入库主表信息
            ErpStockIn stockIn = new ErpStockIn();
            stockIn.setMerchantId(oRefundAfterSale.getMerchantId());
            stockIn.setStockInNum("THRK-" + DateUtils.parseDateToStr("yyyyMMdd", new Date()) + "-" + System.currentTimeMillis() / 1000);
            stockIn.setStockInType(EnumStockInType.RET_STOCK_IN.getIndex());
            stockIn.setStockInOperator("");
            stockIn.setStockInOperatorId("0");
//        insert.setStockInTime(new Date());
            stockIn.setSourceNo(oRefundAfterSale.getRefundNum());
            stockIn.setRemark(request.getRemark());
            stockIn.setCreateBy("退货确认" + userId);
            stockIn.setCreateTime(new Date());
            stockIn.setSourceGoodsUnit(1);
            stockIn.setSourceSpecUnit(1);
            stockIn.setSourceSpecUnitTotal(oRefundAfterSale.getQuantity());
            stockIn.setStatus(0);//状态（0待入库1部分入库2全部入库）
//            stockIn.setWarehouseId(warehouse.getId());
//            stockIn.setWarehouseNo(warehouse.getWarehouseNo());
//            stockIn.setWarehouseName(warehouse.getWarehouseName());
//            stockIn.setWarehouseType(warehouse.getWarehouseType());
            stockIn.setWarehouseId(warehouse.getId());
            stockIn.setWarehouseNo(warehouse.getWarehouseNo());
            stockIn.setWarehouseName(warehouse.getWarehouseName());
            stockIn.setWarehouseType(warehouse.getWarehouseType());

            erpStockInService.save(stockIn);
            //添加子表信息

            OGoodsSku goodsSku = oGoodsSkuService.getById(oRefundAfterSale.getOGoodsSkuId());
            if (goodsSku != null) {
//            OGoods goods = goodsService.getById(goodsSku.getGoodsId());
                ErpStockInItem inItem = new ErpStockInItem();
                inItem.setMerchantId(stockIn.getMerchantId());
                inItem.setStockInId(stockIn.getId());
                inItem.setStockInType(stockIn.getStockInType());
                inItem.setSourceNo(stockIn.getSourceNo());
                inItem.setSourceId(0L);
                inItem.setSourceItemId(0L);
                inItem.setGoodsId(goodsSku.getGoodsId());
                inItem.setGoodsName(oRefundAfterSale.getTitle());
                inItem.setGoodsImage(goodsSku.getColorImage());
                inItem.setGoodsNum(goodsSku.getGoodsNum());
                inItem.setSkuName(goodsSku.getSkuName());
                inItem.setSkuId(goodsSku.getId());
                inItem.setSkuCode(goodsSku.getSkuCode());
                inItem.setQuantity(oRefundAfterSale.getQuantity());
                inItem.setInQuantity(0);
                inItem.setPurPrice(goodsSku.getPurPrice().doubleValue());
                inItem.setStatus(0);
                inItem.setCreateBy(stockIn.getCreateBy());
                inItem.setCreateTime(new Date());
                erpStockInItemService.save(inItem);
            } else {
                log.error("=====退货生成入库单=======没有找到商品Sku信息======");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultVo.error("退货入库==没有找到商品Sku信息");
            }

            // 3、更新自己
            ORefundAfterSale update = new ORefundAfterSale();
            update.setId(oRefundAfterSale.getId());
            update.setReturnType(0);//退回类型（0退回仓库；300退回供应商）
            update.setReturnWarehouseId(warehouse.getId());
            update.setReturnWarehouseName(warehouse.getWarehouseName());
            update.setReturnWarehouseType(warehouse.getWarehouseType());
            update.setReissueType(0);//补发、换货类型（0仓库补发换货；300供应商补发换货）
            update.setReissueWarehouseId(warehouse.getId());
            update.setReissueWarehouseName(warehouse.getWarehouseName());
            update.setReissueWarehouseType(warehouse.getWarehouseType());
            update.setUpdateBy("确认仓库换货：" + userId);
            update.setUpdateTime(new Date());
            update.setResult("仓库换货：" + oRefundAfterSale.getOrderNum());
            update.setStatus(10);
            update.setExchangeErpOrderId(shipOrder.getId());
            mapper.updateById(update);

        }

        return ResultVo.success();
    }
}




