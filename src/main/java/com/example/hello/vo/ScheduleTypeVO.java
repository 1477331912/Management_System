package com.example.hello.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 排班类型字典项。
 */
@Data
@AllArgsConstructor
public class ScheduleTypeVO {
    private Integer value;
    private String label;
}
