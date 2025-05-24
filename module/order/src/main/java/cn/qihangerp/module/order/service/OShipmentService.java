package cn.qihangerp.module.order.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.OShipment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_shipment(发货-发货记录表)】的数据库操作Service
* @createDate 2025-05-24 16:26:06
*/
public interface OShipmentService extends IService<OShipment> {
    PageResult<OShipment> queryPageList(OShipment shipping, PageQuery pageQuery);
}
