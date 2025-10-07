package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.OLogisticsCompany;
import cn.qihangerp.model.entity.OOrder;
import cn.qihangerp.model.entity.OOrderItem;
import cn.qihangerp.module.mapper.OLogisticsCompanyMapper;
import cn.qihangerp.module.order.domain.OOrderShipListItem;
import cn.qihangerp.module.order.domain.OfflineOrder;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import cn.qihangerp.module.order.domain.bo.ShipStockUpCompleteBo;
import cn.qihangerp.module.order.domain.bo.SupplierOrderShipBo;
import cn.qihangerp.module.order.mapper.OOrderItemMapper;
import cn.qihangerp.module.order.mapper.OOrderMapper;
import cn.qihangerp.module.order.mapper.OOrderShipListItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OOrderShipList;
import cn.qihangerp.module.order.service.OOrderShipListService;
import cn.qihangerp.module.order.mapper.OOrderShipListMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【o_order_ship_list(发货-备货表（取号发货加入备货清单、分配供应商发货加入备货清单）)】的数据库操作Service实现
* @createDate 2025-05-24 16:03:35
*/
@Slf4j
@AllArgsConstructor
@Service
public class OOrderShipListServiceImpl extends ServiceImpl<OOrderShipListMapper, OOrderShipList>
    implements OOrderShipListService{
    private final OOrderShipListItemMapper shipListItemMapper;
    private final OOrderMapper orderMapper;
    private final OOrderItemMapper orderItemMapper;
    private final OLogisticsCompanyMapper logisticsCompanyMapper;

    @Override
    public PageResult<OOrderShipList> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrderShipList> queryWrapper = new LambdaQueryWrapper<OOrderShipList>()
                .eq(OOrderShipList::getShipper,1)
                .eq(bo.getShopId()!=null,OOrderShipList::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OOrderShipList::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OOrderShipList::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrderShipList> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        if(pages.getRecords()!=null && pages.getRecords().size()>0){
            for(OOrderShipList o : pages.getRecords()){
                o.setItems(shipListItemMapper.selectList(new LambdaQueryWrapper<OOrderShipListItem>().eq(OOrderShipListItem::getListId,o.getId())));
            }
        }

        return PageResult.build(pages);
    }

    /**
     * 备货完成 by Order
     * @param bo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int stockUpCompleteByOrder(ShipStockUpCompleteBo bo) {

        if(bo.getOrderNums() == null || bo.getOrderNums().length == 0) return -1;

        int total=0;
        // 循环判断状态
        for (String orderNum:bo.getOrderNums()) {
            List<OOrderShipList> oOrderShipLists = this.baseMapper.selectList(new LambdaQueryWrapper<OOrderShipList>().eq(OOrderShipList::getOrderNum, orderNum));
            if(oOrderShipLists == null || oOrderShipLists.size() == 0) continue;

            // 更新订单
            OOrderShipList update = new OOrderShipList();
            update.setId(oOrderShipLists.get(0).getId());
            update.setStatus(2);
            update.setUpdateBy("备货完成");
            update.setUpdateTime(new Date());
            this.baseMapper.updateById(update);

            List<OOrderShipListItem> upList = shipListItemMapper.selectList(new LambdaQueryWrapper<OOrderShipListItem>().eq(OOrderShipListItem::getListId,oOrderShipLists.get(0).getId()));
            if (upList != null) {
                for(OOrderShipListItem up : upList) {
                    if (up.getStatus() == 0 || up.getStatus() == 1) {
                        OOrderShipListItem updateItem = new OOrderShipListItem();
                        updateItem.setId(up.getId());
                        updateItem.setStatus(2);//备货完成
                        updateItem.setUpdateBy("备货完成");
                        updateItem.setUpdateTime(new Date());
                        shipListItemMapper.updateById(updateItem);
                    }
                }
            }
        }

        return 1;
    }

    /**
     * 备货完成
     * @param bo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int stockUpComplete(ShipStockUpCompleteBo bo) {

        if(bo.getIds() == null || bo.getIds().length == 0) return -1;

        int total=0;
        // 循环判断状态
        for (Long id:bo.getIds()) {
            OOrderShipListItem up = shipListItemMapper.selectById(id);
            if (up != null) {
                if (up.getStatus() == 0 || up.getStatus() == 1) {
                    OOrderShipListItem update = new OOrderShipListItem();
                    update.setId(id);
                    update.setStatus(2);//备货完成
                    update.setUpdateBy("备货完成");
                    update.setUpdateTime(new Date());
                    shipListItemMapper.updateById(update);
                }
                List<OOrderShipListItem> oOrderShipListItems = shipListItemMapper.selectList(new LambdaQueryWrapper<OOrderShipListItem>().eq(OOrderShipListItem::getListId, up.getListId()).eq(OOrderShipListItem::getStatus, 0));
                if(oOrderShipListItems == null || oOrderShipListItems.size() == 0) {
                    // 订单备货全部完成，更新订单状态
                    OOrderShipList listUpdate = new OOrderShipList();
                    listUpdate.setId(up.getListId());
                    listUpdate.setStatus(2);
                    listUpdate.setUpdateBy("备货完成");
                    listUpdate.setUpdateTime(new Date());
                    this.baseMapper.updateById(listUpdate);
                }
            }
        }

        return 1;
    }

    /**
     * 供应商发货手动确认
     * @param bo
     * @param operator
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Integer> supplierShipOrderManualLogistics(SupplierOrderShipBo bo, String operator) {
        if(bo.getId()==null) return ResultVo.error("缺少参数：Id");
        if(!StringUtils.hasText(bo.getLogisticsCompany()) || !StringUtils.hasText(bo.getLogisticsCode()))
            return ResultVo.error("缺少参数：快递信息");

        OOrderShipList shipOrder = this.baseMapper.selectById(bo.getId());
        if (shipOrder == null) return ResultVo.error("找不到数据");
        if(shipOrder.getShipStatus().intValue() !=1) return ResultVo.error("已发货或已取消不能再发货");

        OLogisticsCompany erpLogisticsCompany = logisticsCompanyMapper.selectById(bo.getLogisticsCompany());
        if(erpLogisticsCompany==null) return ResultVo.error("快递公司选择错误");

        OOrder erpOrder = orderMapper.selectById(shipOrder.getOrderId());
        if(erpOrder==null) return ResultVo.error("订单库找不到订单！");

        // 更新供应商订单状态
        OOrderShipList updateShip = new OOrderShipList();
        updateShip.setShipLogisticsCompany(erpLogisticsCompany.getName());
        updateShip.setShipLogisticsCompanyCode(erpLogisticsCompany.getCode());
        updateShip.setShipLogisticsCode(bo.getLogisticsCode());
        updateShip.setShipStatus(2);
        updateShip.setStatus(3);
        updateShip.setUpdateTime(new Date());
        updateShip.setUpdateBy("供应商手动发货");
        updateShip.setId(shipOrder.getId());
        this.baseMapper.updateById(updateShip);

        // 子订单
        List<OOrderShipListItem> shipOrderItemList = shipListItemMapper.selectList(
                new LambdaQueryWrapper<OOrderShipListItem>()
                        .eq(OOrderShipListItem::getListId, bo.getId()));
        if(!shipOrderItemList.isEmpty()){
            for (var item:shipOrderItemList) {
                // 更新子订单发货状态
                OOrderShipListItem shipOrderItem=new OOrderShipListItem();
                shipOrderItem.setStatus(3);
                shipOrderItem.setUpdateTime(new Date());
                shipOrderItem.setUpdateBy("供应商发货手动确认");
                shipOrderItem.setId(item.getId());
                shipListItemMapper.updateById(shipOrderItem);

                // 更新订单明细o_order_item
                OOrderItem updateOrderItem =new OOrderItem();
                updateOrderItem.setId(item.getOrderItemId().toString());
                updateOrderItem.setShipStatus(2);
                updateOrderItem.setOrderStatus(2);
                updateOrderItem.setUpdateBy("供应商发货手动确认");
                updateOrderItem.setUpdateTime(new Date());
                orderItemMapper.updateById(updateOrderItem);
            }
        }

        // 更新订单发货状态

        // 查询订单item是否全部发货
        List<OOrderItem> waitShipList = orderItemMapper.selectList(new LambdaQueryWrapper<OOrderItem>().eq(OOrderItem::getOrderId, shipOrder.getOrderId()).ne(OOrderItem::getShipStatus, 2));
        if(waitShipList.isEmpty()){
            //已经全部发货了
            OOrder update = new OOrder();
            update.setId(shipOrder.getOrderId().toString());
            update.setShipStatus(2);//发货状态 0 待发货 1 部分发货 2全部发货
            update.setOrderStatus(2);
            update.setShipCompany(erpLogisticsCompany.getName());
            update.setShipCode(bo.getLogisticsCode());
            update.setUpdateTime(new Date());
            update.setUpdateBy("供应商发货确认-全部发货完成");
            orderMapper.updateById(update);

            // 更新店铺订单（仅线下订单个螳螂订单）
            // 更新店铺订单


        }else {
            // 部分发货
            OOrder update = new OOrder();
            update.setId(shipOrder.getOrderId().toString());
            update.setShipStatus(1);//发货状态 0 待发货 1 部分发货 2全部发货
//            update.setOrderStatus(2);
            update.setShipCompany(erpLogisticsCompany.getName());
            update.setShipCode(bo.getLogisticsCode());
            update.setUpdateTime(new Date());
            update.setUpdateBy("供应商发货确认-部分发货");
            orderMapper.updateById(update);
        }
        log.info("============供应商发货确认成功===================");
        // 推送到店铺由controller进行操作
        return ResultVo.success();
    }
}




