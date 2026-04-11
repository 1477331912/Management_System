package com.example.hello.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端配置类。
 * <p>
 * 用于向 Spring 容器注册 {@link MinioClient}，避免在业务代码中重复构建客户端。
 */
@Configuration
public class OssConfig {

    /**
     * 构建对象存储客户端。
     * <p>
     * 上传走内网地址，降低延迟并避免公网链路依赖。
     *
     * @param ossProperties 对象存储配置
     * @return MinIO 客户端
     */
    @Bean
    public MinioClient minioClient(OssProperties ossProperties) {
        return MinioClient.builder()
                .endpoint(ossProperties.getInternalEndpoint())
                .credentials(ossProperties.getAccessKey(), ossProperties.getSecretKey())
                .build();
    }
}

