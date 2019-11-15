package com.example.redis.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jackie wang
 * @Title: App
 * @ProjectName redis-spring-boot-starter-master
 * @Description: TODO
 * @date 2019/10/30 11:28
 */
@SpringBootApplication(scanBasePackages = {"com.example.redis.crud", "com.example.redis.crud.starter"})
public class App {

    public static void main(String[] args) {

        init();
        SpringApplication.run(App.class, args);
    }

    private static void init() {

    }

}
