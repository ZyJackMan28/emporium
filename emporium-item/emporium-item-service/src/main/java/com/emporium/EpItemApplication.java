package com.emporium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
//一定不要漏掉Mapper扫描包
@MapperScan("com.emporium.item.mapper")
public class EpItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(EpItemApplication.class,args);
    }
}
