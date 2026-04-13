package com.example.hello.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改客户请求体（必须含 id、name、phone；address、member_level 选填）。
 */
@Data
public class CustomerUpdateDTO {
    @NotNull(message = "id不能为空")
    private Integer id;

    @NotBlank(message = "name不能为空")
    @Size(max = 20, message = "name最长20个字符")
    private String name;

    @NotBlank(message = "phone不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "phone必须为11位数字")
    private String phone;

    @Size(max = 200, message = "address最长200个字符")
    private String address;

    @Min(value = 1, message = "memberLevel取值范围为1-4")
    @Max(value = 4, message = "memberLevel取值范围为1-4")
    private Integer memberLevel;
}
