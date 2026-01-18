package cn.qihangerp.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.JdOrderItem;
import cn.qihangerp.module.open.jd.service.JdOrderItemService;
import cn.qihangerp.module.open.jd.mapper.JdOrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【oms_jd_order_item(京东订单明细表)】的数据库操作Service实现
* @createDate 2025-05-19 22:37:54
*/
@Service
public class JdOrderItemServiceImpl extends ServiceImpl<JdOrderItemMapper, JdOrderItem>
    implements JdOrderItemService{

}




