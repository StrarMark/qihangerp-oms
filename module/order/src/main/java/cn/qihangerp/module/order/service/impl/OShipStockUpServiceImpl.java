package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.OShipStockUpItem;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OShipStockUp;
import cn.qihangerp.module.order.service.OShipStockUpService;
import cn.qihangerp.module.order.mapper.OShipStockUpMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【o_ship_stock_up(发货-备货表（取号发货加入备货清单、分配供应商发货加入备货清单）)】的数据库操作Service实现
* @createDate 2025-05-23 21:43:16
*/
@Service
public class OShipStockUpServiceImpl extends ServiceImpl<OShipStockUpMapper, OShipStockUp>
    implements OShipStockUpService{

    @Override
    public PageResult<OShipStockUp> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OShipStockUp> queryWrapper = new LambdaQueryWrapper<OShipStockUp>()
                .eq(OShipStockUp::getShipper,1)
                .eq(bo.getShopId()!=null,OShipStockUp::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OShipStockUp::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OShipStockUp::getOrderNum,bo.getOrderNum())
                ;
        Page<OShipStockUp> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




