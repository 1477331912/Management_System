package com.example.hello.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 「订单评价」成功后的响应（文档 8.6.3）。
 * <p>
 * 用途：评价提交后前端需确认星级、评语与更新时间，其它订单字段非必需，故单独定义。
 */
@Data
public class OrderRatingResultVO {
    private Integer id;
    private Integer rating;
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;
}
