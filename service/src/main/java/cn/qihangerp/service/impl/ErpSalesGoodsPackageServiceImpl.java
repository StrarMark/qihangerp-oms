package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.mapper.ErpSalesGoodsPackageItemMapper;
import cn.qihangerp.mapper.ErpSalesGoodsPackageMapper;
import cn.qihangerp.model.entity.ErpSalesGoodsPackage;
import cn.qihangerp.model.entity.ErpSalesGoodsPackageItem;
import cn.qihangerp.model.entity.OGoodsSku;
import cn.qihangerp.service.ErpSalesGoodsPackageService;
import cn.qihangerp.service.OGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ErpSalesGoodsPackageServiceImpl extends ServiceImpl<ErpSalesGoodsPackageMapper, ErpSalesGoodsPackage> implements ErpSalesGoodsPackageService {

    private final ErpSalesGoodsPackageItemMapper packageItemMapper;
    private final OGoodsSkuService oGoodsSkuService;

    @Override
    public PageResult<ErpSalesGoodsPackage> queryPageList(PageQuery pageQuery) {
        Page<ErpSalesGoodsPackage> page = pageQuery.build();
        LambdaQueryWrapper<ErpSalesGoodsPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ErpSalesGoodsPackage::getCreateTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPackage(ErpSalesGoodsPackage pkg) {
        if (pkg.getPackageNo() == null || pkg.getPackageNo().isEmpty()) {
            pkg.setPackageNo(generatePackageNo());
        }
        pkg.setCreateTime(new Date());
        pkg.setStatus(1);
        return baseMapper.insert(pkg) > 0;
    }

    @Override
    public Long addPackageAndGetId(ErpSalesGoodsPackage pkg) {
        if (pkg.getPackageNo() == null || pkg.getPackageNo().isEmpty()) {
            pkg.setPackageNo(generatePackageNo());
        }
        pkg.setCreateTime(new Date());
        pkg.setStatus(1);
        baseMapper.insert(pkg);
        return pkg.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePackage(ErpSalesGoodsPackage pkg) {
        pkg.setUpdateTime(new Date());
        return baseMapper.updateById(pkg) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePackage(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public ErpSalesGoodsPackage getPackageById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<ErpSalesGoodsPackageItem> getPackageItems(Long packageId) {
        return baseMapper.selectItemsByPackageId(packageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean savePackageItems(Long packageId, List<ErpSalesGoodsPackageItem> items) {
        LambdaQueryWrapper<ErpSalesGoodsPackageItem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ErpSalesGoodsPackageItem::getPackageId, packageId);
        packageItemMapper.delete(deleteWrapper);

        for (ErpSalesGoodsPackageItem item : items) {
            // 查询SKU详情填充冗余字段
            if (item.getGoodsSkuId() != null) {
                OGoodsSku sku = oGoodsSkuService.getById(item.getGoodsSkuId().toString());
                if (sku != null) {
                    item.setGoodsName(sku.getGoodsName());
                    item.setSkuName(sku.getSkuName());
                    item.setSkuCode(sku.getSkuCode());
                    item.setSkuImage(sku.getColorImage());
                }
            }
            item.setPackageId(packageId);
            item.setCreateTime(new Date());
            packageItemMapper.insert(item);
        }
        return true;
    }

    private String generatePackageNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PKG" + date;
        Long count = this.count() + 1;
        return prefix + String.format("%04d", count);
    }
}