package com.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChemicalManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChemicalManagementApplication.class, args);
    }
}
