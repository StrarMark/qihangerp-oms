package cn.qihangerp.erp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.qihangerp.erp.serviceImpl.AiService;

import java.io.IOException;
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
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    
    @Autowired
    private AiService aiService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String clientId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(clientId, emitter);

        // 设置超时处理
        emitter.onTimeout(() -> emitters.remove(clientId));
        emitter.onCompletion(() -> emitters.remove(clientId));

        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("连接成功"));
        } catch (IOException e) {
            emitters.remove(clientId);
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
            }
        }, 30, 30, TimeUnit.SECONDS);

        return emitter;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String clientId, @RequestParam String message, @RequestParam(required = false, defaultValue = "llama3") String model) {
        log.info("=============来新消息了！");
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                // 使用AiService处理消息，传递模型参数
                String response = aiService.processMessage(message, model);
                
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(response));
                
                return "消息发送成功";
            } catch (Exception e) {
                log.error("消息处理失败: {}", e.getMessage());
                emitters.remove(clientId);
                return "消息发送失败";
            }
        }
        return "客户端不存在";
    }

    @GetMapping("/disconnect")
    public String disconnect(@RequestParam String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        if (emitter != null) {
            emitter.complete();
            return "断开连接成功";
        }
        return "客户端不存在";
    }

    @GetMapping("/status")
    public String getStatus() {
        return "当前连接数: " + emitters.size();
    }
}