package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.ShopShare;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 门店共享服务接口
 * @author qihang
 * @date 2026-04-21
 */
public interface ShopShareService extends IService<ShopShare> {

    PageResult<ShopShare> queryPageList(PageQuery pageQuery);

    boolean addShare(ShopShare share);

    boolean updateShare(ShopShare share);

    boolean deleteShare(Long id);

    PageResult<Map<String, Object>> getMyApplications(Long shopId, PageQuery pageQuery);

    PageResult<Map<String, Object>> getPendingApprovals(Long shopId, PageQuery pageQuery);

    PageResult<Map<String, Object>> getShareHistory(Long shopId, PageQuery pageQuery);

    boolean approve(Long id);

    boolean reject(Long id);

    boolean cancel(Long id);

    List<Map<String, Object>> getAvailableShops();

    List<Map<String, Object>> getAuthorizedShops(Long shopId);

    PageResult<Map<String, Object>> querySharedInventory(Long shopId, PageQuery pageQuery, String keyword);
}