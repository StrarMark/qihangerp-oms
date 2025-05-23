package cn.qihangerp.module.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OShipStockUp;
import cn.qihangerp.module.order.service.OShipStockUpService;
import cn.qihangerp.module.order.mapper.OShipStockUpMapper;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【o_ship_stock_up(发货-备货表（取号发货加入备货清单、分配供应商发货加入备货清单）)】的数据库操作Service实现
* @createDate 2025-05-23 21:43:16
*/
@Service
public class OShipStockUpServiceImpl extends ServiceImpl<OShipStockUpMapper, OShipStockUp>
    implements OShipStockUpService{

}




