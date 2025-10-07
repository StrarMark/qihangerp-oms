package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.OOrderShipListItem;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import cn.qihangerp.module.order.domain.bo.ShipStockUpCompleteBo;
import cn.qihangerp.module.order.mapper.OOrderShipListItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OOrderShipList;
import cn.qihangerp.module.order.service.OOrderShipListService;
import cn.qihangerp.module.order.mapper.OOrderShipListMapper;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Service
public class OOrderShipListServiceImpl extends ServiceImpl<OOrderShipListMapper, OOrderShipList>
    implements OOrderShipListService{
    private final OOrderShipListItemMapper shipListItemMapper;
    @Override
    public PageResult<OOrderShipList> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrderShipList> queryWrapper = new LambdaQueryWrapper<OOrderShipList>()
                .eq(OOrderShipList::getShipper,1)
                .eq(bo.getShopId()!=null,OOrderShipList::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OOrderShipList::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OOrderShipList::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrderShipList> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

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
}




