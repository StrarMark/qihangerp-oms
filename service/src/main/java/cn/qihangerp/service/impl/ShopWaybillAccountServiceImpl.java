package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.service.ShopWaybillAccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ShopWaybillAccount;
import cn.qihangerp.mapper.ShopWaybillAccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author 1
* @description 针对表【oms_diansan_waybill_branch(点三电子面单物流网点表)】的数据库操作Service实现
* @createDate 2025-10-12 17:35:35
*/
@Service
public class ShopWaybillAccountServiceImpl extends ServiceImpl<ShopWaybillAccountMapper, ShopWaybillAccount>
    implements ShopWaybillAccountService {

    @Override
    public PageResult<ShopWaybillAccount> queryPageList(ShopWaybillAccount branch, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopWaybillAccount> queryWrapper = new LambdaQueryWrapper<ShopWaybillAccount>()
                .eq(branch.getSupportOffline()!=null, ShopWaybillAccount::getSupportOffline,branch.getSupportOffline())
                .eq(branch.getMerchantId()!=null, ShopWaybillAccount::getMerchantId,branch.getMerchantId())
                .eq(StringUtils.hasText(branch.getOuterLogisticsId()), ShopWaybillAccount::getOuterLogisticsId,branch.getOuterLogisticsId())
                .eq(branch.getShopId()!=null, ShopWaybillAccount::getShopId,branch.getShopId())
                .eq(branch.getShopType()!=null, ShopWaybillAccount::getShopType,branch.getShopType())
                .eq(StringUtils.hasText(branch.getProviderCode()), ShopWaybillAccount::getProviderCode,branch.getProviderCode())
                .eq(StringUtils.hasText(branch.getProviderName()), ShopWaybillAccount::getProviderName,branch.getProviderName())
               ;

        Page<ShopWaybillAccount> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }

    @Override
    public List<ShopWaybillAccount> queryList(Long merchantId, Long shopId, String cpCode) {
        LambdaQueryWrapper<ShopWaybillAccount> queryWrapper = new LambdaQueryWrapper<ShopWaybillAccount>()
                .eq(merchantId!=null, ShopWaybillAccount::getMerchantId, merchantId)
                .eq(shopId!=null, ShopWaybillAccount::getShopId, shopId)
                .eq(StringUtils.hasText(cpCode), ShopWaybillAccount::getProviderCode, cpCode);
        List<ShopWaybillAccount> shopWaybillAccounts = this.baseMapper.selectList(queryWrapper);
        return shopWaybillAccounts;
    }

    @Override
    public ResultVo<Long> saveAndUpdate(ShopWaybillAccount branch) {
        List<ShopWaybillAccount> branches = this.baseMapper.selectList(new LambdaQueryWrapper<ShopWaybillAccount>()
                .eq(StringUtils.hasText(branch.getKey1()), ShopWaybillAccount::getKey1, branch.getKey1())
                .eq(ShopWaybillAccount::getMerchantId, branch.getMerchantId())
                .eq(ShopWaybillAccount::getShopId, branch.getShopId())
                .eq(ShopWaybillAccount::getBranchCode, branch.getBranchCode())
        );
        if(branches==null || branches.size()==0){
            this.baseMapper.insert(branch);
        }else{
            branch.setId(branches.get(0).getId());
            this.baseMapper.updateById(branch);
        }
        return ResultVo.success();
    }
}




