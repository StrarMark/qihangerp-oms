package com.zhijian;

//import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.FilterType;


//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class ,MybatisAutoConfiguration.class})
//@ComponentScan
//@Configuration
//@EnableAutoConfiguration
@Configuration
//@EnableAutoConfiguration(exclude = MybatisAutoConfiguration.class)
@ComponentScan(basePackages = "com.zhijian",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = FrameworkAutoConfiguration.class))
public class FrameworkAutoConfiguration {
}
