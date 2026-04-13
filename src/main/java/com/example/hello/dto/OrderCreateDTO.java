package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建订单（预约）请求体，对应 POST /orders（文档 8.3）。
 * <p>
 * 用途：承载前端传入的客户、宠物、项目、服务师与预约时间；校验注解保证必填项齐全。
 * 时长可选：不传则由服务层使用 {@code service_item.duration_minutes}（见 Service 实现）。
 */
@Data
public class OrderCreateDTO {

    @NotNull(message = "customerId不能为空")
    private Integer customerId;

    @NotNull(message = "petId不能为空")
    private Integer petId;

    @NotNull(message = "serviceItemId不能为空")
    private Integer serviceItemId;

    @NotNull(message = "empId不能为空")
    private Integer empId;

    @NotNull(message = "serviceTime不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;

    @Min(value = 1, message = "durationMinutes至少为1")
    private Integer durationMinutes;
}
