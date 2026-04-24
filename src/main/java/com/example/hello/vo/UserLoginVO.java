package com.example.hello.vo;

import lombok.Data;

/**
 * 用户端登录响应。
 */
@Data
public class UserLoginVO {
    private Integer userId;
    private Integer customerId;
    private String username;
    private String phone;
    private String token;
}
