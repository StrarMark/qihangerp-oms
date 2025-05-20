package cn.qihangerp.module.open.wei.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.wei.domain.WeiRefund;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【oms_wei_refund(视频号小店退款)】的数据库操作Service
* @createDate 2024-06-20 17:07:27
*/
public interface WeiRefundService extends IService<WeiRefund> {
    PageResult<WeiRefund> queryPageList(WeiRefund bo, PageQuery pageQuery);
    ResultVo<Integer> saveRefund(Long shopId, WeiRefund refund);
}
