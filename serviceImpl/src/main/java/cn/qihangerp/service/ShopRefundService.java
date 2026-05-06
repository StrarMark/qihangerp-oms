package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ShopRefund;
import cn.qihangerp.model.request.SaleOrderAfterAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shop_refund(视频号小店退款)】的数据库操作Service
* @createDate 2025-07-15 12:25:58
*/
public interface ShopRefundService extends IService<ShopRefund> {
    PageResult<ShopRefund> queryPageList(ShopRefund bo, PageQuery pageQuery);

    ResultVo<Long> saveRefund(Long shopId,ShopRefund bo);

    /**
     * 添加店铺订单售后
     * @param addRequest
     * @return
     */
    ResultVo<Long> addRefund(SaleOrderAfterAddRequest addRequest);
}
