package com.artezio.artping.config.minio;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class MinioConfig {

    private final MinioConfigProperties minioProp;

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        MinioClient minioClient = new MinioClient(
                minioProp.getEndpoint(),
                minioProp.getPort(),
                minioProp.getAccessKey(),
                minioProp.getSecretKey()
        );
        return minioClient;
    }
}
