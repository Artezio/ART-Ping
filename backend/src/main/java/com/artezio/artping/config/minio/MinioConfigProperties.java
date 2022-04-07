package com.artezio.artping.config.minio;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties("minio-client")
public class MinioConfigProperties {
    private String endpoint;
    private int port;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
