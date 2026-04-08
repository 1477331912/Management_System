package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增部门请求参数。
 */
@Data
public class DeptCreateDTO {

    @NotBlank(message = "name不能为空")
    @Size(min = 2, max = 10, message = "name长度必须为2-10位")
    private String name;
}

