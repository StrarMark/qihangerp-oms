package cn.qihangerp.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.qihangerp.erp.serviceImpl.ConversationHistoryManager;
import cn.qihangerp.erp.serviceImpl.SessionManager;

/**
 * AI相关配置类
 */
@Configuration
public class AiConfig {
    
    /**
     * 会话管理服务Bean
     */
    @Bean
    public SessionManager sessionManager() {
        return new SessionManager();
    }
    
    /**
     * 对话历史管理服务Bean
     */
    @Bean
    public ConversationHistoryManager conversationHistoryManager() {
        return new ConversationHistoryManager();
    }
}