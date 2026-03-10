package cn.qihangerp.erp.serviceImpl;

import cn.qihangerp.service.IAiConversationHistoryService;
import cn.qihangerp.model.entity.AiConversationHistory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话历史管理服务，用于保存和管理用户的对话历史
 */
@Component
@AllArgsConstructor
public class ConversationHistoryManager {

    private final IAiConversationHistoryService aiConversationHistoryService;

    /**
     * 消息实体类
     */
    public static class Message {
        private long id;
        private String role; // user 或 assistant
        private String content;
        private long timestamp;

        public Message(long id, String role, String content, long timestamp) {
            this.id = id;
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public Message(String role, String content) {
            this.id = 0;
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
        // 这里简化处理，暂时不传入userId，实际使用时需要从会话中获取
        aiConversationHistoryService.saveMessage(0L, sessionId, role, content);
    }

    /**
     * 添加消息到对话历史（带用户ID）
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param role 角色
     * @param content 消息内容
     */
    public void addMessage(Long userId, String sessionId, String role, String content) {
        if (sessionId == null) {
            return;
        }
        aiConversationHistoryService.saveMessage(userId, sessionId, role, content);
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
        List<AiConversationHistory> historyList = aiConversationHistoryService.getBySessionId(sessionId);
        return historyList.stream()
                .map(history -> new Message(history.getId(), history.getRole(), history.getContent(), history.getTimestamp()))
                .collect(Collectors.toList());
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
        List<AiConversationHistory> historyList = aiConversationHistoryService.getRecentBySessionId(sessionId, limit);
        // 反转列表，使时间戳从早到晚排序
        List<Message> messages = historyList.stream()
                .map(history -> new Message(history.getId(), history.getRole(), history.getContent(), history.getTimestamp()))
                .collect(Collectors.toList());
        java.util.Collections.reverse(messages);
        return messages;
    }

    /**
     * 清空会话的对话历史
     * @param sessionId 会话ID
     */
    public void clearConversationHistory(String sessionId) {
        if (sessionId != null) {
            aiConversationHistoryService.deleteBySessionId(sessionId);
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
        List<AiConversationHistory> historyList = aiConversationHistoryService.getBySessionId(sessionId);
        return historyList.size();
    }
}