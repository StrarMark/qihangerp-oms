package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpWarehouseShop;
import cn.qihangerp.service.ErpWarehouseShopService;
import cn.qihangerp.mapper.ErpWarehouseShopMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_cloud_warehouse_shop】的数据库操作Service实现
* @createDate 2025-07-07 21:22:47
*/
@Service
public class ErpWarehouseShopServiceImpl extends ServiceImpl<ErpWarehouseShopMapper, ErpWarehouseShop>
    implements ErpWarehouseShopService {

    @Override
    public PageResult<ErpWarehouseShop> queryPageList(ErpWarehouseShop query,  PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWarehouseShop> qw = new LambdaQueryWrapper<ErpWarehouseShop>()
                .eq(query.getMerchantId()!=null ,ErpWarehouseShop::getMerchantId, query.getMerchantId())
                .eq(query.getStatus() != null, ErpWarehouseShop::getStatus, query.getStatus())
                .eq(query.getShopType() != null, ErpWarehouseShop::getShopType, query.getShopType())
                .eq(query.getWarehouseId() != null, ErpWarehouseShop::getWarehouseId, query.getWarehouseId())
                ;
        Page<ErpWarehouseShop> pages = this.baseMapper.selectPage(pageQuery.build(), qw);
        return PageResult.build(pages);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo saveErpCloudWarehouseShop(List<ErpWarehouseShop> shops) {
        for (ErpWarehouseShop shop : shops) {
            List<ErpWarehouseShop> erpCloudWarehouses = this.baseMapper.selectList(
                    new LambdaQueryWrapper<ErpWarehouseShop>()
                            .eq(ErpWarehouseShop::getShopNo, shop.getShopNo())
                            .eq(shop.getMerchantId()!=null, ErpWarehouseShop::getMerchantId, shop.getMerchantId())
            );
            if (erpCloudWarehouses != null && erpCloudWarehouses.size() > 0) {
                shop.setId(erpCloudWarehouses.get(0).getId());
                this.baseMapper.updateById(shop);
            } else {
                this.baseMapper.insert(shop);
            }
        }

//        List<Long> ids = shops.stream().map(ErpWarehouseShop::getId).toList();
//        this.baseMapper.delete(new LambdaQueryWrapper<ErpWarehouseShop>()
//                .eq(ErpWarehouseShop::getMerchantId, shops.get(0).getMerchantId())
//                .notIn(ErpWarehouseShop::getId,ids)
//        );
        return ResultVo.success();
    }

    @Override
    public ResultVo saveErpCloudWarehouseShop(ErpWarehouseShop shop) {
        List<ErpWarehouseShop> erpCloudWarehouses = this.baseMapper.selectList(
                new LambdaQueryWrapper<ErpWarehouseShop>()
                        .eq(ErpWarehouseShop::getShopNo, shop.getShopNo())
                        .eq(shop.getMerchantId()!=null, ErpWarehouseShop::getMerchantId, shop.getMerchantId())
        );
        if (erpCloudWarehouses != null && erpCloudWarehouses.size() > 0) {
            shop.setId(erpCloudWarehouses.get(0).getId());
            this.baseMapper.updateById(shop);
        } else {
            this.baseMapper.insert(shop);
        }
        return ResultVo.success();
    }

    @Override
    public List<ErpWarehouseShop> getWarehouseShopList(Long warehouseId,Integer shopType,Long merchantId,String ownerNo) {
        List<ErpWarehouseShop> erpCloudWarehouses = this.baseMapper.selectList(new LambdaQueryWrapper<ErpWarehouseShop>()
                        .eq(warehouseId!=null, ErpWarehouseShop::getWarehouseId, warehouseId)
                        .eq(shopType!=null, ErpWarehouseShop::getShopType, shopType)
                .eq(merchantId!=null, ErpWarehouseShop::getMerchantId, merchantId)
                .eq(StringUtils.hasText(ownerNo),ErpWarehouseShop::getOwnerNo, ownerNo)
        );
        return erpCloudWarehouses;
    }
}




