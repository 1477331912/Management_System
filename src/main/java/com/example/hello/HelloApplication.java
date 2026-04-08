package com.example.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * Spring Boot 启动类。
 * <p>
 * 扫描 MyBatis Mapper：{@code com.example.hello} 及其子包。
 */
@SpringBootApplication
@MapperScan("com.example.hello.mapper")
public class HelloApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

}
