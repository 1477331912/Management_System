package com.example.hello.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单表 {@code order} 对应的领域实体。
 * <p>
 * 命名为 BizOrder 而非 Order：SQL 中 {@code ORDER} 为保留字，表名需写成 {@code `order`}；
 * Java 里再用 Order 易与排序语义、注解名混淆，故加 Biz 前缀表示业务订单。
 * <p>
 * 除持久化字段外，列表查询会临时填充客户/宠物/项目/员工名称及状态文案，便于一条 JSON 返回前端。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BizOrder {

    private Integer id;
    private String orderNo;
    /** 下单用户ID（逻辑关联 user_account.id） */
    private Integer userAccountId;
    private Integer customerId;
    private Integer petId;
    private Integer serviceItemId;
    private Integer empId;

    /** 预约开始时间；列表等接口使用 {@code yyyy-MM-dd HH:mm:ss} 与文档示例一致 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;

    /** 预约结束时间（由开始时间 + 时长计算并落库） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceEndTime;

    private Integer durationMinutes;
    private Integer status;
    /** 非表字段：由业务层根据 status 转成中文，供列表展示 */
    private String statusDesc;
    private Integer rating;
    /** 对应表字段 comment，MyBatis 映射列名需反引号 */
    private String comment;
    /** 取消原因（仅 status=4 时通常有值） */
    private String cancelReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;

    // —— 仅分页列表联查填充，表中无下列列 ——
    private String customerName;
    private String petNickname;
    private String serviceItemName;
    private String empName;
}
