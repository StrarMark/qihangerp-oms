package cn.qihangerp.service;

import cn.qihangerp.model.entity.AiConversationHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * AI聊天历史Service
 */
public interface IAiConversationHistoryService extends IService<AiConversationHistory> {
    /**
     * 根据会话ID获取聊天历史
     * @param sessionId 会话ID
     * @return 聊天历史列表
     */
    List<AiConversationHistory> getBySessionId(String sessionId);

    /**
     * 根据会话ID获取最近的聊天历史
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 最近的聊天历史列表
     */
    List<AiConversationHistory> getRecentBySessionId(String sessionId, int limit);

    /**
     * 根据用户ID获取会话ID列表
     * @param userId 用户ID
     * @return 会话ID列表
     */
    List<String> getSessionIdsByUserId(Long userId);

    /**
     * 根据会话ID删除聊天历史
     * @param sessionId 会话ID
     * @return 删除的记录数
     */
    int deleteBySessionId(String sessionId);

    /**
     * 保存聊天消息
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param role 角色
     * @param content 内容
     * @return 保存的消息
     */
    AiConversationHistory saveMessage(Long userId, String sessionId, String role, String content);
}