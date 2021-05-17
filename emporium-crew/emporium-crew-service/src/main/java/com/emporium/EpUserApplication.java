package com.emporium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.emporium.crew.mapper")
public class EpUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpUserApplication.class, args);
    }
}