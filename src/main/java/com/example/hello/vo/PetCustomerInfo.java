package com.example.hello.vo;

import lombok.Data;

/**
 * 宠物详情中嵌套的所属客户基本信息（接口 6.2.3）。
 */
@Data
public class PetCustomerInfo {
    private Integer id;
    private String name;
    private String phone;
    private Integer memberLevel;
}
