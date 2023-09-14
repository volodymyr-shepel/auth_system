package com.ackerman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication//(
//        scanBasePackages = {
//                "com.ackerman.clients.notification"
//        }
//)
public class EmailApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmailApplication.class,args);
    }
}
