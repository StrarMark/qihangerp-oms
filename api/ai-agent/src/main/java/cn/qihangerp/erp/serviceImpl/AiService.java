package cn.qihangerp.erp.serviceImpl;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import cn.qihangerp.erp.service.OrderToolService;
import cn.qihangerp.erp.serviceImpl.ConversationHistoryManager;

/**
 * AI服务类，使用LangChain4J调用Ollama模型处理聊天内容
 */
@Service
public class AiService {
    
    /**
     * 页面规则类
     */
    static class PageRule {
        String keyword;
        String route;
        String message;

        public PageRule(String keyword, String route, String message) {
            this.keyword = keyword;
            this.route = route;
            this.message = message;
        }
    }

    // 页面规则列表
    private List<PageRule> pageRules = new ArrayList<>();

    // DeepSeek API 配置
    @Value("${deepseek.api.key:}")
    private String deepSeekApiKey;

    @Value("${deepseek.api.endpoint:https://api.deepseek.com/v1/chat/completions}")
    private String deepSeekApiEndpoint;

    @Value("${deepseek.api.model:deepseek-chat}")
    private String deepSeekModel;

    /**
     * 构造方法，加载页面规则
     */
    public AiService() {
        loadPageRules();
    }

    /**
     * 加载页面规则
     */
    private void loadPageRules() {
        try (InputStream inputStream = getClass().getResourceAsStream("/page-rules.md");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean inTable = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // 检查是否进入表格部分
                if (line.equals("| 关键词 | 路由路径 | 提示消息 |")) {
                    inTable = true;
                    // 跳过下一行分隔符
                    reader.readLine();
                    continue;
                }

                // 如果在表格中，解析规则
                if (inTable && line.startsWith("|")) {
                    String[] parts = line.split("\\|").clone();
                    if (parts.length >= 4) {
                        String keyword = parts[1].trim();
                        String route = parts[2].trim();
                        String message = parts[3].trim();
                        if (!keyword.isEmpty() && !route.isEmpty() && !message.isEmpty()) {
                            pageRules.add(new PageRule(keyword, route, message));
                        }
                    }
                }

                // 检查表格结束
                if (inTable && line.isEmpty()) {
                    break;
                }
            }

            System.out.println("加载页面规则成功，共加载 " + pageRules.size() + " 条规则");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("加载页面规则失败");
        }
    }

    /**
     * 检查页面跳转规则
     * @param message 用户消息
     * @return 页面跳转响应，null表示没有匹配的规则
     */
    private String checkPageRules(String message) {
        for (PageRule rule : pageRules) {
            if (message.contains(rule.keyword)) {
                return String.format("{\"action\": \"navigate\", \"route\": \"%s\", \"message\": \"%s\"}", rule.route, rule.message);
            }
        }
        return null;
    }

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
     * @param sessionId 会话ID
     * @param conversationHistory 对话历史
     * @return AI回复
     */
    public String processMessage(String message, String model, String sessionId, List<ConversationHistoryManager.Message> conversationHistory) {
        try {
            // 优先检查页面跳转规则
            String pageRuleResponse = checkPageRules(message);
            if (pageRuleResponse != null) {
                System.out.println("匹配到页面跳转规则: " + pageRuleResponse);
                return pageRuleResponse;
            }
            
            // 获取当前日期
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 替换消息中的"今天"为具体日期
            message = message.replace("今天", today);
            
            // 构建包含历史对话的提示词
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("今天的日期是：").append(today).append("\n");
            
            // 添加历史对话作为上下文，但限制总长度
            final int MAX_CONTEXT_LENGTH = 2000; // 最大上下文长度限制
            StringBuilder contextBuilder = new StringBuilder();
            
            if (conversationHistory != null && !conversationHistory.isEmpty()) {
                // 倒序遍历对话历史，优先保留最新的对话
                for (int i = conversationHistory.size() - 1; i >= 0; i--) {
                    ConversationHistoryManager.Message msg = conversationHistory.get(i);
                    String msgStr;
                    if (msg.getRole().equals("user")) {
                        msgStr = "用户: " + msg.getContent() + "\n";
                    } else {
                        msgStr = "助手: " + msg.getContent() + "\n";
                    }
                    
                    // 如果添加当前消息会超过限制，则停止添加更早的消息
                    if (contextBuilder.length() + msgStr.length() <= MAX_CONTEXT_LENGTH) {
                        contextBuilder.insert(0, msgStr); // 插入到开头，保持时间顺序
                    } else {
                        break;
                    }
                }
                
                if (contextBuilder.length() > 0) {
                    promptBuilder.append("以下是之前的对话历史：\n");
                    promptBuilder.append(contextBuilder);
                    promptBuilder.append("\n当前用户消息：\n");
                }
            }
            
            // 添加当前消息
            promptBuilder.append(message);
            
            String enhancedMessage = promptBuilder.toString();
            System.out.println("发送给AI的消息: " + enhancedMessage);
            
            // 尝试创建订单工具服务
            OrderToolService orderToolService = null;
            try {
                orderToolService = new OrderToolService();
                System.out.println("成功创建OrderToolService");
            } catch (Exception e) {
                System.out.println("创建OrderToolService失败: " + e.getMessage());
                // 工具创建失败，仍然继续执行，只是不使用工具
            }
            
            // 根据模型名称选择使用Ollama还是DeepSeek API
            OrderAiService aiService;
            if (model.startsWith("deepseek")) {
                // 使用DeepSeek API
                if (deepSeekApiKey == null || deepSeekApiKey.isEmpty()) {
                    return "错误: DeepSeek API密钥未配置，请在application.yml中设置deepseek.api.key";
                }
                
                try {
                    // 尝试创建DeepSeek模型实例
                    OpenAiChatModel deepSeekModelInstance = OpenAiChatModel.builder()
                            .baseUrl(deepSeekApiEndpoint)
                            .apiKey(deepSeekApiKey)
                            .modelName(deepSeekModel)
                            .temperature(0.7)
                            .timeout(Duration.ofSeconds(300))
                            .build();
                    
                    if (orderToolService != null) {
                        aiService = AiServices.builder(OrderAiService.class)
                                .chatModel(deepSeekModelInstance)
                                .tools(orderToolService)
                                .build();
                    } else {
                        aiService = AiServices.builder(OrderAiService.class)
                                .chatModel(deepSeekModelInstance)
                                .build();
                    }
                    System.out.println("使用DeepSeek API处理消息");
                } catch (Exception e) {
                    // 如果DeepSeek依赖不可用，返回错误消息
                    return "错误: DeepSeek API依赖未配置，请检查Maven依赖是否正确";
                }
            } else {
                // 使用Ollama
                try {
                    System.out.println("尝试连接Ollama，模型: " + model);
                    OllamaChatModel modelInstance = OllamaChatModel.builder()
                            .baseUrl("http://localhost:11434") // Ollama默认端口
                            .modelName(model) // 使用指定的模型
                            .temperature(0.7)
                            .timeout(Duration.ofSeconds(300)) // 超时时间设置为300秒（5分钟）
                            .build();
                    
                    if (orderToolService != null) {
                        aiService = AiServices.builder(OrderAiService.class)
                                .chatModel(modelInstance)
                                .tools(orderToolService)
                                .build();
                    } else {
                        aiService = AiServices.builder(OrderAiService.class)
                                .chatModel(modelInstance)
                                .build();
                    }
                    System.out.println("成功创建Ollama模型实例，模型: " + model);
                } catch (Exception e) {
                    System.out.println("创建Ollama模型实例失败: " + e.getMessage());
                    return "错误: 无法连接到Ollama服务，请检查Ollama是否已启动，端口是否正确（默认11434）";
                }
            }
            
            try {
                System.out.println("开始调用AI服务");
                String result = aiService.chat(enhancedMessage);
                System.out.println("AI返回的结果: " + result);
                return result;
            } catch (Exception e) {
                System.out.println("调用AI服务失败: " + e.getMessage());
                return "错误: 调用AI服务失败: " + e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "错误: " + e.getMessage();
        }
    }
    
    /**
     * 处理聊天消息
     * @param message 用户消息
     * @param model 模型名称
     * @return AI回复
     */
    public String processMessage(String message, String model) {
        return processMessage(message, model, null, null);
    }
    
    /**
     * 处理聊天消息（使用默认模型）
     * @param message 用户消息
     * @return AI回复
     */
    public String processMessage(String message) {
        return processMessage(message, "llama3", null, null);
    }
}