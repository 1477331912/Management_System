package com.example.hello.vo;

import lombok.Data;

/**
 * 订单详情里「客户」的精简视图。
 * <p>
 * 用途：接口 8.2 要求详情中嵌套 customer 对象，但只需 id、姓名、手机用于展示与联系，
 * 不必返回客户表全部字段（如地址、会员等级等），故单独定义小 VO，避免与 {@link com.example.hello.entity.Customer} 混用导致字段过多。
 */
@Data
public class OrderCustomerMini {
    private Integer id;
    private String name;
    private String phone;
}
