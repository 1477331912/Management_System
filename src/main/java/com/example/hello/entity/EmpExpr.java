package com.example.hello.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工工作经历实体。
 * <p>
 * 对应表：{@code emp_expr}
 */
@Data
public class EmpExpr {
    private Integer id;
    private Integer empId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String company;
    private String position;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

