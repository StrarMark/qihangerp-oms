package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.AiConversationHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI聊天历史Mapper
 */
@Mapper
public interface AiConversationHistoryMapper extends BaseMapper<AiConversationHistory> {
    /**
     * 根据会话ID获取聊天历史
     * @param sessionId 会话ID
     * @return 聊天历史列表
     */
    List<AiConversationHistory> selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID获取最近的聊天历史
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 最近的聊天历史列表
     */
    List<AiConversationHistory> selectRecentBySessionId(@Param("sessionId") String sessionId, @Param("limit") int limit);

    /**
     * 根据用户ID获取会话ID列表
     * @param userId 用户ID
     * @return 会话ID列表
     */
    List<String> selectSessionIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID删除聊天历史
     * @param sessionId 会话ID
     * @return 删除的记录数
     */
    int deleteBySessionId(@Param("sessionId") String sessionId);
}