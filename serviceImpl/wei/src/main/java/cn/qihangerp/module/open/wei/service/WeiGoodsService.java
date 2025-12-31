package cn.qihangerp.module.open.wei.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.WeiGoods;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_wei_goods】的数据库操作Service
* @createDate 2025-05-20 16:31:43
*/
public interface WeiGoodsService extends IService<WeiGoods> {
    PageResult<WeiGoods> queryPageList(WeiGoods bo, PageQuery pageQuery);
    int saveAndUpdateGoods(Long shopId, WeiGoods goods);
    ResultVo pushToOms(Long taoGoodsId);
}
