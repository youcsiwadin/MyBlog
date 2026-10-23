package com.chiaya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlogProject01Application {

    public static void main(String[] args) {
        SpringApplication.run(BlogProject01Application.class, args);
    }

}
