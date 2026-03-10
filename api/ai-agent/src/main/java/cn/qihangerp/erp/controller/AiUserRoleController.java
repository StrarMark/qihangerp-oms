package cn.qihangerp.erp.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import cn.qihangerp.security.LoginUser;
import cn.qihangerp.security.TokenService;
import cn.qihangerp.model.entity.AiUserRole;
import cn.qihangerp.service.IAiUserRoleService;

/**
 * 用户AI角色Controller
 * 
 * @author qihang
 * @date 2024-06-20
 */
@RestController
@RequestMapping("/ai/user-role")
public class AiUserRoleController {
    @Autowired
    private IAiUserRoleService aiUserRoleService;
    
    @Autowired
    private TokenService tokenService;

    /**
     * 获取当前用户所有角色
     */
    @GetMapping("/list")
    public ResponseEntity<List<AiUserRole>> list(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        List<AiUserRole> list = aiUserRoleService.selectAiUserRolesByUserId(loginUser.getUserId());
        return ResponseEntity.ok(list);
    }

    /**
     * 获取当前用户默认角色
     */
    @GetMapping("/default")
    public ResponseEntity<AiUserRole> getDefault(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        AiUserRole aiUserRole = aiUserRoleService.selectDefaultAiUserRoleByUserId(loginUser.getUserId());
        return ResponseEntity.ok(aiUserRole);
    }

    /**
     * 新增用户AI角色
     */
    @PostMapping
    public ResponseEntity<Integer> add(@RequestBody AiUserRole aiUserRole, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        aiUserRole.setUserId(loginUser.getUserId());
        int result = aiUserRoleService.insertAiUserRole(aiUserRole);
        return ResponseEntity.ok(result);
    }

    /**
     * 修改用户AI角色
     */
    @PutMapping
    public ResponseEntity<Integer> edit(@RequestBody AiUserRole aiUserRole, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        // 验证角色是否属于当前用户
        AiUserRole existingRole = aiUserRoleService.selectAiUserRoleById(aiUserRole.getId());
        if (existingRole == null || !existingRole.getUserId().equals(loginUser.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(0);
        }
        int result = aiUserRoleService.updateAiUserRole(aiUserRole);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除用户AI角色
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> remove(@PathVariable Long id, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        // 验证角色是否属于当前用户
        AiUserRole existingRole = aiUserRoleService.selectAiUserRoleById(id);
        if (existingRole == null || !existingRole.getUserId().equals(loginUser.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(0);
        }
        int result = aiUserRoleService.deleteAiUserRoleById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 设置默认角色
     */
    @PutMapping("/set-default/{roleId}")
    public ResponseEntity<Integer> setDefault(@PathVariable Long roleId, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        // 验证角色是否属于当前用户
        AiUserRole existingRole = aiUserRoleService.selectAiUserRoleById(roleId);
        if (existingRole == null || !existingRole.getUserId().equals(loginUser.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(0);
        }
        int result = aiUserRoleService.setDefaultAiUserRole(loginUser.getUserId(), roleId);
        return ResponseEntity.ok(result);
    }
}