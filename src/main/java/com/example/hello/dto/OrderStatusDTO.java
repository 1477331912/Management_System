package com.example.hello.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新订单状态请求体，对应 PATCH /orders/{id}/status（文档 8.5）。
 * <p>
 * 用途：只携带目标状态；合法流转（如 1→2/4，2→3/4）由 Service 与文档约定共同约束。
 */
@Data
public class OrderStatusDTO {

    @NotNull(message = "status不能为空")
    @Min(value = 2, message = "status只能为2、3、4")
    @Max(value = 4, message = "status只能为2、3、4")
    private Integer status;
}
