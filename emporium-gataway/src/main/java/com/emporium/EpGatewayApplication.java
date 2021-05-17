package com.emporium;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class EpGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(EpGatewayApplication.class,args);
    }
}
