package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.AiUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户AI角色表Mapper接口
 * 
 * @author qihang
 */
@Mapper
public interface AiUserRoleMapper extends BaseMapper<AiUserRole>
{
    /**
     * 查询用户AI角色表
     * 
     * @param id 用户AI角色表主键
     * @return 用户AI角色表
     */
    public AiUserRole selectAiUserRoleById(@Param("id") Long id);

    /**
     * 查询用户AI角色表列表
     * 
     * @param aiUserRole 用户AI角色表
     * @return 用户AI角色表集合
     */
    public List<AiUserRole> selectAiUserRoleList(@Param("aiUserRole") AiUserRole aiUserRole);

    /**
     * 查询用户的所有角色
     * 
     * @param userId 用户ID
     * @return 用户角色集合
     */
    public List<AiUserRole> selectAiUserRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的默认角色
     * 
     * @param userId 用户ID
     * @return 默认角色
     */
    public AiUserRole selectDefaultAiUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 新增用户AI角色表
     * 
     * @param aiUserRole 用户AI角色表
     * @return 结果
     */
    public int insertAiUserRole(@Param("aiUserRole") AiUserRole aiUserRole);

    /**
     * 修改用户AI角色表
     * 
     * @param aiUserRole 用户AI角色表
     * @return 结果
     */
    public int updateAiUserRole(@Param("aiUserRole") AiUserRole aiUserRole);

    /**
     * 删除用户AI角色表
     * 
     * @param id 用户AI角色表主键
     * @return 结果
     */
    public int deleteAiUserRoleById(@Param("id") Long id);

    /**
     * 批量删除用户AI角色表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiUserRoleByIds(@Param("ids") Long[] ids);

    /**
     * 更新用户的非默认角色
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int updateNonDefaultRolesByUserId(@Param("userId") Long userId);
}