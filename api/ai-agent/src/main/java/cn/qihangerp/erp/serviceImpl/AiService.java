package cn.qihangerp.erp.serviceImpl;

import cn.qihangerp.common.ResultVo;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * AI服务类，直接调用Ollama API处理聊天内容
 */
@Service
public class AiService {
    
    private final HttpClient httpClient;
    private final String ollamaUrl;
    
    public AiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.ollamaUrl = "http://localhost:11434/api/generate";
    }
    
    /**
     * 处理聊天消息
     * @param message 用户消息
     * @return AI回复
     */
    public String processMessage(String message) {
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "llama3");
            requestBody.put("prompt", message);
            requestBody.put("stream", false);
            requestBody.put("temperature", 0.7);
            
            // 创建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                    .build();
            
            // 发送请求并获取响应
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // 解析响应
            JSONObject responseBody = JSONObject.parseObject(response.body());
            
            // 检查是否有错误
            if (responseBody.containsKey("error")) {
                String errorMessage = responseBody.getString("error");
                return "错误: " + errorMessage;
            }
            
            return responseBody.getString("response");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "抱歉，我暂时无法处理您的请求，请稍后重试。";
        }
    }
}
