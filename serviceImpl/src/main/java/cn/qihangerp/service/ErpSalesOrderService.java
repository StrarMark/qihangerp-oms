package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpSalesOrder;
import cn.qihangerp.model.bo.ErpSalesOrderCreateBo;
import cn.qihangerp.model.bo.ErpSalesOrderH5CreateBo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.qihangerp.request.OrderSearchRequest;
import java.util.List;


/**
* @author qilip
* @description 针对表【offline_order(线下渠道订单表)】的数据库操作Service
* @createDate 2024-07-27 23:03:38
*/
public interface ErpSalesOrderService extends IService<ErpSalesOrder> {
    PageResult<ErpSalesOrder> queryPageList(OrderSearchRequest bo, PageQuery pageQuery);
    List<ErpSalesOrder> queryOrderList(OrderSearchRequest bo);

    ErpSalesOrder queryDetailById(Long id);

    /**
     * 手动添加订单
     * @param bo
     * @return
     */
    ResultVo<Long> insertOfflineOrder(ErpSalesOrderCreateBo bo, String createBy);

    /**
     * 取消订单
     * @param id 店铺订单id
     * @param cancelReason 取消原因
     * @param man 操作人
     * @return
     */
    ResultVo cancelOrder(Long id, String cancelReason, String man);

    /**
     * 取消子订单
     * @param orderItemId
     * @param cancelReason
     * @param man
     * @return
     */
    ResultVo cancelOrderItem(Long orderItemId, String cancelReason, String man);

    /**
     * H5内销订单创建（支持套餐+商品混合）
     * @param bo 订单信息
     * @param createBy 创建人
     * @return 创建结果
     */
    ResultVo<Long> insertOfflineOrderWithPackage(ErpSalesOrderH5CreateBo bo, String createBy, String userId);

    /**
     * H5内销订单列表查询
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 订单列表
     */
    PageResult<ErpSalesOrder> queryH5PageList(OrderSearchRequest bo, PageQuery pageQuery);
}
