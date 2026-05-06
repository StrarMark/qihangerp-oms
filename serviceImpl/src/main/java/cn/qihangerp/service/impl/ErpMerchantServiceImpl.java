package cn.qihangerp.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.bo.MerchantAddBo;
import cn.qihangerp.model.query.MerchantQuery;
import cn.qihangerp.model.bo.ShareCloudWarehouseToMerchantWarehouse;
import cn.qihangerp.model.entity.ErpWarehouseMerchant;
import cn.qihangerp.mapper.ErpWarehouseMerchantMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpMerchant;
import cn.qihangerp.service.ErpMerchantService;
import cn.qihangerp.mapper.ErpMerchantMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_tenant(租户用户表)】的数据库操作Service实现
* @createDate 2024-06-23 11:10:08
*/
@AllArgsConstructor
@Service
public class ErpMerchantServiceImpl extends ServiceImpl<ErpMerchantMapper, ErpMerchant>
    implements ErpMerchantService {
    private final ErpMerchantMapper mapper;
//    private final ErpVendorMapper vendorMapper;
    private final ErpWarehouseMerchantMapper warehouseMerchantMapper;
//    private final ErpMerchantSupplierService merchantSupplierService;


    @Override
    public PageResult<ErpMerchant> queryPageList(MerchantQuery bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpMerchant> queryWrapper = new LambdaQueryWrapper<ErpMerchant>()
                .eq(bo.getStatus()!=null,ErpMerchant::getStatus,bo.getStatus())
                .like(StringUtils.hasText(bo.getNumber()),ErpMerchant::getNumber,bo.getNumber())
                .like(StringUtils.hasText(bo.getUsci()),ErpMerchant::getUsci,bo.getUsci())
                .like(StringUtils.hasText(bo.getMobile()),ErpMerchant::getMobile,bo.getMobile())
                .eq(bo.getMerchantId()!=null,ErpMerchant::getId,bo.getMerchantId())
                .like(StringUtils.hasText(bo.getName()),ErpMerchant::getName,bo.getName());

        Page<ErpMerchant> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ErpMerchant selectUserByUserName(String userName) {
        List<ErpMerchant> scmDistributors = mapper.selectList(new LambdaQueryWrapper<ErpMerchant>()
                .eq(ErpMerchant::getLoginName, userName)
                .eq(ErpMerchant::getDelFlag, "0"));
        if(scmDistributors == null || scmDistributors.size()==0)
            return null;
        else
            return scmDistributors.get(0);
    }

    @Override
    public void updateByUserId(ErpMerchant tenant, Long userId) {
        tenant.setId(userId);
        mapper.updateById(tenant);
//        mapper.update(tenant,new LambdaQueryWrapper<OmsTenant>().eq(OmsTenant::getId,userId));
    }

    @Override
    public ResultVo<ErpMerchant> add(MerchantAddBo bo, String createBy) {
        ErpMerchant merchant = new ErpMerchant();
        BeanUtils.copyProperties(bo,merchant);
        merchant.setPassword(bo.getLoginPwd());
        merchant.setStatus("0");
        merchant.setDelFlag("0");
//        merchant.setLoginIp(IpUtils.getIpAddr());
//        merchant.setLoginDate(new Date());
        merchant.setCreateBy(createBy);
        merchant.setCreateTime(new Date());
        mapper.insert(merchant);
        return ResultVo.success(merchant);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<ErpMerchant> setLoginName(Long id,MerchantAddBo bo, String updateBy) {
        ErpMerchant merchant = mapper.selectById(id);
        if(merchant==null) return ResultVo.error("数据不存在");
         if(!merchant.getLoginName().equals(bo.getLoginName())){
             // 新登陆账号
             ErpMerchant merchant1 = this.selectUserByUserName(bo.getLoginName());
             if(merchant1!=null) return ResultVo.error("登录账号已存在，请重新输入");
         }
         ErpMerchant up = new ErpMerchant();
         up.setId(id);
         up.setLoginName(bo.getLoginName());
         up.setPassword(bo.getLoginPwd());
         up.setUpdateBy(updateBy);
         up.setUpdateTime(new Date());
         mapper.updateById(up);


        return ResultVo.success(merchant);
    }


    /**
     * 分配云仓给商户
     * @param id 商户id
     * @param warehouseList 云仓list
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo shareCloudWarehouse(Long id, List<ShareCloudWarehouseToMerchantWarehouse> warehouseList) {

        ErpMerchant erpMerchant = mapper.selectById(id);
        if(erpMerchant==null){
            return ResultVo.error("没有找到商户");
        }


        if(warehouseList.isEmpty()){
            // 删除 该商户 的所有云仓

            // 删除 云仓 商户数据
            warehouseMerchantMapper.delete(new LambdaQueryWrapper<ErpWarehouseMerchant>()
                    .eq(ErpWarehouseMerchant::getMerchantId, erpMerchant.getId())
            );


            // 删除商户供应商（云仓） -- old
//            merchantSupplierService.remove(new LambdaQueryWrapper<ErpMerchantSupplier>()
//                    .eq(ErpMerchantSupplier::getMerchantId, erpMerchant.getId())
//                    .eq(ErpMerchantSupplier::getType,1)
//            );

            // 更新自己
            ErpMerchant update = new ErpMerchant();
            update.setId(id);
            update.setSupplierIds("");
            mapper.updateById(update);
            return ResultVo.success();
        }else {
            // 更新商户的云仓数据
            List<Long> newCloudWarehouseIds = new ArrayList<Long>();
            String newCloudWarehouseIdsStr = "";
            // 新增商户供应商(修改之后的)
            for (var cw : warehouseList) {
                // 添加 云仓 商户

                // 查询该商户是否存在
                List<ErpWarehouseMerchant> erpWarehouseMerchants = warehouseMerchantMapper.selectList(new LambdaQueryWrapper<ErpWarehouseMerchant>()
                        .eq(ErpWarehouseMerchant::getMerchantId, erpMerchant.getId())
                        .eq(ErpWarehouseMerchant::getWarehouseId, cw.getWarehouseId()));

                if (erpWarehouseMerchants == null || erpWarehouseMerchants.size() == 0) {
                    // 添加 云仓 商户

                    ErpWarehouseMerchant vendorMerchant = new ErpWarehouseMerchant();
                    vendorMerchant.setWarehouseId(cw.getWarehouseId());
                    vendorMerchant.setWarehouseType(cw.getWarehouseType());
                    vendorMerchant.setWarehouseName(cw.getWarehouseName());
                    vendorMerchant.setWarehouseNo(cw.getWarehouseNo());
                    vendorMerchant.setWarehouseAddress(cw.getWarehouseAddress());
                    vendorMerchant.setMerchantId(erpMerchant.getId());
                    vendorMerchant.setName(erpMerchant.getName());
                    vendorMerchant.setRemark(erpMerchant.getRemark());
                    vendorMerchant.setNumber(erpMerchant.getNumber());
                    vendorMerchant.setNickName(erpMerchant.getNickName());
                    vendorMerchant.setMobile(erpMerchant.getMobile());
                    vendorMerchant.setAvatar(erpMerchant.getAvatar());
                    vendorMerchant.setStatus(0);
                    vendorMerchant.setDelFlag(0);
                    vendorMerchant.setCreateBy("后台分配");
                    vendorMerchant.setCreateTime(new Date());
                    vendorMerchant.setUsci(erpMerchant.getUsci());
                    vendorMerchant.setFaren(erpMerchant.getFaren());
                    vendorMerchant.setBank(erpMerchant.getBank());
                    vendorMerchant.setLinkMan(erpMerchant.getLinkMan());
                    vendorMerchant.setAddress(erpMerchant.getAddress());
                    warehouseMerchantMapper.insert(vendorMerchant);


                }else {
                    // 修改
                    ErpWarehouseMerchant updateMerchant = new ErpWarehouseMerchant();
                    updateMerchant.setWarehouseId(cw.getWarehouseId());
                    updateMerchant.setWarehouseType(cw.getWarehouseType());
                    updateMerchant.setWarehouseName(cw.getWarehouseName());
                    updateMerchant.setWarehouseNo(cw.getWarehouseNo());
                    updateMerchant.setWarehouseAddress(cw.getWarehouseAddress());
                    updateMerchant.setMerchantId(erpMerchant.getId());
                    updateMerchant.setName(erpMerchant.getName());
                    updateMerchant.setRemark(erpMerchant.getRemark());
                    updateMerchant.setNumber(erpMerchant.getNumber());
                    updateMerchant.setNickName(erpMerchant.getNickName());
                    updateMerchant.setMobile(erpMerchant.getMobile());
                    updateMerchant.setAvatar(erpMerchant.getAvatar());
                    updateMerchant.setStatus(0);
                    updateMerchant.setDelFlag(0);
                    updateMerchant.setUsci(erpMerchant.getUsci());
                    updateMerchant.setFaren(erpMerchant.getFaren());
                    updateMerchant.setBank(erpMerchant.getBank());
                    updateMerchant.setLinkMan(erpMerchant.getLinkMan());
                    updateMerchant.setAddress(erpMerchant.getAddress());
                    updateMerchant.setUpdateBy("后台分配");
                    updateMerchant.setUpdateTime(new Date());
                    updateMerchant.setId(erpWarehouseMerchants.get(0).getId());
                    warehouseMerchantMapper.updateById(updateMerchant);
                }
                newCloudWarehouseIdsStr += cw.getWarehouseId().toString() + ",";
                newCloudWarehouseIds.add(cw.getWarehouseId());


                // 添加 供应商（云仓）
//                ErpVendor erpVendor = vendorMapper.selectById(supplierId);
//                if (erpVendor != null) {
////                    // 查询是否存在
////                    List<ErpMerchantSupplier> merchantSuppliers = merchantSupplierService.list(new LambdaQueryWrapper<ErpMerchantSupplier>()
////                            .eq(ErpMerchantSupplier::getMerchantId, erpMerchant.getId())
////                            .eq(ErpMerchantSupplier::getOriginVendorId, supplierId)
////                            .eq(ErpMerchantSupplier::getType, 1)
////                    );
////                    if (merchantSuppliers == null || merchantSuppliers.size() == 0) {
////                        ErpMerchantSupplier supplier = new ErpMerchantSupplier();
////                        supplier.setType(1);
////                        supplier.setName(erpVendor.getName());
////                        supplier.setNumber(erpVendor.getNumber());
////                        supplier.setRemark(erpVendor.getRemark());
////                        supplier.setLinkMan(erpVendor.getLinkMan());
////                        supplier.setContact(erpVendor.getContact());
////                        supplier.setProvince(erpVendor.getProvince());
////                        supplier.setCity(erpVendor.getCity());
////                        supplier.setCounty(erpVendor.getCounty());
////                        supplier.setAddress(erpVendor.getAddress());
////                        supplier.setDisable(0);
////                        supplier.setIsDelete(0);
////                        supplier.setCreateTime(new Date());
////                        supplier.setCreateBy("管理员分配");
////                        supplier.setOriginVendorId(erpVendor.getId());
////                        supplier.setMerchantId(erpMerchant.getId());
////                        merchantSupplierService.save(supplier);
////
////
////                    }
//                    supplierIdsNew += supplierId.toString() + ",";
//                }
            }

            // 删除不需要的 供应商（云仓）商户
            warehouseMerchantMapper.delete(new LambdaQueryWrapper<ErpWarehouseMerchant>()
                    .eq(ErpWarehouseMerchant::getMerchantId, erpMerchant.getId())
                    .notIn(ErpWarehouseMerchant::getWarehouseId, newCloudWarehouseIds)
            );

            // 删除不需要的供应商
//            merchantSupplierService.remove(new LambdaQueryWrapper<ErpMerchantSupplier>().notIn(ErpMerchantSupplier::getOriginVendorId, newSupplierIds));
            //更新自己
            ErpMerchant update = new ErpMerchant();
            update.setId(id);
            update.setSupplierIds("," + newCloudWarehouseIdsStr);
            mapper.updateById(update);

            return ResultVo.success();
        }
    }
}




