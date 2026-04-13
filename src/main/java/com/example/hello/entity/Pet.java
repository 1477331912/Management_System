package com.example.hello.entity;

import com.example.hello.vo.PetCustomerInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物实体，对应表 {@code pet}。
 * <p>
 * 列表接口填充 {@link #customerName}；详情接口填充 {@link #customer}。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pet {
    private Integer id;
    private String nickname;
    private String breed;
    private Integer gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private BigDecimal weight;
    private Integer vaccineStatus;
    private String allergyHistory;
    private String image;
    private Integer customerId;

    /** 分页列表：关联客户姓名 */
    private String customerName;

    /** 详情：所属客户基本信息 */
    private PetCustomerInfo customer;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;
}
