package cn.qihangerp.erp.serviceImpl;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
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
     * @return AI回复
     */
    public String processMessage(String message, String model) {
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
