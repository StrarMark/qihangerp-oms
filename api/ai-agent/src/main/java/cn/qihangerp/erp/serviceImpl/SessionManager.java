package cn.qihangerp.erp.serviceImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * 会话管理服务，用于管理用户的会话ID
 */
public class SessionManager {
    private static final Map<Long, String> userIdSessionMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> sessionUserIdMap = new ConcurrentHashMap<>();

    /**
     * 获取或创建用户的会话ID
     * @param userId 用户ID
     * @return 会话ID
     */
    public String getOrCreateSessionId(Long userId) {
        if (userId == null) {
            return null;
        }
        return userIdSessionMap.computeIfAbsent(userId, k -> {
            String sessionId = UUID.randomUUID().toString();
            sessionUserIdMap.put(sessionId, userId);
            return sessionId;
        });
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