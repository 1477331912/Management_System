package com.example.hello.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传业务接口。
 * <p>
 * 约定：上传成功后返回文件的公网访问 URL。
 */
public interface UploadService {
    /**
     * 上传文件并返回可访问地址。
     *
     * @param file 文件对象
     * @return 文件访问 URL
     */
    String upload(MultipartFile file);
}

