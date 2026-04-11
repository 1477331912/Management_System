package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.service.UploadService;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口。
 * <p>
 * 按接口文档约定提供 {@code POST /upload}，请求参数名固定为 {@code file}。
 */
@Validated
@RestController
@RequestMapping
public class UploadController {
    /**
     * 上传业务。
     */
    private final UploadService uploadService;

    /**
     * 构造注入。
     *
     * @param uploadService 上传业务
     */
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     * 上传文件。
     * <p>
     * POST /upload
     *
     * @param file 上传文件（参数名：file）
     * @return 文件可访问URL
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") @NotNull(message = "file不能为空") MultipartFile file) {
        // 防御式校验：空文件不允许上传。
        if (file.isEmpty()) {
            return Result.error("file不能为空");
        }
        // 防御式校验：部分客户端可能上传无文件名对象。
        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            return Result.error("文件名不能为空");
        }
        try {
            return Result.success(uploadService.upload(file));
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof IllegalStateException) {
                return Result.error(ex.getCause().getMessage());
            }
            throw ex;
        }
    }
}

