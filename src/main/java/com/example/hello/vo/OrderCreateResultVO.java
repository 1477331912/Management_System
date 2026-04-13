package com.example.hello.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 「创建订单成功」时的响应子集（文档 8.3.3）。
 * <p>
 * 用途：新建后前端主要关心系统分配的 id、orderNo、初始 status 以及确认的预约时间与时长；
 * 不必返回联表名称等，故用专用 VO 收窄字段，与详情/列表响应区分。
 */
@Data
public class OrderCreateResultVO {
    private Integer id;
    private String orderNo;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;

    private Integer durationMinutes;
}
