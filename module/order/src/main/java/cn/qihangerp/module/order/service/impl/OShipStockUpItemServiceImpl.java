package cn.qihangerp.module.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OShipStockUpItem;
import cn.qihangerp.module.order.service.OShipStockUpItemService;
import cn.qihangerp.module.order.mapper.OShipStockUpItemMapper;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【o_ship_stock_up_item(发货-备货表（打单加入备货清单）)】的数据库操作Service实现
* @createDate 2025-05-23 21:43:16
*/
@Service
public class OShipStockUpItemServiceImpl extends ServiceImpl<OShipStockUpItemMapper, OShipStockUpItem>
    implements OShipStockUpItemService{

}




