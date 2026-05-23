package cn.qihangerp.erp.controller.ai;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Ollama AI模型Controller
 */
@Slf4j
@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/ai-agent")
public class OllamaController {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";

    @GetMapping(value = "/ollama/models")
    public ResponseEntity<?> getOllamaModels() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = OLLAMA_BASE_URL + "/api/tags";
            Map<String, Object> models = restTemplate.getForObject(url, Map.class);
            return ResponseEntity.ok(models);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("msg", "Error connecting to Ollama: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
