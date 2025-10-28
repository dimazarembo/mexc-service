package com.dzarembo.mexcservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MexcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MexcServiceApplication.class, args);
    }

}
