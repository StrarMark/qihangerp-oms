package cn.qihangerp.erp.controller.ai;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.security.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户AI角色Controller
 *
 * @author qihang
 * @date 2024-06-20
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-agent/ai/user-role")
public class AiUserRoleController extends BaseController {

    /**
     * 获取当前用户所有角色
     */
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success("开源版本暂不支持AI角色功能");
    }

    /**
     * 获取当前用户默认角色
     */
    @GetMapping("/default")
    public AjaxResult getDefault() {
        return AjaxResult.success("开源版本暂不支持AI角色功能");
    }

    /**
     * 新增用户AI角色
     */
    @PostMapping
    public AjaxResult add() {
        return AjaxResult.success("开源版本暂不支持AI角色功能");
    }

    /**
     * 修改用户AI角色
     */
    @PutMapping
    public AjaxResult edit() {
        return AjaxResult.success("开源版本暂不支持AI角色功能");
    }

    /**
     * 删除用户AI角色
     */
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return AjaxResult.success("开源版本暂不支持AI角色功能");
    }

    /**
     * 设置默认角色
     */
    @PutMapping("/set-default/{roleId}")
    public AjaxResult setDefault(@PathVariable Long roleId) {
        return AjaxResult.success("开源版本暂不支持AI角色功能");
    }
}
