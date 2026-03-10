package cn.qihangerp.erp.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.qihangerp.service.IAiConversationHistoryService;

/**
 * 会话管理服务，用于管理用户的会话ID
 */
@Component
public class SessionManager {
    private static final Map<Long, String> userIdSessionMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> sessionUserIdMap = new ConcurrentHashMap<>();
    
    @Autowired
    private IAiConversationHistoryService aiConversationHistoryService;

    /**
     * 获取或创建用户的会话ID
     * @param userId 用户ID
     * @return 会话ID
     */
    public String getOrCreateSessionId(Long userId) {
        if (userId == null) {
            return null;
        }
        
        // 先从内存缓存中获取
        String sessionId = userIdSessionMap.get(userId);
        if (sessionId != null) {
            return sessionId;
        }
        
        // 内存缓存中没有，从数据库中查找
        List<String> sessionIds = aiConversationHistoryService.getSessionIdsByUserId(userId);
        if (sessionIds != null && !sessionIds.isEmpty()) {
            // 使用第一个会话ID
            sessionId = sessionIds.get(0);
            userIdSessionMap.put(userId, sessionId);
            sessionUserIdMap.put(sessionId, userId);
            return sessionId;
        }
        
        // 数据库中也没有，创建新的会话ID
        sessionId = UUID.randomUUID().toString();
        userIdSessionMap.put(userId, sessionId);
        sessionUserIdMap.put(sessionId, userId);
        return sessionId;
    }

    /**
     * 根据用户ID获取会话ID
     * @param userId 用户ID
     * @return 会话ID
     */
    public String getSessionId(Long userId) {
        if (userId == null) {
            return null;
        }
        return userIdSessionMap.get(userId);
    }

    /**
     * 根据会话ID获取用户ID
     * @param sessionId 会话ID
     * @return 用户ID
     */
    public Long getUserIdBySessionId(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionUserIdMap.get(sessionId);
    }

    /**
     * 移除用户的会话
     * @param userId 用户ID
     */
    public void removeSession(Long userId) {
        if (userId == null) {
            return;
        }
        String sessionId = userIdSessionMap.remove(userId);
        if (sessionId != null) {
            sessionUserIdMap.remove(sessionId);
        }
    }

    /**
     * 获取当前活跃会话数
     * @return 活跃会话数
     */
    public int getSessionCount() {
        return userIdSessionMap.size();
    }
}