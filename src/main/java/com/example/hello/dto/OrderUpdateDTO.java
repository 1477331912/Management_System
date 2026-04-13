package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 修改订单请求体，对应 PUT /orders（文档 8.4）。
 * <p>
 * 用途：在「待确认」状态下改期、换服务师、换宠物或换服务项目；字段均可选除 id 外，
 * Service 层对未传字段做「保留原值」合并。业务上仅 status=1 允许修改，由 Service 校验。
 */
@Data
public class OrderUpdateDTO {

    @NotNull(message = "id不能为空")
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;

    private Integer empId;
    private Integer petId;
    private Integer serviceItemId;
}
