package cn.qihangerp.sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 *
 */

@EnableDiscoveryClient
@ComponentScan(basePackages={"cn.qihangerp"})
//@MapperScan("com.qihang.sys.api.mapper")
@SpringBootApplication
public class SysApi
{
    public static void main( String[] args )
    {
        System.out.println( "Hello sys-api!" );
        SpringApplication.run(SysApi.class, args);
    }


}
