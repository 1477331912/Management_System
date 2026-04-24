package com.example.hello.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工排班实体，对应表 emp_schedule。
 */
@Data
public class EmpSchedule {
    private Integer id;
    private Integer empId;
    private String empName;
    private Integer deptId;
    private String deptName;
    private LocalDate workDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    /** 排班类型：1可预约 2休息 3请假 4锁定 5已被预约 */
    private Integer scheduleType;
    private Integer maxAppointments;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
