package com.example.hello.service.impl;

import com.example.hello.config.OssProperties;
import com.example.hello.service.UploadService;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传业务实现。
 * <p>
 * 上传流程：
 * 1) 检查桶是否存在，不存在则创建；
 * 2) 生成带日期目录的唯一对象名；
 * 3) 上传文件流到对象存储；
 * 4) 返回可供前端访问的公网 URL。
 */
@Service
public class UploadServiceImpl implements UploadService {
    /**
     * 对象路径的日期目录格式，例如 2026/04/08。
     */
    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 对象存储客户端。
     */
    private final MinioClient minioClient;

    /**
     * 对象存储参数。
     */
    private final OssProperties ossProperties;

    public UploadServiceImpl(MinioClient minioClient, OssProperties ossProperties) {
        this.minioClient = minioClient;
        this.ossProperties = ossProperties;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            String bucket = ossProperties.getBucket();
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                throw new IllegalStateException("存储桶不存在，请先在对象存储中创建: " + bucket);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') >= 0) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            // 对象名使用“日期目录 + UUID”，避免重名并便于按天归档管理。
            String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
            String objectName = datePath + "/" + UUID.randomUUID().toString().replace("-", "") + extension;

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }
            return buildPublicUrl(bucket, objectName);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 组装文件公网访问地址。
     *
     * @param bucket     存储桶名
     * @param objectName 对象名
     * @return 完整公网 URL
     */
    private String buildPublicUrl(String bucket, String objectName) {
        String endpoint = ossProperties.getExternalEndpoint();
        // 去除结尾 "/"，避免 URL 出现双斜杠。
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + bucket + "/" + objectName;
    }
}

