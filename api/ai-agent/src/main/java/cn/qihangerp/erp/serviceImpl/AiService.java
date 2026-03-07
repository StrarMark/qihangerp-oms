package cn.qihangerp.erp.serviceImpl;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import cn.qihangerp.erp.service.OrderToolService;

/**
 * AI服务类，使用LangChain4J调用Ollama模型处理聊天内容
 */
@Service
public class AiService {
    
    /**
     * 定义AI服务接口
     */
    interface OrderAiService {
        String chat(String message);
    }
    
    /**
     * 处理聊天消息
     * @param message 用户消息
     * @param model 模型名称
     * @return AI回复
     */
    public String processMessage(String message, String model) {
        try {
            // 获取当前日期
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 替换消息中的"今天"为具体日期
            message = message.replace("今天", today);
            
            // 根据模型名称创建对应的ChatModel
            OllamaChatModel modelInstance = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434") // Ollama默认端口
                    .modelName(model) // 使用指定的模型
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(300)) // 超时时间设置为300秒（5分钟）
                    .build();
            
            // 创建订单工具服务
            OrderToolService orderToolService = new OrderToolService();
            
            // 使用AiServices创建AI服务，自动处理工具调用
            OrderAiService aiService = AiServices.builder(OrderAiService.class)
                    .chatModel(modelInstance)
                    .tools(orderToolService)
                    .build();
            
            // 执行AI服务，添加今天的日期信息
            String enhancedMessage = "今天的日期是：" + today + "\n" + message;
            System.out.println("发送给AI的消息: " + enhancedMessage);
            String result = aiService.chat(enhancedMessage);
            System.out.println("AI返回的结果: " + result);
            return result;
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
