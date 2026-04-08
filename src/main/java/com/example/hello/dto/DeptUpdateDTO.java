package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改部门请求参数。
 */
@Data
public class DeptUpdateDTO {
    @NotNull(message = "id不能为空")
    private Integer id;

    @NotBlank(message = "name不能为空")
    @Size(min = 2, max = 10, message = "name长度必须为2-10位")
    private String name;
}

