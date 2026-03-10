package cn.qihangerp.erp.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 对话历史管理服务，用于保存和管理用户的对话历史
 */
public class ConversationHistoryManager {
    private static final Map<String, List<Message>> sessionHistoryMap = new ConcurrentHashMap<>();
    private static final AtomicLong messageIdCounter = new AtomicLong(0);

    /**
     * 消息实体类
     */
    public static class Message {
        private long id;
        private String role; // user 或 assistant
        private String content;
        private long timestamp;

        public Message(String role, String content) {
            this.id = messageIdCounter.incrementAndGet();
            this.role = role;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public long getId() {
            return id;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * 添加消息到对话历史
     * @param sessionId 会话ID
     * @param role 角色
     * @param content 消息内容
     */
    public void addMessage(String sessionId, String role, String content) {
        if (sessionId == null) {
            return;
        }
        sessionHistoryMap.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new Message(role, content));
    }

    /**
     * 获取会话的所有对话历史
     * @param sessionId 会话ID
     * @return 对话历史列表
     */
    public List<Message> getConversationHistory(String sessionId) {
        if (sessionId == null) {
            return new ArrayList<>();
        }
        return sessionHistoryMap.getOrDefault(sessionId, new ArrayList<>());
    }

    /**
     * 获取会话的最近几条对话历史
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 最近的对话历史列表
     */
    public List<Message> getRecentConversationHistory(String sessionId, int limit) {
        if (sessionId == null) {
            return new ArrayList<>();
        }
        List<Message> history = sessionHistoryMap.getOrDefault(sessionId, new ArrayList<>());
        int startIndex = Math.max(0, history.size() - limit);
        return history.subList(startIndex, history.size());
    }

    /**
     * 清空会话的对话历史
     * @param sessionId 会话ID
     */
    public void clearConversationHistory(String sessionId) {
        if (sessionId != null) {
            sessionHistoryMap.remove(sessionId);
        }
    }

    /**
     * 获取会话的对话历史数量
     * @param sessionId 会话ID
     * @return 对话历史数量
     */
    public int getMessageCount(String sessionId) {
        if (sessionId == null) {
            return 0;
        }
        List<Message> history = sessionHistoryMap.get(sessionId);
        return history != null ? history.size() : 0;
    }
}