package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.GoodsInventoryLock;
import cn.qihangerp.model.entity.GoodsInventoryTransfer;
import cn.qihangerp.model.query.GoodsInventoryTransferQuery;
import cn.qihangerp.mapper.GoodsInventoryTransferMapper;
import cn.qihangerp.service.GoodsInventoryLockService;
import cn.qihangerp.service.GoodsInventoryTransferService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * 跨门店调拨申请服务实现类
 * @author qihang
 * @date 2026-04-20
 */
@Service
@RequiredArgsConstructor
public class GoodsInventoryTransferServiceImpl extends ServiceImpl<GoodsInventoryTransferMapper, GoodsInventoryTransfer> implements GoodsInventoryTransferService {

    private final GoodsInventoryLockService goodsInventoryLockService;

    @Override
    public PageResult<GoodsInventoryTransfer> queryPageList(GoodsInventoryTransferQuery query, PageQuery pageQuery) {
        Page<GoodsInventoryTransfer> page = pageQuery.build();
        QueryWrapper<GoodsInventoryTransfer> wrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getApplyNo() != null) {
                wrapper.like("apply_no", query.getApplyNo());
            }
            if (query.getFromShopId() != null) {
                wrapper.eq("from_shop_id", query.getFromShopId());
            }
            if (query.getToShopId() != null) {
                wrapper.eq("to_shop_id", query.getToShopId());
            }
            if (query.getStatus() != null) {
                wrapper.eq("status", query.getStatus());
            }
            if (query.getCreatedBy() != null) {
                wrapper.like("created_by", query.getCreatedBy());
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
    public boolean applyTransfer(GoodsInventoryTransfer application) {
        application.setApplyNo(generateApplyNo());
        application.setStatus(1);
        return save(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveTransfer(Long id, boolean approved, String remark, String operator) {
        GoodsInventoryTransfer application = getById(id);
        if (application == null) {
            return false;
        }

        if (approved) {
            application.setStatus(2);
            String lockNo = goodsInventoryLockService.lockInventory(
                application.getBatchId(),
                application.getApplyQuantity(),
                id,
                1
            );
            if (lockNo == null) {
                return false;
            }
        } else {
            application.setStatus(3);
        }

        application.setApprovalRemark(remark);
        application.setApprovedBy(operator);
        application.setApprovedTime(new Date());
        return updateById(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmOutbound(Long id, String operator) {
        GoodsInventoryTransfer application = getById(id);
        if (application == null || application.getStatus() != 2) {
            return false;
        }

        application.setStatus(4);
        application.setOutboundTime(new Date());
        return updateById(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmInbound(Long id, String operator) {
        GoodsInventoryTransfer application = getById(id);
        if (application == null || application.getStatus() != 4) {
            return false;
        }

        application.setStatus(5);
        application.setInboundTime(new Date());
        return updateById(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTransfer(Long id) {
        GoodsInventoryTransfer application = getById(id);
        if (application == null) {
            return false;
        }

        if (application.getStatus() == 2) {
            QueryWrapper<GoodsInventoryLock> wrapper = new QueryWrapper<>();
            wrapper.eq("source_id", id)
                   .eq("source_type", 1);
            GoodsInventoryLock lock = goodsInventoryLockService.getOne(wrapper);
            if (lock != null) {
                goodsInventoryLockService.releaseLock(lock.getLockNo());
            }
        }

        application.setStatus(5);
        return updateById(application);
    }

    private String generateApplyNo() {
        return "TF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
