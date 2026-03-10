package cn.qihangerp.service.impl;

import cn.qihangerp.mapper.AiConversationHistoryMapper;
import cn.qihangerp.model.entity.AiConversationHistory;
import cn.qihangerp.service.IAiConversationHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * AI聊天历史Service实现
 */
@Service
@AllArgsConstructor
public class AiConversationHistoryServiceImpl extends ServiceImpl<AiConversationHistoryMapper, AiConversationHistory> implements IAiConversationHistoryService {

    private final AiConversationHistoryMapper aiConversationHistoryMapper;

    @Override
    public List<AiConversationHistory> getBySessionId(String sessionId) {
        return aiConversationHistoryMapper.selectBySessionId(sessionId);
    }

    @Override
    public List<AiConversationHistory> getRecentBySessionId(String sessionId, int limit) {
        return aiConversationHistoryMapper.selectRecentBySessionId(sessionId, limit);
    }

    @Override
    public List<String> getSessionIdsByUserId(Long userId) {
        return aiConversationHistoryMapper.selectSessionIdsByUserId(userId);
    }

    @Override
    public int deleteBySessionId(String sessionId) {
        return aiConversationHistoryMapper.deleteBySessionId(sessionId);
    }

    @Override
    public AiConversationHistory saveMessage(Long userId, String sessionId, String role, String content) {
        AiConversationHistory history = new AiConversationHistory();
        history.setUserId(userId);
        history.setSessionId(sessionId);
        history.setRole(role);
        history.setContent(content);
        history.setTimestamp(System.currentTimeMillis());
        history.setCreateTime(new Date());
        history.setUpdateTime(new Date());
        save(history);
        return history;
    }
}