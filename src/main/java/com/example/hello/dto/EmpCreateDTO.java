package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 新增员工请求参数。
 */
@Data
public class EmpCreateDTO {
    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[A-Za-z]{2,20}$", message = "username必须为2-20位字母")
    private String username;

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "name必须为2-10位汉字")
    private String name;

    @NotNull(message = "gender不能为空")
    @Min(value = 1, message = "gender取值必须为1或2")
    @Max(value = 2, message = "gender取值必须为1或2")
    private Integer gender;

    @NotBlank(message = "phone不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "phone必须为11位数字")
    private String phone;

    @DecimalMin(value = "0.00", message = "salary不能为负数")
    private BigDecimal salary;

    private Integer deptId;

    @Min(value = 1, message = "position取值范围为1-5")
    @Max(value = 5, message = "position取值范围为1-5")
    private Integer position;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;

    private String image;

    @Valid
    private List<EmpExprDTO> exprList;
}

