package cn.qihangerp.oms;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



//@EnableDiscoveryClient
//@MapperScan("com.qihang.oms.mapper")
@ComponentScan(basePackages={"cn.qihangerp.oms","cn.qihangerp"})
@SpringBootApplication
public class OmsApi {
    public static void main( String[] args )
    {
        System.out.println( "Hello oms-api!" );
        SpringApplication.run(OmsApi.class, args);
    }

}
