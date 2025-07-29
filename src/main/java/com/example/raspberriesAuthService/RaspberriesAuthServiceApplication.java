package com.example.raspberriesAuthService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RaspberriesAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaspberriesAuthServiceApplication.class, args);
    }

}
