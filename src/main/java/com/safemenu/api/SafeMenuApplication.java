package com.safemenu.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SafeMenuApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeMenuApplication.class, args);
    }
}
