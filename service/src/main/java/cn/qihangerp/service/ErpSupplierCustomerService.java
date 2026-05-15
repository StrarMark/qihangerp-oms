package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpSupplierCustomer;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ErpSupplierCustomerService extends IService<ErpSupplierCustomer> {
    PageResult<ErpSupplierCustomer> queryPageList(ErpSupplierCustomer bo, PageQuery pageQuery);
    
    ResultVo updateStatus(Long id, Integer status);
}
