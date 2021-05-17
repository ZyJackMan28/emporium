package com.emporium.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

//需要component注解
@Data
@ConfigurationProperties(prefix = "emp.sms")
public class SmsReadProps {
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String verifyCodeTemplate;
}
