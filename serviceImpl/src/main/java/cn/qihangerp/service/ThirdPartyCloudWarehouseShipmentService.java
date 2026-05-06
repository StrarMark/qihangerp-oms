package cn.qihangerp.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.request.ThirdPartyCloudWarehouseShipRequest;

/**
* 第三方云仓发货出库服务
*/
public interface ThirdPartyCloudWarehouseShipmentService {
    /**
     * 获取第三方云仓商品批次列表
     * @param warehouseId 仓库ID
     * @param goodsSkuId 商品SKU ID
     * @param quantity 需要的数量
     * @return 批次列表
     */
    ResultVo getBatches(Long warehouseId, Long goodsSkuId, Integer quantity);

    /**
     * 第三方云仓手动出库
     * @param userId 用户ID
     * @param userName 用户名
     * @param request 出库请求
     * @return 操作结果
     */
    ResultVo manualShipment(Long userId, String userName, ThirdPartyCloudWarehouseShipRequest request);
}