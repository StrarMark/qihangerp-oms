package cn.qihangerp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.GoodsInventoryTransfer;
import cn.qihangerp.model.query.GoodsInventoryTransferQuery;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 跨门店调拨申请服务接口
 * @author qihang
 * @date 2026-04-20
 */
public interface GoodsInventoryTransferService extends IService<GoodsInventoryTransfer> {

    /**
     * 分页查询调拨申请列表
     * @param query 查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<GoodsInventoryTransfer> queryPageList(GoodsInventoryTransferQuery query, PageQuery pageQuery);

    /**
     * 发起调拨申请
     * @param application 调拨申请信息
     * @return 是否成功
     */
    boolean applyTransfer(GoodsInventoryTransfer application);

    /**
     * 审批调拨申请
     * @param id 申请ID
     * @param approved 是否通过
     * @param remark 审批备注
     * @param operator 审批人
     * @return 是否成功
     */
    boolean approveTransfer(Long id, boolean approved, String remark, String operator);

    /**
     * 确认调拨出库
     * @param id 申请ID
     * @param operator 操作人
     * @return 是否成功
     */
    boolean confirmOutbound(Long id, String operator);

    /**
     * 确认调拨入库
     * @param id 申请ID
     * @param operator 操作人
     * @return 是否成功
     */
    boolean confirmInbound(Long id, String operator);

    /**
     * 取消调拨申请
     * @param id 申请ID
     * @return 是否成功
     */
    boolean cancelTransfer(Long id);
}
