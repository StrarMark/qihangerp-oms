package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpSalesPerson;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_sales_person(销售人员表)】的数据库操作Service
* @createDate 2026-04-09 20:41:48
*/
public interface ErpSalesPersonService extends IService<ErpSalesPerson> {
    PageResult<ErpSalesPerson> queryPageList(ErpSalesPerson bo, PageQuery pageQuery);
    ResultVo<Long> addSalesPerson(ErpSalesPerson erpSalesPerson);
    ResultVo<Long> update(ErpSalesPerson bo);
}
