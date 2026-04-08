package com.example.hello.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工实体。
 * <p>
 * 对应表：{@code emp}，并扩展了 {@code deptName} 与 {@code exprList} 用于接口返回。
 */
@Data
public class Emp {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private Integer gender;
    private String phone;
    private BigDecimal salary;
    private Integer deptId;
    private String deptName;
    private Integer position;
    private LocalDate entryDate;
    private String image;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<EmpExpr> exprList;
}

