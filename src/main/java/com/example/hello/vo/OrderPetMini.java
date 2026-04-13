package com.example.hello.vo;

import lombok.Data;

/**
 * 订单详情里「宠物」的精简视图。
 * <p>
 * 用途：详情页展示「哪只宠物」的服务，文档要求昵称与品种即可；
 * 体重、疫苗等与此处无关，故从 {@link com.example.hello.entity.Pet} 中抽最小子集，减小响应体积、语义更清晰。
 */
@Data
public class OrderPetMini {
    private Integer id;
    private String nickname;
    private String breed;
}
