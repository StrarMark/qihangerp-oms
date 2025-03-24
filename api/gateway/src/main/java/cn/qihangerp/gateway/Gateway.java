package cn.qihangerp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Gateway
{
    public static void main( String[] args )
    {
        System.out.println( "Hello gateway! " );
        SpringApplication.run(Gateway.class, args);
    }

//    @Bean
//    public TokenFilter tokenFilter() {
//        return new TokenFilter();
//    }
}
