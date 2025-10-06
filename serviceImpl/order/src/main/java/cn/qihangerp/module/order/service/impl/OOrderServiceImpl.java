package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.model.entity.*;
import cn.qihangerp.model.request.OrderSearchRequest;
import cn.qihangerp.module.goods.mapper.OGoodsMapper;
import cn.qihangerp.module.goods.mapper.OGoodsSkuMapper;
import cn.qihangerp.module.goods.mapper.OGoodsSupplierMapper;
import cn.qihangerp.module.mapper.OLogisticsCompanyMapper;
import cn.qihangerp.module.order.domain.*;
import cn.qihangerp.module.order.domain.bo.OrderAllocateShipRequest;
import cn.qihangerp.module.order.domain.bo.OrderShipRequest;
import cn.qihangerp.model.vo.OrderDiscountVo;
import cn.qihangerp.module.order.domain.vo.SalesDailyVo;
import cn.qihangerp.module.order.mapper.*;
import cn.qihangerp.module.order.service.OOrderService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.JdOrderStateEnum;
import cn.qihangerp.common.enums.TaoOrderStateEnum;
import cn.qihangerp.common.utils.DateUtils;
import cn.qihangerp.common.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author qilip
* @description 针对表【o_order(订单表)】的数据库操作Service实现
* @createDate 2024-03-09 13:15:57
*/
@Slf4j
@AllArgsConstructor
@Service
public class OOrderServiceImpl extends ServiceImpl<OOrderMapper, OOrder>
    implements OOrderService {

    private final OOrderMapper orderMapper;
    private final OOrderItemMapper orderItemMapper;
    private final OLogisticsCompanyMapper logisticsCompanyMapper;
    private final OGoodsSkuMapper oGoodsSkuMapper;
    private final OGoodsMapper oGoodsMapper;
    private final OGoodsSupplierMapper supplierMapper;

    private final OOrderShipListMapper orderShipListMapper;
    private final OOrderShipListItemMapper orderShipListItemMapper;

    private final ErpShipmentMapper shipmentMapper;
    private final ErpShipmentItemMapper shipmentItemMapper;

    private final OfflineOrderMapper offlineOrderMapper;
    private final OfflineOrderItemMapper offlineOrderItemMapper;



    private final String DATE_PATTERN =
            "^(?:(?:(?:\\d{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|(?:(?:(?:\\d{2}(?:0[48]|[2468][048]|[13579][26])|(?:(?:0[48]|[2468][048]|[13579][26])00))-0?2-29))$)|(?:(?:(?:\\d{4}-(?:0?[13578]|1[02]))-(?:0?[1-9]|[12]\\d|30))$)|(?:(?:(?:\\d{4}-0?[13-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|30))$)|(?:(?:(?:\\d{2}(?:0[48]|[13579][26]|[2468][048])|(?:(?:0[48]|[13579][26]|[2468][048])00))-0?2-29))$)$";
    private final Pattern DATE_FORMAT = Pattern.compile(DATE_PATTERN);


    @Transactional
    @Override
    public ResultVo<Integer> jdOrderMessage(String orderId,JSONObject orderDetail) {
        log.info("京东订单消息处理"+orderId);
        return ResultVo.success();
    }



    @Transactional
    @Override
    public ResultVo<Integer> taoOrderMessage(String tid,JSONObject orderDetail ) {
        log.info("Tao订单消息处理"+tid);
        return ResultVo.success();
    }


    @Transactional
    @Override
    public ResultVo<Integer> pddOrderMessage(String orderSn,JSONObject orderDetail ) {
        log.info("=====pdd order message===订单号{}===" + orderSn);
        return ResultVo.success();
    }

    @Transactional
    @Override
    public ResultVo<Integer> douOrderMessage(String orderId,JSONObject orderDetail ) {
        log.info("Dou订单消息处理" + orderId);

        return ResultVo.success();
    }

    @Transactional
    @Override
    public ResultVo<Integer> weiOrderMessage(String orderId,JSONObject orderDetail ) {
        log.info("WEI订单消息处理" + orderId);


        return ResultVo.success();
    }
    /**
     * 线下订单通知
     * @param
     * @return
     */
    @Transactional
    @Override
    public ResultVo<Integer> offlineOrderMessage(String orderNum) {
        log.info("Offline订单消息处理" + orderNum);
        List<OfflineOrder> originOrders = offlineOrderMapper.selectList(new LambdaQueryWrapper<OfflineOrder>().eq(OfflineOrder::getOrderNum, orderNum));

        if (originOrders == null || originOrders.size() == 0) {
            // 没有找到订单信息
            return ResultVo.error(ResultVoEnum.NotFound, "没有找到OFFLINE原始订单：" + orderNum);
        }
        OfflineOrder originOrder = originOrders.get(0);

        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderNum, orderNum));
        if (oOrders == null || oOrders.isEmpty()) {
            // 新增订单
            OOrder insert = new OOrder();
            insert.setOrderNum(originOrder.getOrderNum());
            insert.setShopType(EnumShopType.OFFLINE.getIndex());
            insert.setShopId(originOrder.getShopId());
            insert.setBuyerMemo(originOrder.getBuyerMemo());
            insert.setSellerMemo(originOrder.getSellerMemo());
            insert.setRefundStatus(originOrder.getRefundStatus());
            insert.setOrderStatus(originOrder.getOrderStatus());

            insert.setGoodsAmount(originOrder.getGoodsAmount());
            insert.setPostFee(originOrder.getPostFee());
            insert.setAmount(originOrder.getAmount());
            insert.setPayment(originOrder.getPayment());
            insert.setPlatformDiscount(originOrder.getPlatformDiscount());
            insert.setSellerDiscount(originOrder.getSellerDiscount());

            insert.setReceiverName(originOrder.getReceiverName());
            insert.setReceiverMobile(originOrder.getReceiverMobile());
            insert.setAddress(originOrder.getAddress());
            insert.setProvince(originOrder.getProvince());
            insert.setCity(originOrder.getCity());
            insert.setTown(originOrder.getTown());
            insert.setOrderTime(originOrder.getOrderTime());
            insert.setShipType(0);
            insert.setCreateTime(new Date());
            insert.setCreateBy("ORDER_MESSAGE");

            orderMapper.insert(insert);
            // 插入orderItem
            addOfflineOrderItem(insert.getId(), originOrder.getOrderNum(), originOrder.getOrderStatus(), originOrder.getRefundStatus(),insert.getShopId());

            //更新推送状态
            OfflineOrder offlineUpdate = new OfflineOrder();
            offlineUpdate.setId(originOrder.getId());
            offlineUpdate.setOmsPushStatus(1);
            offlineUpdate.setUpdateTime(new Date());
            offlineUpdate.setUpdateBy("推送状态更新");
            offlineOrderMapper.updateById(offlineUpdate);
        } else {
            // 修改订单 (修改：)
            OOrder update = new OOrder();
            update.setId(oOrders.get(0).getId());
            update.setShopType(EnumShopType.OFFLINE.getIndex());
            update.setShopId(originOrder.getShopId());
            update.setRefundStatus(originOrder.getRefundStatus());
            update.setOrderStatus(originOrder.getOrderStatus());
            update.setReceiverName(originOrder.getReceiverName());
            update.setReceiverMobile(originOrder.getReceiverMobile());
            update.setAddress(originOrder.getAddress());
            update.setProvince(originOrder.getProvince());
            update.setCity(originOrder.getCity());
            update.setTown(originOrder.getTown());
            update.setUpdateTime(new Date());
            update.setUpdateBy("ORDER_MESSAGE");
            orderMapper.updateById(update);
        }
        return ResultVo.success();
    }
    private void addOfflineOrderItem(String oOrderId,String originOrderNum,Integer orderStatus,Integer refundStatus,Long shopId){
        List<OfflineOrderItem> originOrderItems = offlineOrderItemMapper.selectList(new LambdaQueryWrapper<OfflineOrderItem>().eq(OfflineOrderItem::getOrderNum, originOrderNum));
        if(originOrderItems!=null && originOrderItems.size()>0) {
            for (var item : originOrderItems) {
                OOrderItem orderItem = new OOrderItem();
                orderItem.setOrderId(oOrderId);
                orderItem.setShopType(EnumShopType.OFFLINE.getIndex());
                orderItem.setShopId(shopId);
                orderItem.setOrderNum(originOrderNum);
                orderItem.setSubOrderNum(item.getSubOrderNum());
                // 这里将订单商品skuid转换成erp系统的skuid
                Long erpGoodsId = 0L;
                String erpSkuId = "0";

//                DouGoodsSku douGoodsSku = douGoodsSkuMapper.selectById(item.getSkuId());
//                if (douGoodsSku != null ) {
//                    erpGoodsId = douGoodsSku.getOGoodsId();
//                    erpSkuId = douGoodsSku.getOGoodsSkuId();
////                        orderItem.setGoodsImg(taoGoodsSku.get(0).getLogo());
////                        orderItem.setGoodsSpec(jdGoodsSkus.get(0).getSkuName());
////                    orderItem.setSkuNum(taoGoodsSku.get(0).getOuterId());
//                }
//                List<DouGoodsSku> douGoodsSku = douGoodsSkuMapper.selectList(new LambdaQueryWrapper<DouGoodsSku>().eq(DouGoodsSku::getId, item.getSkuId()));
//                if (douGoodsSku != null && !douGoodsSku.isEmpty()) {
//                    erpGoodsId = douGoodsSku.get(0).getOGoodsId();
//                    erpSkuId = douGoodsSku.get(0).getOGoodsSkuId();
////                        orderItem.setGoodsImg(taoGoodsSku.get(0).getLogo());
////                        orderItem.setGoodsSpec(jdGoodsSkus.get(0).getSkuName());
////                    orderItem.setSkuNum(taoGoodsSku.get(0).getOuterId());
//                }
                orderItem.setSkuNum(item.getSkuNum());
                orderItem.setSkuId(item.getSkuId());
                orderItem.setGoodsId(item.getGoodsId());
                orderItem.setGoodsSkuId(item.getGoodsSkuId());
                orderItem.setGoodsImg(item.getGoodsImg());
                orderItem.setGoodsSpec(item.getGoodsSpec());
                orderItem.setGoodsTitle(item.getGoodsTitle());
                orderItem.setGoodsPrice(item.getGoodsPrice());
                orderItem.setItemAmount(item.getItemAmount());
                orderItem.setPayment(item.getPayment());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setOrderStatus(orderStatus);
                orderItem.setRefundStatus(refundStatus);
                orderItem.setRefundCount(0);
                orderItem.setCreateTime(new Date());
                orderItem.setCreateBy("ORDER_MESSAGE");
                orderItemMapper.insert(orderItem);
            }
        }
    }
    /**
     * 获取待发货list（去除处理过的）
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OOrder> queryWaitShipmentPageList(OrderSearchRequest bo, PageQuery pageQuery) {

        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()
                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .eq(OOrder::getOrderStatus,1)
                .eq(OOrder::getRefundStatus,1)
                .eq(OOrder::getShipStatus,0)//发货状态 0 待发货 1 已分配供应商发货 2全部发货
//                .lt(ErpOrder::getShipType,2)//ship_type发货方式 0 自己发货1联合发货2供应商发货
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (OOrder order:pages.getRecords()) {
                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                                .eq(OOrderItem::getOrderId, order.getId())
                                .eq(OOrderItem::getShipStatus,0)
//                        .eq(ErpOrderItem::getShipType,0)
                ));
            }
        }

        return PageResult.build(pages);
    }

    /**
     * 查询分配给供应商发货的订单list
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OOrder> queryAssignedShipmentList(OrderSearchRequest bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()
                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .ne(OOrder::getShipper,0)//ship_type发货方 0 自己发货1联合发货2供应商发货
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (OOrder order:pages.getRecords()) {
                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                        .eq(OOrderItem::getOrderId, order.getId())
                        .ne(OOrderItem::getShipper,0)
                ));
//                order.setShipmentList(shipmentMapper.selectList(new LambdaQueryWrapper<ErpShipment>().eq(ErpShipment::getOrderId,order.getId())));
            }
        }

        return PageResult.build(pages);

    }

    /**
     * 已经发货的list（去除分配给供应商发货的）
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OOrder> queryShippedPageList(OrderSearchRequest bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()

                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .eq(bo.getShipType()!=null,OOrder::getShipType,bo.getShipType())
                .eq(OOrder::getShipStatus,2)//发货状态 0 待发货 1 已分配供应商发货 2全部发货

                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (OOrder order:pages.getRecords()) {
                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                        .eq(OOrderItem::getOrderId, order.getId())
                        .eq(OOrderItem::getShipStatus,2)
                ));
//                order.setShipmentList(shipmentMapper.selectList(new LambdaQueryWrapper<ErpShipment>().eq(ErpShipment::getOrderId,order.getId())));
            }
        }

        return PageResult.build(pages);
    }

    @Override
    public List<OOrder> getList(OOrder order) {
        return orderMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public PageResult<OOrder> queryPageList(OrderSearchRequest bo, PageQuery pageQuery) {
        if(org.springframework.util.StringUtils.hasText(bo.getStartTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getStartTime());
            boolean b = matcher.find();
            if(b){
                bo.setStartTime(bo.getStartTime()+" 00:00:00");
            }
        }
        if(org.springframework.util.StringUtils.hasText(bo.getEndTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getEndTime());
            boolean b = matcher.find();
            if(b){
                bo.setEndTime(bo.getEndTime()+" 23:59:59");
            }
        }

        LambdaQueryWrapper<OOrder> queryWrapper = new LambdaQueryWrapper<OOrder>()
                .eq(bo.getShopId()!=null,OOrder::getShopId,bo.getShopId())
                .eq(bo.getShopType()!=null,OOrder::getShopType,bo.getShopType())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),OOrder::getOrderNum,bo.getOrderNum())
                .eq(bo.getOrderStatus()!=null,OOrder::getOrderStatus,bo.getOrderStatus())
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),OOrder::getOrderTime,bo.getStartTime()+" 00:00:00")
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),OOrder::getOrderTime,bo.getEndTime()+" 23:59:59")

                .eq(org.springframework.util.StringUtils.hasText(bo.getReceiverName()),OOrder::getReceiverName,bo.getReceiverName())
                .like(org.springframework.util.StringUtils.hasText(bo.getReceiverMobile()),OOrder::getReceiverMobile,bo.getReceiverMobile())
                ;

//        pageQuery.setOrderByColumn("order_time");
//        pageQuery.setIsAsc("desc");
        Page<OOrder> pages = orderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (var order:pages.getRecords()) {
//                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, order.getId())));
                order.setItemVoList(orderItemMapper.selectOrderItemListByOrderId(Long.parseLong(order.getId())));
            }
        }

        return PageResult.build(pages);
    }

    @Override
    public OOrder queryDetailById(Long id) {
        OOrder oOrder = orderMapper.selectById(id);
        if(oOrder!=null) {
//           oOrder.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, oOrder.getId())));
            oOrder.setItemVoList(orderItemMapper.selectOrderItemListByOrderId(id));
            // 获取优惠信息
            if(oOrder.getShopType()==EnumShopType.TAO.getIndex()){
                oOrder.setDiscounts(orderMapper.getTaoOrderDiscount(oOrder.getOrderNum()));
            } else if (oOrder.getShopType()==EnumShopType.JD.getIndex()) {
                oOrder.setDiscounts(orderMapper.getJdOrderDiscount(oOrder.getOrderNum()));
            }else if (oOrder.getShopType()==EnumShopType.PDD.getIndex()) {
                List<OrderDiscountVo> discountVoList = new ArrayList<>();
                if(oOrder.getPlatformDiscount()!=null&& oOrder.getPlatformDiscount()>0){
                    OrderDiscountVo vo = new OrderDiscountVo();
                    vo.setName("平台优惠");
                    vo.setDiscountAmount(oOrder.getPlatformDiscount().toString());
                    vo.setDescription("平台优惠");
                    discountVoList.add(vo);
                    oOrder.setDiscounts(discountVoList);
                }
            }else if (oOrder.getShopType()==EnumShopType.DOU.getIndex()) {
                List<OrderDiscountVo> discountVoList = new ArrayList<>();
                if(oOrder.getPlatformDiscount()!=null&& oOrder.getPlatformDiscount()>0){
                    OrderDiscountVo vo = new OrderDiscountVo();
                    vo.setName("平台优惠");
                    vo.setDiscountAmount(oOrder.getPlatformDiscount().toString());
                    vo.setDescription("平台优惠");
                    discountVoList.add(vo);
                    oOrder.setDiscounts(discountVoList);
                }
            }
        }

        return oOrder;
    }

    @Override
    public List<OOrder> searchOrderConsignee(String consignee) {
        LambdaQueryWrapper<OOrder> qw = new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderStatus,1).likeRight(OOrder::getReceiverName,consignee);
        return orderMapper.selectList(qw);
    }

    @Override
    public List<OOrderItem> searchOrderItemByReceiverMobile(String receiverMobile) {
        List<OOrder> oOrders = orderMapper.selectList(new LambdaQueryWrapper<OOrder>().eq(OOrder::getOrderStatus, 1).eq(OOrder::getReceiverMobile, receiverMobile));
        List<OOrderItem> orderItemList = new ArrayList<>();
        if(oOrders!=null){
            for (var order:oOrders) {
                orderItemList.addAll(orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId,order.getId())));
            }
        }
        return orderItemList;
    }

    @Override
    public List<SalesDailyVo> salesDaily() {
        return orderMapper.salesDaily();
    }
    @Override
    public SalesDailyVo getTodaySalesDaily() {
        return orderMapper.getTodaySalesDaily();
    }

    @Override
    public Integer getWaitShipOrderAllCount() {
        return orderMapper.getWaitShipOrderAllCount();
    }

    /**
     * 手动发货
     * @param shipBo
     * @param createBy
     * @return
     */
    @Transactional
    @Override
    public ResultVo<Integer> manualShipmentOrder(OrderShipRequest shipBo, String createBy) {
        if (org.springframework.util.StringUtils.isEmpty(shipBo.getId()) || shipBo.getId().equals("0"))
            return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数：id");

        OOrder erpOrder = orderMapper.selectById(shipBo.getId());
        if (erpOrder == null) {
            return ResultVo.error("找不到订单数据");
        } else if (erpOrder.getOrderStatus().intValue() != 1 && erpOrder.getRefundStatus().intValue() != 1) {
            return ResultVo.error("订单状态不对，不允许发货");
        }
        if(erpOrder.getShipStatus()!=0){
            return ResultVo.error("订单已分配供应商发货，不允许手动发货");
        }

        OLogisticsCompany erpLogisticsCompany = logisticsCompanyMapper.selectById(shipBo.getShippingCompany());
        if(erpLogisticsCompany==null) return ResultVo.error("快递公司选择错误");

        // 自己发货的list
        List<OOrderItem> oOrderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OOrderItem>()
                        .eq(OOrderItem::getOrderId, erpOrder.getId())
                        .eq(OOrderItem::getShipStatus,0)
                        .eq(OOrderItem::getShipper,0)
        );
        if(oOrderItems==null) return ResultVo.error("订单 item 数据错误，无法发货！");

        // 添加到备货单
        OOrderShipList shipList = new OOrderShipList();
        shipList.setShopId(erpOrder.getShopId());
        shipList.setShopType(erpOrder.getShopType());
        shipList.setShipper(0);
        shipList.setShipSupplierId(0L);
        shipList.setShipSupplier("自由仓库发货");
        shipList.setOrderId(Long.parseLong(erpOrder.getId()));
        shipList.setOrderNum(erpOrder.getOrderNum());
        shipList.setStatus(0);
        shipList.setRemark(erpOrder.getRemark());
        shipList.setBuyerMemo(erpOrder.getBuyerMemo());
        shipList.setSellerMemo(erpOrder.getSellerMemo());
        shipList.setShipLogisticsCompany(erpLogisticsCompany.getName());
        shipList.setShipLogisticsCompanyCode(erpLogisticsCompany.getCode());
        shipList.setShipLogisticsCode(shipBo.getShippingNumber());
        shipList.setShipStatus(2);
        shipList.setCreateTime(new Date());
        shipList.setCreateBy("手动发货");
        orderShipListMapper.insert(shipList);

        // 添加发货记录
        ErpShipment erpShipment = new ErpShipment();
        erpShipment.setShipper(0);//发货方 0 仓库发货 1 供应商发货】
        erpShipment.setShopId(erpOrder.getShopId());
        erpShipment.setShopType(erpOrder.getShopType());
        erpShipment.setOrderId(Long.parseLong(erpOrder.getId()));
        erpShipment.setOrderNum(erpOrder.getOrderNum());
        erpShipment.setOrderTime(erpOrder.getOrderTime());
        erpShipment.setShipType(1);//发货类型（1订单发货2商品补发3商品换货）
        erpShipment.setShipCompany(erpLogisticsCompany.getName());
        erpShipment.setShipCompanyCode(erpLogisticsCompany.getCode());
        erpShipment.setShipCode(shipBo.getShippingNumber());
        erpShipment.setShipFee(shipBo.getShippingCost());
        erpShipment.setShipTime(new Date());
        erpShipment.setShipOperator(shipBo.getShippingMan());
        erpShipment.setShipStatus(1);//物流状态（0 待发货1已发货2已完成）

        erpShipment.setPackageHeight(shipBo.getHeight());
        erpShipment.setPackageWeight(shipBo.getWeight());
        erpShipment.setPackageLength(shipBo.getLength());
        erpShipment.setPackageWidth(shipBo.getWidth());
        erpShipment.setPacksgeOperator(shipBo.getShippingMan());
//        erpShipment.setPackages(JSONObject.toJSONString(oOrderItems));
        erpShipment.setRemark(shipBo.getRemark());
        erpShipment.setCreateBy(createBy);
        erpShipment.setCreateTime(new Date());

        shipmentMapper.insert(erpShipment);

        for(OOrderItem orderItem:oOrderItems){
            // 添加备货清单item
            OOrderShipListItem listItem=new OOrderShipListItem();
            listItem.setShopId(erpOrder.getShopId());
            listItem.setShopType(erpOrder.getShopType());
            listItem.setListId(shipList.getId());
            listItem.setShipper(shipList.getShipper());
            listItem.setShipSupplier(shipList.getShipSupplier());
            listItem.setShipSupplierId(shipList.getShipSupplierId());
            listItem.setOrderId(Long.parseLong(orderItem.getOrderId()));
            listItem.setOrderItemId(Long.parseLong(orderItem.getId()));
            listItem.setOrderNum(orderItem.getOrderNum());
            listItem.setOriginalSkuId(orderItem.getSkuId());
            listItem.setGoodsId(orderItem.getGoodsId());
            listItem.setSkuId(orderItem.getGoodsSkuId());
            listItem.setGoodsTitle(orderItem.getGoodsTitle());
            listItem.setGoodsImg(orderItem.getGoodsImg());
            listItem.setGoodsNum(orderItem.getGoodsNum());
            listItem.setSkuName(orderItem.getGoodsSpec());
            listItem.setSkuNum(orderItem.getSkuNum());
            listItem.setQuantity(orderItem.getQuantity());
            listItem.setStatus(0);//状态0待备货1备货中2备货完成3已发货
            listItem.setCreateBy("手动发货");
            listItem.setCreateTime(new Date());
            orderShipListItemMapper.insert(listItem);
            // 添加发货明细
            ErpShipmentItem erpShipmentItem = new ErpShipmentItem();
            erpShipmentItem.setShipper(erpShipment.getShipper());
            erpShipmentItem.setShopId(erpShipment.getShopId());
            erpShipmentItem.setShopType(erpShipment.getShopType());
            erpShipmentItem.setShipmentId(erpShipment.getId());
            erpShipmentItem.setOrderId(erpShipment.getOrderId());
            erpShipmentItem.setOrderNum(erpShipment.getOrderNum());
            erpShipmentItem.setOrderTime(erpShipment.getOrderTime());
            erpShipmentItem.setOrderItemId(Long.parseLong(orderItem.getId()));
            erpShipmentItem.setErpGoodsId(orderItem.getGoodsId());
            erpShipmentItem.setErpSkuId(orderItem.getGoodsSkuId());
            erpShipmentItem.setGoodsTitle(orderItem.getGoodsTitle());
            erpShipmentItem.setGoodsNum(orderItem.getGoodsNum());
            erpShipmentItem.setGoodsImg(orderItem.getGoodsImg());
            erpShipmentItem.setGoodsSpec(orderItem.getGoodsSpec());
            erpShipmentItem.setSkuNum(orderItem.getSkuNum());
            erpShipmentItem.setQuantity(orderItem.getQuantity());
            erpShipmentItem.setRemark(orderItem.getRemark());
            erpShipmentItem.setStockStatus(0);
            erpShipmentItem.setCreateBy(createBy);
            erpShipmentItem.setCreateTime(new Date());
            shipmentItemMapper.insert(erpShipmentItem);

            // 更新订单item发货状态
            OOrderItem orderItemUpdate = new OOrderItem();
            orderItemUpdate.setId( orderItem.getId());
            orderItemUpdate.setUpdateBy("手动发货");
            orderItemUpdate.setUpdateTime(new Date());
            orderItemUpdate.setShipper(0);
            orderItemUpdate.setShipStatus(2);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
            orderItemUpdate.setShipType(2);//发货方式1电子面单发货2手动发货
            orderItemMapper.updateById(orderItemUpdate);
        }


        // 更新状态、发货方式
        OOrder update = new OOrder();
        update.setId(erpOrder.getId());
        update.setShipper(0);
        update.setShipStatus(2);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
        update.setOrderStatus(2);
        update.setShipType(2);//发货方式1电子面单发货2手动发货

        update.setUpdateTime(new Date());
        update.setUpdateBy("手动发货");
        orderMapper.updateById(update);

        return ResultVo.success();
    }

    /**
     * 分配供应商发货
     * @param shipBo
     * @param createBy
     * @return
     */
    @Override
    public ResultVo<Integer> allocateShipmentOrder(OrderAllocateShipRequest shipBo, String createBy) {
        if (org.springframework.util.StringUtils.isEmpty(shipBo.getId()) || shipBo.getId().equals("0"))
            return ResultVo.error(ResultVoEnum.ParamsError, "缺少参数：id");

        OOrder erpOrder = orderMapper.selectById(shipBo.getId());
        if (erpOrder == null) {
            return ResultVo.error("找不到订单数据");
        } else if (erpOrder.getOrderStatus().intValue() != 1 && erpOrder.getRefundStatus().intValue() != 1) {
            return ResultVo.error("订单状态不对，不允许分配发货");
        }
        if(erpOrder.getShipStatus()!=0){
            return ResultVo.error("订单发货已处理，不允许分配发货");
        }

        List<OOrderItem> oOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                .eq(OOrderItem::getOrderId, erpOrder.getId()));
        if(oOrderItems==null) return ResultVo.error("订单 item 数据错误，无法发货！");

        long skuIdZeroCount = oOrderItems.stream().filter(x -> x.getGoodsSkuId() == 0).count();
        if(skuIdZeroCount>0) return ResultVo.error("订单 item 数据中有skuId错误的数据，请补充！");

        // 按 订单明细找出同供应商 分组
        Map<Long,List<OOrderItem>> supplierOrderItemList = new TreeMap<>();
        Map<Long, OGoodsSupplier> supplierList = new TreeMap<>();
        for(OOrderItem orderItem:oOrderItems){
            OGoodsSku erpGoodsSku = oGoodsSkuMapper.selectById(orderItem.getGoodsSkuId());
            if(erpGoodsSku==null) {
                return ResultVo.error("订单明细找不到商品sku信息");
            }
            OGoods erpGoods = oGoodsMapper.selectById(erpGoodsSku.getGoodsId());
            if(erpGoods==null){
                return ResultVo.error("订单明细找不到商品信息");
            }
            OGoodsSupplier erpSupplier = supplierMapper.selectById(erpGoods.getSupplierId());
            if(erpSupplier==null){
                return ResultVo.error("订单明细商品找不到供应商信息");
            }
            // 组合供应商
            boolean isExist = supplierOrderItemList.containsKey(erpSupplier.getId());
            if(isExist){
                supplierOrderItemList.get(erpSupplier.getId()).add(orderItem);
            }else{
                List<OOrderItem> orderItemList = new ArrayList<>();
                orderItemList.add(orderItem);
                supplierOrderItemList.put(Long.parseLong(erpSupplier.getId()),orderItemList);
            }
            supplierList.put(Long.parseLong(erpSupplier.getId()),erpSupplier);
        }

        // 开始组装分配数据
        // 遍历 Map
        for (Map.Entry<Long, List<OOrderItem>> entry : supplierOrderItemList.entrySet()) {
            Long supplierId = entry.getKey();  // 获取键（Long）
            List<OOrderItem> orderItemList = entry.getValue();  // 获取值（List<ErpOrderItem>）

            // 添加分配发货
//            ErpShipment erpShipment = new ErpShipment();
//            erpShipment.setShipper(1);//发货方 0 仓库发货 1 供应商发货】
//            erpShipment.setSupplierId(supplierId);
//            erpShipment.setSupplier(supplierList.get(supplierId)!=null?supplierList.get(supplierId).getName():"");
//            erpShipment.setTenantId(erpOrder.getTenantId());
//            erpShipment.setShopId(erpOrder.getShopId());
//            erpShipment.setShopType(erpOrder.getShopType());
//            erpShipment.setOrderId(erpOrder.getId());
//            erpShipment.setOrderNum(erpOrder.getOrderNum());
//            erpShipment.setOrderTime(erpOrder.getOrderTime());
//            erpShipment.setShipType(1);//发货类型（1订单发货2商品补发3商品换货）
//            erpShipment.setShipCompany("");
//            erpShipment.setShipCompanyCode("");
//            erpShipment.setShipCode("");
//            erpShipment.setShipFee(BigDecimal.ZERO);
//            erpShipment.setShipStatus(0);//物流状态（0 待发货1已发货2已完成）
//
//            erpShipment.setPackageHeight(0.0);
//            erpShipment.setPackageWeight(0.0);
//            erpShipment.setPackageLength(0.0);
//            erpShipment.setPackageWidth(0.0);
//            erpShipment.setPacksgeOperator("");
////        erpShipment.setPackages(JSONObject.toJSONString(oOrderItems));
//            erpShipment.setRemark("");
//            erpShipment.setCreateBy(createBy);
//            erpShipment.setCreateTime(new Date());
//
//            shipmentMapper.insert(erpShipment);

            // 添加分配发货
            OOrderShipList shipList = new OOrderShipList();
            shipList.setShopId(erpOrder.getShopId());
            shipList.setShopType(erpOrder.getShopType());
            shipList.setShipper(1);
            shipList.setShipSupplierId(supplierId);
            shipList.setShipSupplier(supplierList.get(supplierId)!=null?supplierList.get(supplierId).getName():"");
            shipList.setOrderId(Long.parseLong(erpOrder.getId()));
            shipList.setOrderNum(erpOrder.getOrderNum());
            shipList.setStatus(0);
            shipList.setShipLogisticsCompany("");
            shipList.setShipLogisticsCompanyCode("");
            shipList.setShipLogisticsCode("");
            shipList.setShipStatus(1);
            shipList.setReceiverName(shipBo.getReceiverName());
            shipList.setReceiverMobile(shipBo.getReceiverMobile());
            shipList.setProvince(erpOrder.getProvince());
            shipList.setCity(erpOrder.getCity());
            shipList.setTown(erpOrder.getTown());
            shipList.setAddress(shipBo.getAddress());

            shipList.setRemark(erpOrder.getRemark());
            shipList.setSellerMemo(shipBo.getSellerMemo());
            shipList.setBuyerMemo(shipBo.getBuyerMemo());
            shipList.setCreateTime(new Date());
            shipList.setCreateBy("分配供应商发货");
            orderShipListMapper.insert(shipList);

            // 遍历 List<ErpOrderItem>
            for (OOrderItem item : orderItemList) {
                // 打印 List 中的每个 ErpOrderItem 对象
//                ErpShipmentItem erpShipmentItem = new ErpShipmentItem();
//                erpShipmentItem.setSupplierId(erpShipment.getSupplierId());
//                erpShipmentItem.setSupplier(erpShipment.getSupplier());
//                erpShipmentItem.setShipper(erpShipment.getShipper());
//                erpShipmentItem.setTenantId(erpShipment.getTenantId());
//                erpShipmentItem.setShopId(erpShipment.getShopId());
//                erpShipmentItem.setShopType(erpShipment.getShopType());
//                erpShipmentItem.setShipmentId(erpShipment.getId());
//                erpShipmentItem.setOrderId(erpShipment.getOrderId());
//                erpShipmentItem.setOrderNum(erpShipment.getOrderNum());
//                erpShipmentItem.setOrderTime(erpShipment.getOrderTime());
//                erpShipmentItem.setOrderItemId(item.getId());
//                erpShipmentItem.setErpGoodsId(item.getErpGoodsId());
//                erpShipmentItem.setErpSkuId(item.getErpSkuId());
//                erpShipmentItem.setGoodsTitle(item.getGoodsTitle());
//                erpShipmentItem.setGoodsNum(item.getGoodsNum());
//                erpShipmentItem.setGoodsImg(item.getGoodsImg());
//                erpShipmentItem.setGoodsSpec(item.getGoodsSpec());
//                erpShipmentItem.setSkuNum(item.getSkuNum());
//                erpShipmentItem.setQuantity(item.getQuantity());
//                erpShipmentItem.setRemark(item.getRemark());
//                erpShipmentItem.setStockStatus(0);
//                erpShipmentItem.setCreateBy(createBy);
//                erpShipmentItem.setCreateTime(new Date());
//                shipmentItemMapper.insert(erpShipmentItem);
                // 添加备货清单item
                OOrderShipListItem listItem=new OOrderShipListItem();
                listItem.setShopId(erpOrder.getShopId());
                listItem.setShopType(erpOrder.getShopType());
                listItem.setListId(shipList.getId());
                listItem.setShipper(shipList.getShipper());
                listItem.setShipSupplier(shipList.getShipSupplier());
                listItem.setShipSupplierId(shipList.getShipSupplierId());
                listItem.setOrderId(Long.parseLong(item.getOrderId()));
                listItem.setOrderItemId(Long.parseLong(item.getId()));
                listItem.setOrderNum(item.getOrderNum());
                listItem.setOriginalSkuId(item.getSkuId());
                listItem.setGoodsId(item.getGoodsId());
                listItem.setSkuId(item.getGoodsSkuId());
                listItem.setGoodsTitle(item.getGoodsTitle());
                listItem.setGoodsImg(item.getGoodsImg());
                listItem.setGoodsNum(item.getGoodsNum());
                listItem.setSkuName(item.getGoodsSpec());
                listItem.setSkuNum(item.getSkuNum());
                listItem.setQuantity(item.getQuantity());
                listItem.setStatus(0);//状态0待备货1备货中2备货完成3已发货
                listItem.setCreateBy("分配供应商发货");
                listItem.setCreateTime(new Date());
                orderShipListItemMapper.insert(listItem);

                // 更新订单item发货状态
                OOrderItem orderItemUpdate = new OOrderItem();
                orderItemUpdate.setId( item.getId());
                orderItemUpdate.setUpdateBy("分配供应商发货");
                orderItemUpdate.setUpdateTime(new Date());
                orderItemUpdate.setShipStatus(1);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
                orderItemUpdate.setShipper(2);//发货方式 0 自己发货1联合发货2供应商发货
                orderItemMapper.updateById(orderItemUpdate);
            }
        }

//        return ResultVo.error("还没有想好怎么实现！");

//
//        for(ErpOrderItem orderItem:oOrderItems){
//            ErpShipmentItem erpShipmentItem = new ErpShipmentItem();
//            erpShipmentItem.setShipper(erpShipment.getShipper());
//            erpShipmentItem.setTenantId(erpShipment.getTenantId());
//            erpShipmentItem.setShopId(erpShipment.getShopId());
//            erpShipmentItem.setShopType(erpShipment.getShopType());
//            erpShipmentItem.setShipmentId(erpShipment.getId());
//            erpShipmentItem.setOrderId(erpShipment.getOrderId());
//            erpShipmentItem.setOrderNum(erpShipment.getOrderNum());
//            erpShipmentItem.setOrderTime(erpShipment.getOrderTime());
//            erpShipmentItem.setOrderItemId(orderItem.getId());
//            erpShipmentItem.setErpGoodsId(orderItem.getErpGoodsId());
//            erpShipmentItem.setErpSkuId(orderItem.getErpSkuId());
//            erpShipmentItem.setGoodsTitle(orderItem.getGoodsTitle());
//            erpShipmentItem.setGoodsNum(orderItem.getGoodsNum());
//            erpShipmentItem.setGoodsImg(orderItem.getGoodsImg());
//            erpShipmentItem.setGoodsSpec(orderItem.getGoodsSpec());
//            erpShipmentItem.setSkuNum(orderItem.getSkuNum());
//            erpShipmentItem.setQuantity(orderItem.getQuantity());
//            erpShipmentItem.setRemark(orderItem.getRemark());
//            erpShipmentItem.setStockStatus(0);
//            erpShipmentItem.setCreateBy(createBy);
//            erpShipmentItem.setCreateTime(new Date());
//            shipmentItemMapper.insert(erpShipmentItem);
//        }
//
//
        // 更新状态、发货方式
        OOrder update = new OOrder();
        update.setId(erpOrder.getId());
        update.setShipStatus(1);//发货状态 0 待发货 1 已分配供应商发货 2全部发货
        update.setShipper(2);//发发货方式 0 自己发货1联合发货2供应商发货
        update.setUpdateTime(new Date());
        update.setUpdateBy("分配供应商发货");
        orderMapper.updateById(update);

        return ResultVo.success();
    }

    @Override
    public ResultVo cancelOrder(Long id, String cancelReason, String man) {
        OOrder shopOrder = this.baseMapper.selectById(id);
        if (shopOrder == null) return ResultVo.error("找不到订单数据");
        else if (shopOrder.getOrderStatus().intValue() == 3) return ResultVo.error("已完成的单不可以取消");

        // 取消订单
        OOrder update = new OOrder();
        update.setId(id.toString());
        update.setCancelReason(cancelReason);
        update.setUpdateBy(man + " 操作取消订单");
        update.setUpdateTime(new Date());
        update.setOrderStatus(11);
        this.baseMapper.updateById(update);

        // 更新子订单order_status字段值
        OOrderItem itemUpdate = new OOrderItem();
        itemUpdate.setOrderStatus(11);
        itemUpdate.setUpdateBy(update.getUpdateBy());
        itemUpdate.setUpdateTime(new Date());
        orderItemMapper.update(itemUpdate, new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, id));

        // 取消发货订单
//        List<OOrderStocking> oOrderStockings = oOrderStockingMapper.selectList(new LambdaQueryWrapper<OOrderStocking>().eq(OOrderStocking::getOOrderId, oOrder.getId()));
//        if (!oOrderStockings.isEmpty()) {
//            for (OOrderStocking oOrderStocking : oOrderStockings) {
//                OOrderStocking oOrderStockingUpdate = new OOrderStocking();
//                oOrderStockingUpdate.setId(oOrderStocking.getId());
//                oOrderStockingUpdate.setOrderStatus(11);
//                oOrderStockingUpdate.setUpdateBy("取消订单");
//                oOrderStockingUpdate.setUpdateTime(new Date());
//                oOrderStockingMapper.updateById(oOrderStockingUpdate);
//            }
//        }

        return ResultVo.success();
    }

    @Override
    public ResultVo cancelOrderItem(Long orderItemId, String cancelReason, String man) {
        OOrderItem shopOrderItem = orderItemMapper.selectById(orderItemId);
        if(shopOrderItem==null) return ResultVo.error("找不到子订单数据");
        else if(shopOrderItem.getRefundStatus().intValue()!=1) return ResultVo.error("售后中的子订单单不可以取消");

        OOrder shopOrder = this.baseMapper.selectById(shopOrderItem.getOrderId());
        if(shopOrder==null) return ResultVo.error("找不到订单数据");
        else if(shopOrder.getOrderStatus().intValue()==3) return ResultVo.error("已完成的单不可以取消");

        // 取消子订单
        OOrderItem orderItemUpdate = new OOrderItem();
        orderItemUpdate.setId(orderItemId.toString());
        orderItemUpdate.setRefundStatus(4);//售后完成
        orderItemUpdate.setUpdateBy("主动取消");
        orderItemUpdate.setUpdateTime(new Date());
        orderItemMapper.updateById(orderItemUpdate);


        // 判断子订单是否全部取消，全部取消把该订单也取消
        List<OOrderItem> offlineOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>()
                .eq(OOrderItem::getOrderId, shopOrder.getId())
                .eq(OOrderItem::getRefundStatus, 1));

        if(offlineOrderItems.isEmpty()) {
            // 全部退款了  ，， 更新订单状态为已关闭
            // 1、取消订单
            OOrder update = new OOrder();
            update.setId(shopOrder.getId());
            update.setCancelReason(cancelReason);
            update.setUpdateBy(man+" 操作取消子订单");
            update.setUpdateTime(new Date());
            update.setOrderStatus(11);
            this.baseMapper.updateById(update);



            // 取消发货订单
//            List<OOrderStocking> oOrderStockings = oOrderStockingMapper.selectList(new LambdaQueryWrapper<OOrderStocking>().eq(OOrderStocking::getOOrderId, oOrderUpdate.getId()));
//            if(!oOrderStockings.isEmpty()){
//                for(OOrderStocking oOrderStocking : oOrderStockings){
//                    OOrderStocking oOrderStockingUpdate = new OOrderStocking();
//                    oOrderStockingUpdate.setId(oOrderStocking.getId());
//                    oOrderStockingUpdate.setOrderStatus(11);
//                    oOrderStockingUpdate.setUpdateBy("取消子订单");
//                    oOrderStockingUpdate.setUpdateTime(new Date());
//                    oOrderStockingMapper.updateById(oOrderStockingUpdate);
//                }
//            }


        }

        return ResultVo.success();
    }
}




