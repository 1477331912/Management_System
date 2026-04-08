package com.example.hello.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体。
 * <p>
 * 对应表：{@code dept}
 */
@Data
public class Dept {
    /**
     * 主键ID。
     */
    private Integer id;

    /**
     * 部门名称（2-10位，唯一）。
     */
    private String name;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 最后操作时间。
     */
    private LocalDateTime updateTime;
}

