package com.example.hello.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户实体，对应表 {@code customer}。
 */
@Data
public class Customer {
    private Integer id;
    /** 关联用户账号ID（逻辑关联 user_account.id） */
    private Integer userAccountId;

    /** 客户姓名，最长 20 字符（与表一致） */
    private String name;

    /** 手机号，11 位 */
    private String phone;

    /** 地址，最长 200 字符 */
    private String address;

    /** 会员等级：1 普通、2 白银、3 黄金、4 钻石 */
    private Integer memberLevel;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;
}
