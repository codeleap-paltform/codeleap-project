package com.example.codeleapbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.codeleapbackend.mapper")
public class CodeleapBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeleapBackendApplication.class, args);
    }
}
