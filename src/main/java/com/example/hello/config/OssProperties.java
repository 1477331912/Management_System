package com.example.hello.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 对象存储配置参数。
 * <p>
 * 该类通过 {@code @ConfigurationProperties(prefix = "oss")} 自动绑定
 * {@code application.properties} 中的 {@code oss.*} 配置项，供上传模块统一使用。
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    /**
     * Access Key。
     */
    private String accessKey;

    /**
     * Secret Key。
     */
    private String secretKey;

    /**
     * 内网访问地址（用于服务端上传）。
     */
    private String internalEndpoint;

    /**
     * 外网访问地址（用于返回给前端访问）。
     */
    private String externalEndpoint;

    /**
     * 存储桶名称。
     */
    private String bucket;
}

