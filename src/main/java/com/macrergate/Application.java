package com.macrergate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.komamitsu.spring.data.sqlite.EnableSqliteRepositories;

@SpringBootApplication
@EnableScheduling
@EnableSqliteRepositories(basePackages = "com.macrergate.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
