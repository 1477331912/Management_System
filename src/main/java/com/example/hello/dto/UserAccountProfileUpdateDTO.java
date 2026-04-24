package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户端账号资料修改请求（用户名、昵称）。
 */
@Data
public class UserAccountProfileUpdateDTO {
    @NotBlank(message = "username不能为空")
    @Size(min = 4, max = 30, message = "username长度需在4-30之间")
    private String username;

    @Size(max = 30, message = "nickname最长30个字符")
    private String nickname;
}
