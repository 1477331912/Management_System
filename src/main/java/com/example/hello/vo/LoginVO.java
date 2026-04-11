package com.example.hello.vo;

import lombok.Data;

/**
 * 登录成功返回数据。
 */
@Data
public class LoginVO {
    private Integer id;
    private String username;
    private String name;
    private String token;
}

