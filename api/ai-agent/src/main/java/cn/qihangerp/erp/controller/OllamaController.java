package cn.qihangerp.erp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@CrossOrigin
public class OllamaController {

    // 默认的Ollama API地址
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";

    @GetMapping(value = "/ollama/models")
    public ResponseEntity<?> getOllamaModels() {
        try {
            // 使用RestTemplate调用Ollama API获取模型列表
            RestTemplate restTemplate = new RestTemplate();
            String url = OLLAMA_BASE_URL + "/api/tags";
            
            // 调用Ollama API获取模型列表
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
