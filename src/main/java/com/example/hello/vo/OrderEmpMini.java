package com.example.hello.vo;

import lombok.Data;

/**
 * 订单详情里「服务师（员工）」的精简视图。
 * <p>
 * 用途：接口 8.2.3 示例中 emp 仅需 id、姓名、电话，便于用户联系服务师；
 * 若直接返回 {@link com.example.hello.entity.Emp}，会带上 username、密码哈希、薪资等敏感或无关字段，因此单独定义 OrderEmpMini 做数据裁剪与安全边界。
 */
@Data
public class OrderEmpMini {
    private Integer id;
    private String name;
    private String phone;
}
