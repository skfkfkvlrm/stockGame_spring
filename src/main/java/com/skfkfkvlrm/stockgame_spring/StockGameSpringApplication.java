package com.skfkfkvlrm.stockgame_spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.skfkfkvlrm.stockgame_spring.repository")
public class StockGameSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockGameSpringApplication.class, args);
    }

}
