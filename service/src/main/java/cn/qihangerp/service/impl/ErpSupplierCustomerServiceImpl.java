package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.mapper.ErpSupplierCustomerMapper;
import cn.qihangerp.model.entity.ErpSupplierCustomer;
import cn.qihangerp.service.ErpSupplierCustomerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ErpSupplierCustomerServiceImpl extends ServiceImpl<ErpSupplierCustomerMapper, ErpSupplierCustomer>
        implements ErpSupplierCustomerService {

    @Override
    public PageResult<ErpSupplierCustomer> queryPageList(ErpSupplierCustomer bo, PageQuery pageQuery) {
        log.info("查询供应商客户列表: supplierId={}, status={}", bo.getSupplierId(), bo.getStatus());
        
        LambdaQueryWrapper<ErpSupplierCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(bo.getSupplierId() != null, ErpSupplierCustomer::getSupplierId, bo.getSupplierId());
        queryWrapper.eq(bo.getStatus() != null, ErpSupplierCustomer::getStatus, bo.getStatus());
        queryWrapper.like(bo.getShopName() != null && !bo.getShopName().isEmpty(),
                ErpSupplierCustomer::getShopName, bo.getShopName());
        queryWrapper.orderByDesc(ErpSupplierCustomer::getLastOrderTime);

        Page<ErpSupplierCustomer> page = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(page);
    }

    @Override
    public ResultVo updateStatus(Long id, Integer status) {
        log.info("更新供应商客户状态: id={}, status={}", id, status);
        
        ErpSupplierCustomer update = new ErpSupplierCustomer();
        update.setId(id);
        update.setStatus(status);
        
        this.updateById(update);
        return ResultVo.success();
    }
}
