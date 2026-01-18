package cn.qihangerp.module.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.OShipment;
import cn.qihangerp.model.bo.OrderShipBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_shipment(发货记录表)】的数据库操作Service
* @createDate 2025-06-01 23:22:40
*/
public interface ErpShipmentService extends IService<OShipment> {
    PageResult<OShipment> queryPageList(OShipment shipping, PageQuery pageQuery);
    OShipment queryDetailById(Long id);
    ResultVo<Long> addRecord(OrderShipBo shipping);
}
