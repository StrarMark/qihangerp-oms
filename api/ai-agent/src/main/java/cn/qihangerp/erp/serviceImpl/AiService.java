package cn.qihangerp.erp.serviceImpl;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import java.time.Duration;

/**
 * AI服务类，使用LangChain4J调用Ollama模型处理聊天内容
 */
@Service
public class AiService {
    
    /**
     * 处理聊天消息
     * @param message 用户消息
     * @param model 模型名称
     * @return AI回复
     */
    public String processMessage(String message, String model) {
        try {
            // 根据模型名称创建对应的ChatModel
            OllamaChatModel modelInstance = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434") // Ollama默认端口
                    .modelName(model) // 使用指定的模型
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(300)) // 超时时间设置为300秒（5分钟）
                    .build();
            
            // 调用Ollama模型获取回复
            return modelInstance.chat(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "错误: " + e.getMessage();
        }
    }
    
    /**
     * 处理聊天消息（使用默认模型）
     * @param message 用户消息
     * @return AI回复
     */
    public String processMessage(String message) {
        return processMessage(message, "llama3");
    }
}
