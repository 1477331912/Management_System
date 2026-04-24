package com.example.hello.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目实体，对应表 {@code service_item}。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceItem {
    private Integer id;
    private String name;
    private Integer deptId;
    /** 关联部门名称（列表/详情） */
    private String deptName;
    private BigDecimal price;
    private Integer durationMinutes;
    private String qualificationRequired;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
