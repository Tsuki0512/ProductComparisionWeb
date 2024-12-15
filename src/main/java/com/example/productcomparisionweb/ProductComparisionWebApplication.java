package com.example.productcomparisionweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.productcomparisionweb.mapper")
public class ProductComparisionWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductComparisionWebApplication.class, args);
    }

}
