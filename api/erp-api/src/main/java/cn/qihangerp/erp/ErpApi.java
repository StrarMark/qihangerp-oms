package cn.qihangerp.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

//@EnableDiscoveryClient
//@MapperScan("cn.qihangerp.oms.mapper")
@EnableFeignClients(basePackages = "cn.qihangerp.erp")
@EnableDiscoveryClient
@ComponentScan(basePackages={"cn.qihangerp"})
@SpringBootApplication
public class ErpApi {
    public static void main( String[] args )
    {
        System.out.println( "Hello erp-api!" );
        SpringApplication.run(ErpApi.class, args);
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
