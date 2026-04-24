package com.example.hello.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户端取消预约请求。
 */
@Data
public class UserAppointmentCancelDTO {
    @Size(max = 200, message = "cancelReason最长200个字符")
    private String cancelReason;
}
