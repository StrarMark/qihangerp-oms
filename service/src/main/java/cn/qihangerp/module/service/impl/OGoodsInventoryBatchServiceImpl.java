package cn.qihangerp.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.OGoodsInventoryBatch;
import cn.qihangerp.module.service.OGoodsInventoryBatchService;
import cn.qihangerp.mapper.OGoodsInventoryBatchMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_goods_inventory_batch(商品库存批次)】的数据库操作Service实现
* @createDate 2024-09-23 22:39:50
*/
@AllArgsConstructor
@Service
public class OGoodsInventoryBatchServiceImpl extends ServiceImpl<OGoodsInventoryBatchMapper, OGoodsInventoryBatch>
    implements OGoodsInventoryBatchService{

    @Override
    public List<OGoodsInventoryBatch> selectGoodsInventoryBatchBySpecId(Long specId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<OGoodsInventoryBatch>().eq(OGoodsInventoryBatch::getSkuId, specId));
    }
}




