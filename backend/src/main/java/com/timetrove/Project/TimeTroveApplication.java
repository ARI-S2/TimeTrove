package com.timetrove.Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class TimeTroveApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeTroveApplication.class, args);
    }

}
