package cn.qihangerp.open;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */
@ComponentScan(basePackages={"cn.qihangerp.open","cn.qihangerp"})
@SpringBootApplication
public class OpenApi
{
    public static void main( String[] args )
    {
        System.out.println( "Hello open!" );
        SpringApplication.run(OpenApi.class, args);
    }
}
