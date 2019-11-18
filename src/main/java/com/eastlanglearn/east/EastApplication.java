package com.eastlanglearn.east;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class EastApplication {
    public static void main(String[] args) {
        SpringApplication.run(EastApplication.class, args);
    }

}

