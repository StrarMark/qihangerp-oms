package cn.qihangerp.module.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.OLogisticsCompany;
import cn.qihangerp.model.entity.OShop;
import cn.qihangerp.mapper.OLogisticsCompanyMapper;
import cn.qihangerp.mapper.OShopMapper;
import cn.qihangerp.model.entity.OShipment;
import cn.qihangerp.model.entity.OShipmentItem;
import cn.qihangerp.model.bo.OrderShipBo;
import cn.qihangerp.mapper.OShipmentItemMapper;
import cn.qihangerp.mapper.OShipmentMapper;
import cn.qihangerp.module.service.ErpShipmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
* @author qilip
* @description 针对表【erp_shipment(发货记录表)】的数据库操作Service实现
* @createDate 2025-06-01 23:22:40
*/
@AllArgsConstructor
@Service
public class ErpShipmentServiceImpl extends ServiceImpl<OShipmentMapper, OShipment>
    implements ErpShipmentService{
    private final OShipmentItemMapper shipmentItemMapper;
    private final OLogisticsCompanyMapper logisticsCompanyMapper;
    private final OShopMapper shopMapper;
    @Override
    public PageResult<OShipment> queryPageList(OShipment shipping, PageQuery pageQuery) {
        LambdaQueryWrapper<OShipment> queryWrapper = new LambdaQueryWrapper<OShipment>()
                .eq(shipping.getShipper()!=null, OShipment::getShipper,shipping.getShipper())
                .eq(shipping.getShipType()!=null, OShipment::getShipType,shipping.getShipType())
                .eq(StringUtils.hasText(shipping.getOrderNum()), OShipment::getOrderNum, shipping.getOrderNum())
                .eq(StringUtils.hasText(shipping.getWaybillCode()), OShipment::getWaybillCode, shipping.getWaybillCode())
                .eq(shipping.getShopId() != null, OShipment::getShopId, shipping.getShopId());

        Page<OShipment> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        if(pages.getRecords().size()>0){
            for(OShipment item : pages.getRecords()){
                item.setItemList(shipmentItemMapper.selectList(new LambdaQueryWrapper<OShipmentItem>()
                        .eq(OShipmentItem::getShipmentId,item.getId())));
            }
        }
        return PageResult.build(pages);
    }

    @Override
    public OShipment queryDetailById(Long id) {
        OShipment oShipment = this.baseMapper.selectById(id);
        if(oShipment !=null){
            oShipment.setItemList(shipmentItemMapper.selectList(new LambdaQueryWrapper<OShipmentItem>().eq(OShipmentItem::getShipmentId, oShipment.getId())));
        }
        return oShipment;
    }

    @Override
    public ResultVo<Long> addRecord(OrderShipBo shipping) {
        OLogisticsCompany oLogisticsCompany = logisticsCompanyMapper.selectById(shipping.getShipCompany());
        if(oLogisticsCompany==null){
            return ResultVo.error("快递公司不存在");
        }
        OShop oShop = shopMapper.selectById(shipping.getShopId());
        if(oShop==null){
            return ResultVo.error("店铺不存在");
        }

        OShipment shipment = new OShipment();
        shipment.setShipType(shipping.getShipType());
        shipment.setShopId(shipping.getShopId());
        shipment.setShopType(shipping.getShipType());
        shipment.setWaybillCode(shipping.getShipCode());
        shipment.setShipCompanyCode(oLogisticsCompany.getCode());
        shipment.setShipCompany(oLogisticsCompany.getName());
        shipment.setReceiverName(shipping.getReceiverName());
        shipment.setReceiverMobile(shipping.getReceiverMobile());
        shipment.setProvince(shipping.getProvince());
        shipment.setCity(shipping.getCity());
        shipment.setTown(shipping.getTown());
        shipment.setAddress(shipping.getAddress());
        shipment.setOrderNum(shipping.getOrderNum());
        shipment.setShipOperator(shipping.getShipOperator());
        shipment.setOrderId(0L);
        shipment.setShipper(0);
        shipment.setSupplierId(0L);
        shipment.setShipFee(BigDecimal.ZERO);
        shipment.setShipStatus(1);
        shipment.setCreateBy("手动添加发货记录");
        shipment.setCreateTime(new Date());
        this.baseMapper.insert(shipment);
        return ResultVo.success(shipment.getId());
    }
}




