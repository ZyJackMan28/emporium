package com.emporium.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ep.worker")
public class IdWorkerProperties {
    private long dataCenterId;
    private long workerId;
}

