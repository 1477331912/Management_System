package com.example.hello.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单详情里「服务项目」的精简视图。
 * <p>
 * 用途：说明本次预约对应哪项服务、标价与标准时长，满足对账与展示；
 * 部门、资质等管理字段不必出现在订单详情中，故与 {@link com.example.hello.entity.ServiceItem} 全量字段解耦。
 */
@Data
public class OrderServiceItemMini {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
}
