package com.example.hello.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改服务项目请求体（接口 7.4.2）。
 */
@Data
public class ServiceItemUpdateDTO {
    @NotNull(message = "id不能为空")
    private Integer id;

    @NotBlank(message = "name不能为空")
    @Size(max = 50, message = "name最长50个字符")
    private String name;

    @NotNull(message = "deptId不能为空")
    private Integer deptId;

    @NotNull(message = "price不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "price不能为负数")
    private BigDecimal price;

    @NotNull(message = "durationMinutes不能为空")
    @Min(value = 1, message = "durationMinutes至少为1")
    private Integer durationMinutes;

    @Size(max = 200, message = "qualificationRequired最长200个字符")
    private String qualificationRequired;
}
