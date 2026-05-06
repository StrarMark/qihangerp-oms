package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpSalesPerson;
import cn.qihangerp.service.ErpSalesPersonService;
import cn.qihangerp.mapper.ErpSalesPersonMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 1
* @description 针对表【erp_sales_person(销售人员表)】的数据库操作Service实现
* @createDate 2026-04-09 20:41:48
*/
@Service
public class ErpSalesPersonServiceImpl extends ServiceImpl<ErpSalesPersonMapper, ErpSalesPerson>
    implements ErpSalesPersonService{

    @Override
    public PageResult<ErpSalesPerson> queryPageList(ErpSalesPerson bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpSalesPerson> queryWrapper = new LambdaQueryWrapper<ErpSalesPerson>()
                .eq(bo.getStatus()!=null, ErpSalesPerson::getStatus, bo.getStatus())
                .eq(bo.getMerchantId()!=null, ErpSalesPerson::getMerchantId, bo.getMerchantId())
                .eq(bo.getShopId()!=null, ErpSalesPerson::getShopId, bo.getShopId());

        Page<ErpSalesPerson> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ResultVo<Long> addSalesPerson(ErpSalesPerson erpSalesPerson) {
        List<ErpSalesPerson> erpSalesPeople = this.baseMapper.selectList(new LambdaQueryWrapper<ErpSalesPerson>().eq(ErpSalesPerson::getMobile, erpSalesPerson.getMobile()));
        if(!erpSalesPeople.isEmpty()) return ResultVo.error("手机号存在");
        erpSalesPeople = this.baseMapper.selectList(new LambdaQueryWrapper<ErpSalesPerson>().eq(ErpSalesPerson::getEmployeeNo, erpSalesPerson.getEmployeeNo()));
        if(!erpSalesPeople.isEmpty()) return ResultVo.error("工号存在");

        erpSalesPerson.setStatus(1);
        erpSalesPerson.setCreatedTime(new Date());
        this.baseMapper.insert(erpSalesPerson);
        return ResultVo.success(erpSalesPerson.getId());
    }

    @Override
    public ResultVo<Long> update(ErpSalesPerson bo) {
        if(bo.getId()==null) return ResultVo.error("ID不能为空");
        bo.setUpdatedTime(new Date());
        this.baseMapper.updateById(bo);
        return ResultVo.success(bo.getId());
    }
}




