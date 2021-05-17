package com.emporium.fileupload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "image.upload")
public class ImageProperties {
    private String baseUrl;
    private List<String> allowType;
}
