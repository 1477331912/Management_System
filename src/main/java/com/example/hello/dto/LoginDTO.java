package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 登录请求参数。
 */
@Data
public class LoginDTO {
    /**
     * 用户名，2-20位字母。
     */
    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[A-Za-z]{2,20}$", message = "username必须为2-20位字母")
    private String username;

    /**
     * 明文密码。
     */
    @NotBlank(message = "password不能为空")
    private String password;
}

