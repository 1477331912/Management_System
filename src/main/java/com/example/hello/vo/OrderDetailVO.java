package com.example.hello.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 「根据 ID 查订单详情」的专用响应模型（文档 8.2）。
 * <p>
 * 用途：与分页列表的扁平结构不同，详情需要按业务对象分组（客户/宠物/项目/服务师），
 * 因此用本类聚合 {@link OrderCustomerMini} 等四个子 VO，JSON 结构与文档示例一致，避免扁平字段爆炸。
 */
@Data
public class OrderDetailVO {
    private Integer id;
    private String orderNo;

    private OrderCustomerMini customer;
    private OrderPetMini pet;
    private OrderServiceItemMini serviceItem;
    private OrderEmpMini emp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;

    private Integer durationMinutes;
    private Integer status;
    private Integer rating;
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;
}
