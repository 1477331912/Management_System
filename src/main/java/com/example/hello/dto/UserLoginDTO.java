package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户端登录请求，支持用户名或手机号登录。
 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "account不能为空")
    private String account;
    @NotBlank(message = "password不能为空")
    private String password;
}
