package cn.qihangerp.erp.controller;

import cn.qihangerp.sse.SseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 实时消息推送（SSE）
 */
@Slf4j
@AllArgsConstructor
@RestController
public class NotifyController {

    private final SseService sseService;

    @GetMapping(value = "/api/erp-api/sse/notify_msg", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleSse(@RequestParam(value = "clientId", required = false) String clientId) {
        if (clientId == null || clientId.isBlank()) {
            clientId = "client_" + java.util.UUID.randomUUID();
        }
        log.info("SSE 连接建立: clientId={}", clientId);
        return sseService.createConnection(clientId);
    }
}
