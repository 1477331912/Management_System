package com.example.hello.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 「PATCH 更新订单状态」成功后的响应（文档 8.5.3）。
 * <p>
 * 用途：只回传 id、新状态、可读 statusDesc、updateTime，满足前端局部刷新；
 * 不返回整单全量，避免与详情接口职责重叠。
 */
@Data
public class OrderStatusPatchVO {
    private Integer id;
    private Integer status;
    private String statusDesc;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;
}
