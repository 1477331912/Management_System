package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 修改排班请求参数。
 */
@Data
public class EmpScheduleUpdateDTO {
    @NotNull(message = "排班ID不能为空")
    private Integer id;

    @NotNull(message = "员工ID不能为空")
    private Integer empId;

    @NotNull(message = "排班日期不能为空")
    private LocalDate workDate;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "排班类型不能为空")
    @Min(value = 1, message = "排班类型不合法")
    @Max(value = 5, message = "排班类型不合法")
    private Integer scheduleType;

    @Min(value = 0, message = "最大预约数不能小于0")
    private Integer maxAppointments;

    private String remark;
}
