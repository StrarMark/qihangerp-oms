package cn.qihangerp.erp.controller.erp;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.model.entity.ErpLogisticsCompany;
import cn.qihangerp.model.entity.ErpShipLogistics;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.ErpLogisticsCompanyService;
import cn.qihangerp.service.ErpShipLogisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 发货常用快递公司 Controller
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/erp-api/ship/logistics")
public class ShipLogisticsController extends BaseController {
    private final ErpShipLogisticsService shipLogisticsService;
    private final ErpLogisticsCompanyService logisticsCompanyService;

    /**
     * 获取平台列表（从快递公司库中获取）
     */
    @GetMapping("/platform_list")
    public AjaxResult platformList() {
        LambdaQueryWrapper<ErpLogisticsCompany> queryWrapper = new LambdaQueryWrapper<ErpLogisticsCompany>()
                .eq(ErpLogisticsCompany::getStatus, 0)
                .select(ErpLogisticsCompany::getPlatformId);

        List<ErpLogisticsCompany> allPlatforms = logisticsCompanyService.list(queryWrapper);

        // Deduplicate platform IDs
        List<Integer> distinctPlatformIds = allPlatforms.stream()
                .map(ErpLogisticsCompany::getPlatformId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Build platform name map
        Map<Integer, String> platformNameMap = new HashMap<>();
        platformNameMap.put(0, "ERP内销订单");
        platformNameMap.put(100, "淘宝天猫");
        platformNameMap.put(200, "京东POP");
        platformNameMap.put(280, "京东自营");
        platformNameMap.put(300, "拼多多");
        platformNameMap.put(400, "抖店");
        platformNameMap.put(500, "微信小店");
        platformNameMap.put(600, "快手小店");
        platformNameMap.put(700, "小红书");
        platformNameMap.put(901, "微店");
        platformNameMap.put(911, "螳螂系统");
        platformNameMap.put(999, "线下渠道");
        platformNameMap.put(10000, "店铺订单");

        List<Map<String, Object>> result = distinctPlatformIds.stream()
                .map(platformId -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", platformId);
                    item.put("name", platformNameMap.getOrDefault(platformId, "未知平台(" + platformId + ")"));
                    item.put("code", String.valueOf(platformId));
                    return item;
                })
                .collect(Collectors.toList());

        return success(result);
    }

    /**
     * 获取当前用户的常用快递公司列表（支持按平台筛选）
     * @param shopType 平台类型，传0或不传表示不过滤（显示全部），传具体平台ID则按平台筛选
     */
    @GetMapping("/favorite_list")
    public AjaxResult favoriteList(@RequestParam(required = false) Integer shopType) {
        List<ErpShipLogistics> list = shipLogisticsService.queryListByEntity();

        // 如果传了具体平台ID且不是0，按平台过滤
        if (shopType != null && shopType > 0) {
            list = list.stream()
                    .filter(item -> shopType.equals(item.getShopType()))
                    .collect(Collectors.toList());
        }
        // shopType=0 或不传 → 返回全部，不过滤

        return success(list);
    }

    /**
     * 获取可选择的快递公司列表（支持按平台筛选和模糊搜索）
     */
    @GetMapping("/available_list")
    public AjaxResult availableList(
            @RequestParam(required = false) Integer platformId,
            @RequestParam(required = false, defaultValue = "") String name) {

        LambdaQueryWrapper<ErpLogisticsCompany> queryWrapper = new LambdaQueryWrapper<ErpLogisticsCompany>()
                .eq(ErpLogisticsCompany::getStatus, 0)
                .and(StringUtils.hasText(name), w -> w.like(ErpLogisticsCompany::getName, name)
                        .or().like(ErpLogisticsCompany::getCode, name))
                .orderByDesc(ErpLogisticsCompany::getPlatformId);

        if (platformId != null && platformId > 0) {
            queryWrapper.eq(ErpLogisticsCompany::getPlatformId, platformId);
        }

        List<ErpLogisticsCompany> list = logisticsCompanyService.list(queryWrapper);

        // Transform to include platform display info
        List<Map<String, Object>> result = list.stream()
                .map(company -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", company.getId() != null ? Long.parseLong(company.getId()) : 0L);
                    item.put("name", company.getName());
                    item.put("code", company.getCode());
                    item.put("platformId", company.getPlatformId());
                    return item;
                })
                .collect(Collectors.toList());

        return success(result);
    }

    /**
     * 添加常用快递公司
     */
    @PostMapping("/favorite_add")
    public AjaxResult addFavorite(@RequestBody ErpShipLogistics entity) {
        // Determine entityType and entityId from current user
        String userType = getLoginUser().getUser().getUserType();
        Long deptId = getLoginUser().getDeptId();

        entity.setEntityType(userType);
        entity.setEntityId(deptId);

        if (entity.getIsDefault() == null) {
            entity.setIsDefault(0);
        }
        if (entity.getSort() == null) {
            entity.setSort(0);
        }

        ResultVo<Integer> result = shipLogisticsService.add(entity);
        if (result.getCode() == 0) {
            return success(result.getMsg());
        } else {
            return error(result.getMsg());
        }
    }

    /**
     * 删除常用快递公司
     */
    @DeleteMapping("/favorite_delete/{id}")
    public AjaxResult deleteFavorite(@PathVariable Long id) {
        ResultVo<Integer> result = shipLogisticsService.delete(id);
        if (result.getCode() == 0) {
            return success(result.getMsg());
        } else {
            return error(result.getMsg());
        }
    }

    /**
     * 设置默认快递公司
     */
    @PutMapping("/favorite_set_default/{id}")
    public AjaxResult setDefault(@PathVariable Long id) {
        ResultVo<Integer> result = shipLogisticsService.setDefault(id);
        if (result.getCode() == 0) {
            return success(result.getMsg());
        } else {
            return error(result.getMsg());
        }
    }
}
