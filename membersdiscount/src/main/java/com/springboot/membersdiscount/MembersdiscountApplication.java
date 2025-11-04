package com.springboot.membersdiscount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MembersdiscountApplication {

    public static void main(String[] args) {
        SpringApplication.run(MembersdiscountApplication.class, args);
    }



}
