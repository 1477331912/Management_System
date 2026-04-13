package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增宠物请求体（接口 6.3.2）。
 */
@Data
public class PetCreateDTO {
    @NotBlank(message = "nickname不能为空")
    @Size(max = 20, message = "nickname最长20个字符")
    private String nickname;

    @NotBlank(message = "breed不能为空")
    @Size(max = 50, message = "breed最长50个字符")
    private String breed;

    @NotNull(message = "gender不能为空")
    @Min(value = 1, message = "gender必须为1或2")
    @Max(value = 2, message = "gender必须为1或2")
    private Integer gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private BigDecimal weight;

    @Min(value = 0, message = "vaccineStatus必须为0或1")
    @Max(value = 1, message = "vaccineStatus必须为0或1")
    private Integer vaccineStatus;

    @Size(max = 500, message = "allergyHistory最长500个字符")
    private String allergyHistory;

    @Size(max = 255, message = "image最长255个字符")
    private String image;

    @NotNull(message = "customerId不能为空")
    private Integer customerId;
}
