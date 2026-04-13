package com.example.hello.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 订单评价请求体，对应 PUT /orders/{id}/rating（文档 8.6）。
 * <p>
 * 用途：提交星级与可选文字评价；「仅已完成订单可评」属于业务规则，在 Service 中校验而非仅靠 DTO。
 */
@Data
public class OrderRatingDTO {

    @NotNull(message = "rating不能为空")
    @Min(value = 1, message = "rating范围为1-5")
    @Max(value = 5, message = "rating范围为1-5")
    private Integer rating;

    @Size(max = 500, message = "comment最长500个字符")
    private String comment;
}
