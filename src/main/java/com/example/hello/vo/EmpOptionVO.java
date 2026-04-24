package com.example.hello.vo;

import lombok.Data;

/**
 * 用户端服务师下拉选项。
 */
@Data
public class EmpOptionVO {
    private Integer empId;
    private String empName;
    private Integer deptId;
    private String deptName;
}
