package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户端创建预约请求。
 */
@Data
public class UserAppointmentCreateDTO {
    @NotNull(message = "petId不能为空")
    private Integer petId;
    @NotNull(message = "serviceItemId不能为空")
    private Integer serviceItemId;
    @NotNull(message = "empId不能为空")
    private Integer empId;
    @NotNull(message = "serviceTime不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;
    @Positive(message = "durationMinutes必须大于0")
    private Integer durationMinutes;
}
