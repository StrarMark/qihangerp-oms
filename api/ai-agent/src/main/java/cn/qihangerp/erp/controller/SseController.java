package cn.qihangerp.erp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.qihangerp.security.LoginUser;
import cn.qihangerp.security.TokenService;
import cn.qihangerp.erp.serviceImpl.AiService;
import cn.qihangerp.erp.serviceImpl.ConversationHistoryManager;
import cn.qihangerp.erp.serviceImpl.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/sse")
public class SseController {

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Map<String, Long> clientUserIdMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    
    @Autowired
    private AiService aiService;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private ConversationHistoryManager conversationHistoryManager;
    
    @Autowired
    private TokenService tokenService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String clientId, @RequestParam String token, HttpServletRequest request) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(clientId, emitter);

        // 从token中获取用户信息
        try {
            LoginUser loginUser = tokenService.getLoginUser(request);
            if (loginUser != null) {
                Long userId = loginUser.getUserId();
                clientUserIdMap.put(clientId, userId);
                log.info("用户 {} 连接成功，clientId: {}", userId, clientId);
            }
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
        }

        // 设置超时处理
        emitter.onTimeout(() -> {
            emitters.remove(clientId);
            clientUserIdMap.remove(clientId);
        });
        emitter.onCompletion(() -> {
            emitters.remove(clientId);
            clientUserIdMap.remove(clientId);
        });

        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("连接成功"));
        } catch (IOException e) {
            emitters.remove(clientId);
            clientUserIdMap.remove(clientId);
        }

        // 为新用户添加默认欢迎消息
        Long userId = clientUserIdMap.get(clientId);
        if (userId != null) {
            String sessionId = sessionManager.getOrCreateSessionId(userId);
            // 检查是否有对话历史
            int messageCount = conversationHistoryManager.getMessageCount(sessionId);
            if (messageCount == 0) {
                // 添加欢迎消息到对话历史
                String welcomeMessage = "您好，我是您的智能助手，我能帮你打开页面、查询订单、查询商品、查询库存等等。欢迎提问！";
                conversationHistoryManager.addMessage(userId, sessionId, "assistant", welcomeMessage);
                
                // 发送欢迎消息给客户端
                try {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(welcomeMessage));
                } catch (IOException e) {
                    log.error("发送欢迎消息失败: {}", e.getMessage());
                }
            }
        }

        // 定期发送心跳
        executorService.scheduleAtFixedRate(() -> {
            try {
                if (emitters.containsKey(clientId)) {
                    emitters.get(clientId).send(SseEmitter.event()
                            .name("heartbeat")
                            .data("ping"));
                }
            } catch (IOException e) {
                emitters.remove(clientId);
                clientUserIdMap.remove(clientId);
            }
        }, 30, 30, TimeUnit.SECONDS);

        return emitter;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String clientId, @RequestParam String message, @RequestParam(required = false, defaultValue = "llama3") String model, @RequestParam String token, HttpServletRequest request) {
        log.info("=============来新消息了！");
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                // 从token中获取用户信息
                LoginUser loginUser = tokenService.getLoginUser(request);
                Long userId = null;
                if (loginUser != null) {
                    userId = loginUser.getUserId();
                    clientUserIdMap.put(clientId, userId);
                } else {
                    // 尝试从映射中获取用户ID
                    userId = clientUserIdMap.get(clientId);
                }
                
                String sessionId = null;
                if (userId != null) {
                    // 获取或创建会话ID
                    sessionId = sessionManager.getOrCreateSessionId(userId);
                    log.info("用户 {} 的会话ID: {}", userId, sessionId);
                    
                    // 添加用户消息到对话历史
                    conversationHistoryManager.addMessage(userId, sessionId, "user", message);
                }
                
                // 获取对话历史
                List<ConversationHistoryManager.Message> conversationHistory = null;
                if (sessionId != null) {
                    conversationHistory = conversationHistoryManager.getRecentConversationHistory(sessionId, 10); // 只获取最近10条消息作为上下文
                }
                
                // 使用AiService处理消息，传递模型参数、会话ID和对话历史
                String response = aiService.processMessage(message, model, sessionId, conversationHistory);
                log.info("==========AI回复：{}", response);
                
                // 如果有会话ID，添加AI回复到对话历史
                if (sessionId != null) {
                    conversationHistoryManager.addMessage(userId, sessionId, "assistant", response);
                }
                
                // 检查响应是否已经是JSON格式（以{开头）
                String jsonResponse;
                if (response.trim().startsWith("{")) {
                    // 如果是JSON格式，直接使用
                    jsonResponse = response;
                } else {
                    // 否则包装成JSON格式
                    jsonResponse = String.format("{\"text\": \"%s\", \"sessionId\": \"%s\"}", response.replace("\"", "\\\"").replace("\n", "\\n"), sessionId != null ? sessionId : "");
                }
                
                // 发送JSON格式的消息
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(jsonResponse));
                log.info("发送给前端的消息: {}", response);
                
                return "消息发送成功";
            } catch (Exception e) {
                log.error("消息处理失败: {}", e.getMessage());
                try {
                    // 发送错误信息到前端
                    String errorMessage = e.getMessage();
                    String jsonError = String.format("{\"error\": \"%s\"}", errorMessage.replace("\"", "\\\"").replace("\n", "\\n"));
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data(jsonError));
                } catch (IOException ex) {
                    log.error("发送错误信息失败: {}", ex.getMessage());
                }
                emitters.remove(clientId);
                clientUserIdMap.remove(clientId);
                return "消息发送失败: " + e.getMessage();
            }
        }
        return "客户端不存在";
    }

    @GetMapping("/disconnect")
    public String disconnect(@RequestParam String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        clientUserIdMap.remove(clientId);
        if (emitter != null) {
            emitter.complete();
            return "断开连接成功";
        }
        return "客户端不存在";
    }

    @GetMapping("/status")
    public String getStatus() {
        return "当前连接数: " + emitters.size() + ", 活跃会话数: " + sessionManager.getSessionCount();
    }
    
    @GetMapping("/history")
    public Object getConversationHistory(@RequestParam String token, HttpServletRequest request) {
        try {
            // 从token中获取用户信息
            LoginUser loginUser = tokenService.getLoginUser(request);
            if (loginUser != null) {
                Long userId = loginUser.getUserId();
                // 获取用户的会话ID
                String sessionId = sessionManager.getSessionId(userId);
                if (sessionId != null) {
                    // 获取对话历史
                    var history = conversationHistoryManager.getConversationHistory(sessionId);
                    return Map.of("success", true, "data", history, "sessionId", sessionId);
                }
            }
            return Map.of("success", false, "message", "获取对话历史失败");
        } catch (Exception e) {
            log.error("获取对话历史失败: {}", e.getMessage());
            return Map.of("success", false, "message", "获取对话历史失败: " + e.getMessage());
        }
    }
}