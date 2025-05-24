package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OShipment;
import cn.qihangerp.module.order.service.OShipmentService;
import cn.qihangerp.module.order.mapper.OShipmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【o_shipment(发货-发货记录表)】的数据库操作Service实现
* @createDate 2025-05-24 16:26:06
*/
@Service
public class OShipmentServiceImpl extends ServiceImpl<OShipmentMapper, OShipment>
    implements OShipmentService{
    /**
     * 查询发货记录
     * @param shipping
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OShipment> queryPageList(OShipment shipping, PageQuery pageQuery) {
        LambdaQueryWrapper<OShipment> queryWrapper = new LambdaQueryWrapper<OShipment>()
                .likeRight(StringUtils.hasText(shipping.getOrderNums()), OShipment::getOrderNums, shipping.getOrderNums())
                .eq(StringUtils.hasText(shipping.getLogisticsCode()), OShipment::getLogisticsCode, shipping.getLogisticsCode())
                .eq(shipping.getShopId() != null, OShipment::getShopId, shipping.getShopId());

        Page<OShipment> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }
}




