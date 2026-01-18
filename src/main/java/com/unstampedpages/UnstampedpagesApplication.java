package com.unstampedpages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class UnstampedpagesApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnstampedpagesApplication.class, args);
    }

}
