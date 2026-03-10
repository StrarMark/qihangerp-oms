package cn.qihangerp.service.impl;

import cn.qihangerp.mapper.AiUserRoleMapper;
import cn.qihangerp.model.entity.AiUserRole;
import cn.qihangerp.service.IAiUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户AI角色Service业务层处理
 * 
 * @author qihang
 * @date 2024-05-21
 */
@Service
public class AiUserRoleServiceImpl extends ServiceImpl<AiUserRoleMapper, AiUserRole> implements IAiUserRoleService
{
    @Override
    public AiUserRole selectAiUserRoleById(Long id)
    {
        return baseMapper.selectAiUserRoleById(id);
    }

    @Override
    public List<AiUserRole> selectAiUserRoleList(AiUserRole aiUserRole)
    {
        return baseMapper.selectAiUserRoleList(aiUserRole);
    }

    @Override
    public List<AiUserRole> selectAiUserRolesByUserId(Long userId)
    {
        return baseMapper.selectAiUserRolesByUserId(userId);
    }

    @Override
    public AiUserRole selectDefaultAiUserRoleByUserId(Long userId)
    {
        return baseMapper.selectDefaultAiUserRoleByUserId(userId);
    }

    @Override
    public int insertAiUserRole(AiUserRole aiUserRole)
    {
        // 如果设置为默认角色，则先将该用户的其他角色设置为非默认
        if (aiUserRole.getIsDefault() != null && aiUserRole.getIsDefault() == 1)
        {
            baseMapper.updateNonDefaultRolesByUserId(aiUserRole.getUserId());
        }
        return baseMapper.insertAiUserRole(aiUserRole);
    }

    @Override
    public int updateAiUserRole(AiUserRole aiUserRole)
    {
        // 如果设置为默认角色，则先将该用户的其他角色设置为非默认
        if (aiUserRole.getIsDefault() != null && aiUserRole.getIsDefault() == 1)
        {
            baseMapper.updateNonDefaultRolesByUserId(aiUserRole.getUserId());
        }
        return baseMapper.updateAiUserRole(aiUserRole);
    }

    @Override
    public int deleteAiUserRoleById(Long id)
    {
        return baseMapper.deleteAiUserRoleById(id);
    }

    @Override
    public int deleteAiUserRoleByIds(Long[] ids)
    {
        return baseMapper.deleteAiUserRoleByIds(ids);
    }

    @Override
    public int setDefaultAiUserRole(Long userId, Long roleId)
    {
        // 先将该用户的所有角色设置为非默认
        baseMapper.updateNonDefaultRolesByUserId(userId);
        // 再将指定角色设置为默认
        AiUserRole aiUserRole = new AiUserRole();
        aiUserRole.setId(roleId);
        aiUserRole.setIsDefault(1);
        return baseMapper.updateAiUserRole(aiUserRole);
    }
}