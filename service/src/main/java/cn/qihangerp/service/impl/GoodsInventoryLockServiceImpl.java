package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.GoodsInventoryLock;
import cn.qihangerp.model.query.GoodsInventoryLockQuery;
import cn.qihangerp.mapper.GoodsInventoryLockMapper;
import cn.qihangerp.service.GoodsInventoryLockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * 库存锁定服务实现类
 * @author qihang
 * @date 2026-04-20
 */
@Service
public class GoodsInventoryLockServiceImpl extends ServiceImpl<GoodsInventoryLockMapper, GoodsInventoryLock> implements GoodsInventoryLockService {

    @Override
    public PageResult<GoodsInventoryLock> queryPageList(GoodsInventoryLockQuery query, PageQuery pageQuery) {
        Page<GoodsInventoryLock> page = pageQuery.build();
        QueryWrapper<GoodsInventoryLock> wrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getLockNo() != null) {
                wrapper.like("lock_no", query.getLockNo());
            }
            if (query.getSourceType() != null) {
                wrapper.eq("source_type", query.getSourceType());
            }
            if (query.getSourceId() != null) {
                wrapper.eq("source_id", query.getSourceId());
            }
            if (query.getBatchId() != null) {
                wrapper.eq("batch_id", query.getBatchId());
            }
            if (query.getStatus() != null) {
                wrapper.eq("status", query.getStatus());
            }
            if (query.getCreatedTimeStart() != null) {
                wrapper.ge("created_time", query.getCreatedTimeStart());
            }
            if (query.getCreatedTimeEnd() != null) {
                wrapper.le("created_time", query.getCreatedTimeEnd());
            }
        }
        wrapper.orderByDesc("created_time");
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String lockInventory(Long batchId, Integer quantity, Long sourceId, Integer sourceType) {
        String lockNo = "LOCK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        GoodsInventoryLock lock = new GoodsInventoryLock();
        lock.setLockNo(lockNo);
        lock.setSourceType(sourceType);
        lock.setSourceId(sourceId);
        lock.setBatchId(batchId);
        lock.setLockQuantity(quantity);
        lock.setStatus(1);

        save(lock);
        return lockNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseLock(String lockNo) {
        QueryWrapper<GoodsInventoryLock> wrapper = new QueryWrapper<>();
        wrapper.eq("lock_no", lockNo);
        GoodsInventoryLock lock = getOne(wrapper);
        if (lock == null) {
            return false;
        }

        lock.setStatus(2);
        return updateById(lock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoReleaseExpiredLocks() {
        QueryWrapper<GoodsInventoryLock> wrapper = new QueryWrapper<>();
        wrapper.lt("expire_time", new Date())
               .eq("status", 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addLock(GoodsInventoryLock lock) {
        if (lock.getLockNo() == null || lock.getLockNo().isEmpty()) {
            lock.setLockNo("LOCK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        }
        lock.setStatus(1);
        return save(lock);
    }
}
