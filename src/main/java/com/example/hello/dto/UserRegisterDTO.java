package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户端注册请求。
 */
@Data
public class UserRegisterDTO {
    @NotBlank(message = "username不能为空")
    @Size(min = 4, max = 30, message = "username长度需在4-30之间")
    private String username;

    @NotBlank(message = "phone不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "phone必须为11位数字")
    private String phone;

    @NotBlank(message = "password不能为空")
    @Size(min = 6, max = 32, message = "password长度需在6-32之间")
    private String password;

    @Size(max = 30, message = "nickname最长30个字符")
    private String nickname;

    @Size(max = 20, message = "customerName最长20个字符")
    private String customerName;
}
