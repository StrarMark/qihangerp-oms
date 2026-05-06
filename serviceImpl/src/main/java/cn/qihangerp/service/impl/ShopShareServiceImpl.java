package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.ErpWarehouseGoodsStock;
import cn.qihangerp.model.entity.ShopShare;
import cn.qihangerp.mapper.ErpWarehouseGoodsStockMapper;
import cn.qihangerp.mapper.ShopShareMapper;
import cn.qihangerp.service.ShopShareService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 门店共享服务实现类
 * @author qihang
 * @date 2026-04-21
 */
@Slf4j
@Service
@AllArgsConstructor
public class ShopShareServiceImpl extends ServiceImpl<ShopShareMapper, ShopShare> implements ShopShareService {
    private final ErpWarehouseGoodsStockMapper goodsStockMapper;

    @Override
    public PageResult<ShopShare> queryPageList(PageQuery pageQuery) {
        log.info("======查询门店共享列表======");
        LambdaQueryWrapper<ShopShare> queryWrapper = new LambdaQueryWrapper<ShopShare>()
                .orderByDesc(ShopShare::getCreatedTime);

        Page<ShopShare> page = baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(page);
    }

    @Override
    public boolean addShare(ShopShare share) {
        log.info("======添加门店共享======");
        LambdaQueryWrapper<ShopShare> queryWrapper = new LambdaQueryWrapper<ShopShare>()
                .eq(ShopShare::getFromShopId, share.getFromShopId())
                .eq(ShopShare::getToShopId, share.getToShopId());

        ShopShare existShare = baseMapper.selectOne(queryWrapper);
        if (existShare != null) {
            log.info("======更新门店共享======");
            share.setId(existShare.getId());
            return updateById(share);
        }
        log.info("======新增门店共享======");
        return save(share);
    }

    @Override
    public boolean updateShare(ShopShare share) {
        log.info("======修改门店共享======");
        return updateById(share);
    }

    @Override
    public boolean deleteShare(Long id) {
        log.info("======删除门店共享======");
        return removeById(id);
    }

    @Override
    public PageResult<Map<String, Object>> getMyApplications(Long shopId, PageQuery pageQuery) {
        log.info("======查询我的申请======");
        LambdaQueryWrapper<ShopShare> queryWrapper = new LambdaQueryWrapper<ShopShare>()
                .eq(shopId != null && shopId > 0, ShopShare::getToShopId, shopId)
                .orderByDesc(ShopShare::getCreatedTime);

        Page<ShopShare> result = baseMapper.selectPage(pageQuery.build(), queryWrapper);

        Page<Map<String, Object>> mapPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();
        for (ShopShare share : result.getRecords()) {
            records.add(convertToMap(share));
        }
        mapPage.setRecords(records);
        return PageResult.build(mapPage);
    }

    @Override
    public PageResult<Map<String, Object>> getPendingApprovals(Long shopId, PageQuery pageQuery) {
        log.info("======查询待我审批======");
        LambdaQueryWrapper<ShopShare> queryWrapper = new LambdaQueryWrapper<ShopShare>()
                .eq(shopId != null && shopId > 0, ShopShare::getFromShopId, shopId)
                .eq(ShopShare::getStatus, 0) // 只查询待审批的记录
                .orderByDesc(ShopShare::getCreatedTime);

        Page<ShopShare> result = baseMapper.selectPage(pageQuery.build(), queryWrapper);

        Page<Map<String, Object>> mapPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();
        for (ShopShare share : result.getRecords()) {
            records.add(convertToMap(share));
        }
        mapPage.setRecords(records);
        return PageResult.build(mapPage);
    }

    @Override
    public PageResult<Map<String, Object>> getShareHistory(Long shopId, PageQuery pageQuery) {
        log.info("======查询共享历史======");
        LambdaQueryWrapper<ShopShare> queryWrapper = new LambdaQueryWrapper<ShopShare>();
        if (shopId != null && shopId > 0) {
            queryWrapper.and(w -> w.eq(ShopShare::getFromShopId, shopId).or().eq(ShopShare::getToShopId, shopId));
        }
        queryWrapper.orderByDesc(ShopShare::getUpdatedTime);

        Page<ShopShare> result = baseMapper.selectPage(pageQuery.build(), queryWrapper);

        Page<Map<String, Object>> mapPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();
        for (ShopShare share : result.getRecords()) {
            records.add(convertToMap(share));
        }
        mapPage.setRecords(records);
        return PageResult.build(mapPage);
    }

    @Override
    public boolean approve(Long id) {
        log.info("======审批通过门店共享======");
        ShopShare share = getById(id);
        if (share == null) {
            log.warn("======门店共享记录不存在======");
            return false;
        }
        share.setStatus(1);
        return updateById(share);
    }

    @Override
    public boolean reject(Long id) {
        log.info("======拒绝门店共享======");
        ShopShare share = getById(id);
        if (share == null) {
            log.warn("======门店共享记录不存在======");
            return false;
        }
        share.setStatus(2);
        return updateById(share);
    }

    @Override
    public boolean cancel(Long id) {
        log.info("======取消门店共享======");
        ShopShare share = getById(id);
        if (share == null) {
            log.warn("======门店共享记录不存在======");
            return false;
        }
        share.setStatus(2);
        return updateById(share);
    }

    @Override
    public List<Map<String, Object>> getAvailableShops() {
        // 前端现在直接调用shop API获取店铺列表，此方法保留为兼容
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getAuthorizedShops(Long shopId) {
        log.info("======获取已授权门店列表======");
        LambdaQueryWrapper<ShopShare> queryWrapper = new LambdaQueryWrapper<ShopShare>()
                .eq(shopId != null && shopId > 0, ShopShare::getToShopId, shopId)
                .eq(ShopShare::getStatus, 1);

        List<ShopShare> shareList = baseMapper.selectList(queryWrapper);
        List<Map<String, Object>> shops = new ArrayList<>();
        Set<Long> shopIds = new HashSet<>();
        for (ShopShare share : shareList) {
            shopIds.add(share.getFromShopId());
        }
        
        for (Long id : shopIds) {
            Map<String, Object> shopMap = new HashMap<>();
            shopMap.put("id", id);
            shopMap.put("name", "门店" + id);
            shops.add(shopMap);
        }
        return shops;
    }

    @Override
    public PageResult<Map<String, Object>> querySharedInventory(Long shopId, PageQuery pageQuery, String keyword) {
        log.info("======查询共享库存======");
        // 1. 查询所有已授权的共享店铺（from_shop_id）
        LambdaQueryWrapper<ShopShare> shareQueryWrapper = new LambdaQueryWrapper<ShopShare>()
                .eq(ShopShare::getStatus, 1) // 只查询已通过的授权
                .eq(shopId != null && shopId > 0, ShopShare::getToShopId, shopId);

        List<ShopShare> shareList = baseMapper.selectList(shareQueryWrapper);
        if (shareList.isEmpty()) {
            return PageResult.build(new Page<>());
        }

        // 2. 提取所有授权的店铺ID
        Set<Long> authorizedShopIds = new HashSet<>();
        for (ShopShare share : shareList) {
            authorizedShopIds.add(share.getFromShopId());
        }

        // 3. 根据keyword查询这些店铺的库存
        Page<Map<String, Object>> mapPage = new Page<>(pageQuery .getPageNum(), pageQuery.getPageSize());
        List<Map<String, Object>> records = new ArrayList<>();

        for (Long authorizedShopId : authorizedShopIds) {
            // 构建库存查询条件
            LambdaQueryWrapper<ErpWarehouseGoodsStock> inventoryQueryWrapper = new LambdaQueryWrapper<ErpWarehouseGoodsStock>()
                    .eq(ErpWarehouseGoodsStock::getShopId, authorizedShopId)
                    .gt(ErpWarehouseGoodsStock::getUsableNum, 0); // 只查询有库存的商品

            // 如果有关键词，添加搜索条件
            if (StringUtils.hasText(keyword)) {
                inventoryQueryWrapper.and(w -> w
                        .like(ErpWarehouseGoodsStock::getGoodsId, keyword)
                        .or().like(ErpWarehouseGoodsStock::getGoodsNo, keyword)
                        .or().like(ErpWarehouseGoodsStock::getGoodsName, keyword)
                        .or().like(ErpWarehouseGoodsStock::getErpGoodsNo, keyword)
                );
            }

            // 执行查询
            List<ErpWarehouseGoodsStock> inventoryList = goodsStockMapper.selectList(inventoryQueryWrapper);
            if (!inventoryList.isEmpty()) {
                // 每个店铺只展示前5条库存信息
                int count = 0;
                for (ErpWarehouseGoodsStock inventory : inventoryList) {
                    if (count >= 5) break;
                    Map<String, Object> item = new HashMap<>();
                    item.put("shopId", authorizedShopId);
                    item.put("shopName", "门店" + authorizedShopId);
                    item.put("goodsId", inventory.getGoodsId());
                    item.put("goodsName", inventory.getGoodsName());
                    item.put("goodsNum", inventory.getErpGoodsNo());
                    item.put("skuId", inventory.getErpGoodsSkuId());
                    item.put("skuName", inventory.getErpGoodsSign());
                    item.put("skuCode", inventory.getErpGoodsNo());
                    item.put("quantity", inventory.getUsableNum());
                    item.put("warehouseId", inventory.getWarehouseId());
                    records.add(item);
                    count++;
                }
            }
        }

        // 4. 处理分页
        int start = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int end = Math.min(start + pageQuery.getPageSize(), records.size());
        List<Map<String, Object>> pageRecords = records.subList(start, end);
        mapPage.setRecords(pageRecords);
        mapPage.setTotal(records.size());

        return PageResult.build(mapPage);
    }

    private Map<String, Object> convertToMap(ShopShare share) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", share.getId());
        map.put("applyNo", "SHARE" + System.currentTimeMillis());
        map.put("fromShopId", share.getFromShopId());
        map.put("fromShopName", "门店" + share.getFromShopId());
        map.put("toShopId", share.getToShopId());
        map.put("targetShopName", "门店" + share.getToShopId());
        map.put("reason", share.getReason());
        map.put("status", share.getStatus());
        map.put("createdTime", share.getCreatedTime());
        map.put("approvedTime", share.getUpdatedTime());
        return map;
    }

    private Map<String, Object> createShopMap(Long id, String name) {
        Map<String, Object> shop = new HashMap<>();
        shop.put("id", id);
        shop.put("name", name);
        return shop;
    }
}