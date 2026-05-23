package cn.qihangerp.erp.controller.ai;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@AllArgsConstructor
@RestController
public class AiHomeController {

    @GetMapping(value = "/api/ai-agent/")
    public String home(){
        return "hello ai!";
    }

}
