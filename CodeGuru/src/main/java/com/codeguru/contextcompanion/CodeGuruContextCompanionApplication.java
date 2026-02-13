package com.codeguru.contextcompanion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodeGuruContextCompanionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeGuruContextCompanionApplication.class, args);
    }
}
