package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户端改期/更换服务师请求。
 */
@Data
public class UserAppointmentRescheduleDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;
    private Integer empId;
}
