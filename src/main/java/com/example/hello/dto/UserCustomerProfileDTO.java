package com.example.hello.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户端客户档案创建/更新请求。
 */
@Data
public class UserCustomerProfileDTO {
    @NotBlank(message = "name不能为空")
    @Size(max = 20, message = "name最长20个字符")
    private String name;

    /**
     * 可选：修改手机号时传入。最终手机号统一写入 user_account，并同步到 customer。
     */
    @Pattern(regexp = "^\\d{11}$", message = "phone必须为11位数字")
    private String phone;

    @Size(max = 200, message = "address最长200个字符")
    private String address;

    @Min(value = 1, message = "memberLevel取值范围为1-4")
    @Max(value = 4, message = "memberLevel取值范围为1-4")
    private Integer memberLevel;
}
