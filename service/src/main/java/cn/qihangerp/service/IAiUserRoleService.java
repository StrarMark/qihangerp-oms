package cn.qihangerp.service;

import cn.qihangerp.model.entity.AiUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户AI角色Service接口
 * 
 * @author qihang
 * @date 2024-05-21
 */
public interface IAiUserRoleService extends IService<AiUserRole>
{
    /**
     * 查询用户AI角色
     * 
     * @param id 用户AI角色主键
     * @return 用户AI角色
     */
    public AiUserRole selectAiUserRoleById(Long id);

    /**
     * 查询用户AI角色列表
     * 
     * @param aiUserRole 用户AI角色
     * @return 用户AI角色集合
     */
    public List<AiUserRole> selectAiUserRoleList(AiUserRole aiUserRole);

    /**
     * 根据用户ID查询用户AI角色列表
     * 
     * @param userId 用户ID
     * @return 用户AI角色集合
     */
    public List<AiUserRole> selectAiUserRolesByUserId(Long userId);

    /**
     * 根据用户ID查询默认用户AI角色
     * 
     * @param userId 用户ID
     * @return 用户AI角色
     */
    public AiUserRole selectDefaultAiUserRoleByUserId(Long userId);

    /**
     * 新增用户AI角色
     * 
     * @param aiUserRole 用户AI角色
     * @return 结果
     */
    public int insertAiUserRole(AiUserRole aiUserRole);

    /**
     * 修改用户AI角色
     * 
     * @param aiUserRole 用户AI角色
     * @return 结果
     */
    public int updateAiUserRole(AiUserRole aiUserRole);

    /**
     * 批量删除用户AI角色
     * 
     * @param ids 需要删除的用户AI角色主键集合
     * @return 结果
     */
    public int deleteAiUserRoleByIds(Long[] ids);

    /**
     * 删除用户AI角色信息
     * 
     * @param id 用户AI角色主键
     * @return 结果
     */
    public int deleteAiUserRoleById(Long id);

    /**
     * 设置默认角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 结果
     */
    public int setDefaultAiUserRole(Long userId, Long roleId);
}