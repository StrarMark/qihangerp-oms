package cn.qihangerp.open.service.impl;

import cn.qihangerp.open.domain.ErpShipWaybill;
import cn.qihangerp.open.mapper.ErpShipWaybillMapper;
import cn.qihangerp.open.service.ErpShipWaybillService;
import cn.qihangerp.open.tao.domain.OmsTaoOrder;
import cn.qihangerp.open.tao.mapper.OmsTaoOrderMapper;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qihang.common.common.ResultVo;
import com.qihang.common.enums.EnumShopType;
import com.qihang.common.mq.MqMessage;
import com.qihang.common.mq.MqType;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【erp_ship_waybill(发货电子面单记录表)】的数据库操作Service实现
* @createDate 2024-06-16 17:18:22
*/
@AllArgsConstructor
@Service
public class ErpShipWaybillServiceImpl extends ServiceImpl<ErpShipWaybillMapper, ErpShipWaybill>
    implements ErpShipWaybillService {
    private final ErpShipWaybillMapper mapper;
    private final OmsTaoOrderMapper orderMapper;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    /**
     * 更新电子面单信息
     * @param shipWaybill
     * @return
     */
    @Transactional
    @Override
    public ResultVo<Integer> waybillUpdate(ErpShipWaybill shipWaybill) {
        String orderId= shipWaybill.getOrderId();
        if(shipWaybill.getId()!=null&&shipWaybill.getId()>0){
            // 存在，修改
            shipWaybill.setCreateBy(null);
            shipWaybill.setCreateTime(null);
            shipWaybill.setOrderId(null);
            shipWaybill.setShopId(null);
            shipWaybill.setShopType(null);
            mapper.updateById(shipWaybill);
        }else{
            // 新增
            List<ErpShipWaybill> erpShipWaybills = mapper.selectList(new LambdaQueryWrapper<ErpShipWaybill>().eq(ErpShipWaybill::getOrderId, orderId));
            if(erpShipWaybills==null|| erpShipWaybills.size()==0) {
                shipWaybill.setStatus(1);//已取号
                shipWaybill.setShopType(EnumShopType.TAO.getIndex());
                shipWaybill.setCreateTime(new Date());
                mapper.insert(shipWaybill);
            }else{
                ErpShipWaybill update = new ErpShipWaybill();
                update.setId(erpShipWaybills.get(0).getId());
                update.setWaybillCode(shipWaybill.getWaybillCode());
                update.setLogisticsCode(shipWaybill.getLogisticsCode());
                update.setPrintData(shipWaybill.getPrintData());
                update.setStatus(1);
                update.setUpdateTime(new Date());
                update.setUpdateBy("重新取号");
                mapper.updateById(update);
            }
        }
        // 更新关联订单erp_send_status状态
        OmsTaoOrder orderUpdate = new OmsTaoOrder();
        orderUpdate.setErpSendStatus(shipWaybill.getStatus());
        orderUpdate.setErpSendCode(shipWaybill.getWaybillCode());
        orderUpdate.setErpSendCompany(shipWaybill.getLogisticsCode());
        orderMapper.update(orderUpdate,new LambdaQueryWrapper<OmsTaoOrder>().eq(OmsTaoOrder::getTid,orderId));

        return ResultVo.success();
    }

    @Override
    public List<ErpShipWaybill> getListByOrderIds(Long shopId, String[] orderIds) {
        List<ErpShipWaybill> erpShipWaybills = mapper.selectList(
                new LambdaQueryWrapper<ErpShipWaybill>()
                        .eq(ErpShipWaybill::getShopId,shopId)
                        .in(ErpShipWaybill::getOrderId, Arrays.stream(orderIds).toList()));
        return erpShipWaybills;
    }

    @Transactional
    @Override
    public ResultVo<Integer> printSuccess(Long shopId, String[] orderIds) {
        List<ErpShipWaybill> erpShipWaybills = mapper.selectList(
                new LambdaQueryWrapper<ErpShipWaybill>()
                        .eq(ErpShipWaybill::getShopId,shopId)
                        .in(ErpShipWaybill::getOrderId, Arrays.stream(orderIds).toList()));
        if(erpShipWaybills!=null){
            for (var w : erpShipWaybills){
                if(w.getStatus()==1) {
                    ErpShipWaybill update = new ErpShipWaybill();
                    update.setId(erpShipWaybills.get(0).getId());
                    update.setStatus(2);
                    update.setUpdateTime(new Date());
                    update.setUpdateBy("打印面单");
                    mapper.updateById(update);

                    // 更新关联订单erp_send_status状态
                    OmsTaoOrder orderUpdate = new OmsTaoOrder();
                    orderUpdate.setErpSendStatus(update.getStatus());

                    orderMapper.update(orderUpdate, new LambdaQueryWrapper<OmsTaoOrder>().eq(OmsTaoOrder::getTid, w.getOrderId()));

                    //TODO: 打印成功之后 加入备货清单 采用kafka推送消息处理

                    // 打印完成，通知备货
                    kafkaTemplate.send(MqType.SHIP_STOCK_UP_MQ, JSONObject.toJSONString(MqMessage.build(w.getShopId(), w.getOrderId())));
                }
            }
        }
        return ResultVo.success();
    }

    @Transactional
    @Override
    public ResultVo<Integer> pushShipSend(Long shopId, String[] orderIds) {
        List<ErpShipWaybill> erpShipWaybills = mapper.selectList(
                new LambdaQueryWrapper<ErpShipWaybill>()
                        .eq(ErpShipWaybill::getShopId,shopId)
                        .in(ErpShipWaybill::getOrderId, Arrays.stream(orderIds).toList()));
        if(erpShipWaybills!=null){
            for (var w : erpShipWaybills){
                if(w.getStatus() > 0 && w.getStatus()<3) {
                    ErpShipWaybill update = new ErpShipWaybill();
                    update.setId(erpShipWaybills.get(0).getId());
                    update.setStatus(3);// 已发货
                    update.setUpdateTime(new Date());
                    update.setUpdateBy("电子面单发货");
                    mapper.updateById(update);

                    // 更新关联订单erp_send_status状态
                    OmsTaoOrder orderUpdate = new OmsTaoOrder();
                    orderUpdate.setErpSendStatus(update.getStatus());

                    orderMapper.update(orderUpdate, new LambdaQueryWrapper<OmsTaoOrder>().eq(OmsTaoOrder::getTid, w.getOrderId()));

                    // 更新erp_sale_order发货状态(controller层采用kafka推送消息处理)
                    // 发货完成，通知发货出库
                    kafkaTemplate.send(MqType.SHIP_SEND_MQ, JSONObject.toJSONString(MqMessage.build(w.getShopId(),w.getOrderId(),w.getLogisticsCode(),w.getWaybillCode())));
                }
            }
        }
        return ResultVo.success();
    }
}




