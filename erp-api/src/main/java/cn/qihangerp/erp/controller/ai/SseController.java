package cn.qihangerp.erp.controller.ai;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.security.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SSE AI对话Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-agent/sse")
public class SseController extends BaseController {

    private static final Map<String, Object> emitters = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @GetMapping("/connect")
    public AjaxResult connect(@RequestParam String clientId) {
        log.info("用户连接成功，clientId: {}", clientId);
        return AjaxResult.success("开源版本暂不支持SSE AI对话功能");
    }

    @GetMapping("/send")
    public AjaxResult sendMessage(@RequestParam String clientId, @RequestParam String message) {
        log.info("收到消息: {}", message);
        return AjaxResult.success("开源版本暂不支持SSE AI对话功能");
    }

    @GetMapping("/disconnect")
    public AjaxResult disconnect(@RequestParam String clientId) {
        return AjaxResult.success("开源版本暂不支持SSE AI对话功能");
    }

    @GetMapping("/status")
    public AjaxResult getStatus() {
        return AjaxResult.success("开源版本暂不支持SSE AI对话功能");
    }

    @GetMapping("/history")
    public AjaxResult getConversationHistory(@RequestParam String token) {
        return AjaxResult.success("开源版本暂不支持SSE AI对话功能");
    }
}
