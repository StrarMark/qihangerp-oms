package cn.qihangerp.open;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */
@EnableFeignClients(basePackages = "cn.qihangerp.open")
//@MapperScan("cn.qihangerp.open.mapper")
@ComponentScan(basePackages={"cn.qihangerp"})
@SpringBootApplication
public class OpenApi
{
    public static void main( String[] args )
    {
        System.out.println( "Hello open!" );
        SpringApplication.run(OpenApi.class, args);
    }
}
