 package cn.qihangerp.service.impl;

 import cn.qihangerp.common.PageQuery;
 import cn.qihangerp.common.PageResult;
 import cn.qihangerp.common.ResultVo;
 import cn.qihangerp.model.entity.ShopWaybillAccount;
 import cn.qihangerp.mapper.ShopWaybillAccountMapper;
 import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
 import cn.qihangerp.model.entity.ShopWaybillAccountShare;
 import cn.qihangerp.service.ShopWaybillAccountShareService;
 import cn.qihangerp.mapper.ShopWaybillAccountShareMapper;
 import lombok.AllArgsConstructor;
 import org.springframework.stereotype.Service;
 import org.springframework.util.StringUtils;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.stream.Collectors;

 /**
 * @author qilip
 * @description 针对表【erp_vendor_waybill_account(店铺电子面单账户信息表)】的数据库操作Service实现（已废弃）
 * @createDate 2025-06-15 12:25:35
 */
 @AllArgsConstructor
 @Service
 public class ShopWaybillAccountShareServiceImpl extends ServiceImpl<ShopWaybillAccountShareMapper, ShopWaybillAccountShare>
     implements ShopWaybillAccountShareService {
     private final ShopWaybillAccountMapper shopWaybillAccountMapper;
     @Override
     public PageResult<ShopWaybillAccountShare> queryVendorPageList(ShopWaybillAccountShare bo, PageQuery pageQuery) {
         LambdaQueryWrapper<ShopWaybillAccountShare> queryWrapper = new LambdaQueryWrapper<ShopWaybillAccountShare>()
                 .eq(ShopWaybillAccountShare::getShipperId, bo.getShipperId())
                 .eq(bo.getShopType()!=null, ShopWaybillAccountShare::getShopType, bo.getShopType())
                 .eq(bo.getShopId()!=null, ShopWaybillAccountShare::getShopId,bo.getShopId())
                 .eq(bo.getShipperType()!=null, ShopWaybillAccountShare::getShipperType, bo.getShipperType())
                 .eq(bo.getType()!=null, ShopWaybillAccountShare::getType,bo.getType())
                 .like(StringUtils.hasText(bo.getName()), ShopWaybillAccountShare::getName,bo.getName())
                 ;

         Page<ShopWaybillAccountShare> taoGoodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

         return PageResult.build(taoGoodsPage);
     }

     @Override
     public List<ShopWaybillAccountShare> queryVendorShopWaybillAccountList(Long shipperId, Long shopId) {
         LambdaQueryWrapper<ShopWaybillAccountShare> queryWrapper = new LambdaQueryWrapper<ShopWaybillAccountShare>()
                 .eq(ShopWaybillAccountShare::getShipperId, shipperId)
                 .eq(ShopWaybillAccountShare::getShopId,shopId);
         return this.baseMapper.selectList(queryWrapper);
     }

     @Override
     public PageResult<ShopWaybillAccountShare> queryShareVendorPageList(ShopWaybillAccountShare bo, PageQuery pageQuery) {
         LambdaQueryWrapper<ShopWaybillAccountShare> queryWrapper = new LambdaQueryWrapper<ShopWaybillAccountShare>()
                 .eq( bo.getMerchantId()!=null, ShopWaybillAccountShare::getMerchantId, bo.getMerchantId())
                 .eq(bo.getShopType()!=null, ShopWaybillAccountShare::getShopType, bo.getShopType())
                 .eq(bo.getShopId()!=null, ShopWaybillAccountShare::getShopId,bo.getShopId())
                 .eq(bo.getOriginAccountId()!=null, ShopWaybillAccountShare::getOriginAccountId,bo.getOriginAccountId())
                 .eq( ShopWaybillAccountShare::getType,1)
                 .like(StringUtils.hasText(bo.getName()), ShopWaybillAccountShare::getName,bo.getName())
                 ;

         Page<ShopWaybillAccountShare> taoGoodsPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

         return PageResult.build(taoGoodsPage);
     }

     @Override
     public ResultVo<Integer> syncAccountList(Long shopId, List<ShopWaybillAccountShare> accountList) {
         if(shopId==null) return ResultVo.error("缺少参数ShopId");
         if(accountList.isEmpty()) return ResultVo.error("没有数据");

         List<ShopWaybillAccountShare> list = this.baseMapper.selectList(
                 new LambdaQueryWrapper<ShopWaybillAccountShare>()
                         .eq(ShopWaybillAccountShare::getShopId, shopId));
         List<Long> accIds = new ArrayList<>();
         for(ShopWaybillAccountShare acc:accountList) {
             List<ShopWaybillAccountShare> collect = list.stream().filter(x -> x.getSiteCode().equals(acc.getSiteCode())).collect(Collectors.toList());
             if(collect.isEmpty()){
                 // 没有找到 新增
                 this.baseMapper.insert(acc);
                 accIds.add(acc.getId());
             }else {
                 // 存在更新
                 acc.setId(collect.get(0).getId());
                 this.baseMapper.updateById(acc);
                 accIds.add(acc.getId());
             }
         }
         // 删除已经不存在的数据
         this.baseMapper.delete(new LambdaQueryWrapper<ShopWaybillAccountShare>()
                 .notIn(ShopWaybillAccountShare::getId,accIds)
                 .eq(ShopWaybillAccountShare::getShopId,shopId)
         );
         return ResultVo.success();
     }

     @Override
     public ResultVo shareShipper(Long accountId, Integer shipperType, Long shipperId,String shipperName) {
         if (accountId == null || accountId == 0) return ResultVo.error("缺少参数：id");
         if (shipperType == null || shipperType == 0) return ResultVo.error("缺少参数：shipperType");
         if (shipperId == null || shipperId == 0) return ResultVo.error("缺少参数：shipperId");

         ShopWaybillAccount waybillAccount = shopWaybillAccountMapper.selectById(accountId);
         if (waybillAccount == null) {
             return ResultVo.error("没有找到面单信息");
         }
         if(shipperType==10){
             //共享给云仓库

         }

         // 查询电子面单是否共享过
         List<ShopWaybillAccountShare> list = this.baseMapper.selectList(new LambdaQueryWrapper<ShopWaybillAccountShare>()
                 .eq(ShopWaybillAccountShare::getShipperId, shipperId)
                 .eq(ShopWaybillAccountShare::getMerchantId, waybillAccount.getMerchantId())
                 .eq(ShopWaybillAccountShare::getOriginAccountId, waybillAccount.getId())
         );
         if (list == null || list.size() == 0) {
             ShopWaybillAccountShare vendorWaybillAccount = new ShopWaybillAccountShare();
             vendorWaybillAccount.setType(1);
             vendorWaybillAccount.setMerchantId(waybillAccount.getMerchantId());
             vendorWaybillAccount.setShipperType(shipperType);
             vendorWaybillAccount.setShipperId(shipperId);
             vendorWaybillAccount.setShipperName(shipperName);
             vendorWaybillAccount.setOriginAccountId(waybillAccount.getId().longValue());
             vendorWaybillAccount.setIsShow(1);
             vendorWaybillAccount.setShopId(waybillAccount.getShopId());
             vendorWaybillAccount.setShopType(waybillAccount.getShopType());
             vendorWaybillAccount.setSellerShopId(waybillAccount.getShopId().toString());
             vendorWaybillAccount.setDeliveryId(waybillAccount.getRefLogisticsCode());
 //            vendorWaybillAccount.setCompanyType(waybillAccount.getCompanyType());
             vendorWaybillAccount.setSiteCode(waybillAccount.getBranchCode());
             vendorWaybillAccount.setSiteName(waybillAccount.getBranchName());
             vendorWaybillAccount.setAvailable(waybillAccount.getNum().longValue());
             vendorWaybillAccount.setAllocated(0L);
             vendorWaybillAccount.setCancel(0L);
             vendorWaybillAccount.setRecycled(0L);
             vendorWaybillAccount.setSenderProvince(waybillAccount.getProvince());
             vendorWaybillAccount.setSenderCity(waybillAccount.getCity());
             vendorWaybillAccount.setSenderCounty(waybillAccount.getDistrict());
             vendorWaybillAccount.setSenderStreet(waybillAccount.getCountrysideName());
             vendorWaybillAccount.setSenderAddress(waybillAccount.getAddressAddress());
             vendorWaybillAccount.setName(waybillAccount.getDeliverName());
             vendorWaybillAccount.setMobile(waybillAccount.getDeliverMobile());
             vendorWaybillAccount.setPhone(waybillAccount.getDeliverPhone());
             vendorWaybillAccount.setTemplateUrl(waybillAccount.getTemplateUrl());
             this.baseMapper.insert(vendorWaybillAccount);
             return ResultVo.success();
         }else{
             return ResultVo.error("已经共享过了");
         }
     }
 }
