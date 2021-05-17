package com.emporium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EpSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EpSmsApplication.class);
    }
}
