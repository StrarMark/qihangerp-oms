package cn.qihangerp.module.open.dou.service.impl;

import cn.qihangerp.model.entity.OOrder;
import cn.qihangerp.module.open.dou.mapper.DouOOrderMapper;
import cn.qihangerp.module.open.dou.service.DouOOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.util.*;

/**
* @author qilip
* @description 针对表【o_order(订单表)】的数据库操作Service实现
* @createDate 2024-03-09 13:15:57
*/
@Slf4j
@AllArgsConstructor
@Service
public class DouOOrderServiceImpl extends ServiceImpl<DouOOrderMapper, OOrder>
    implements DouOOrderService {

}




