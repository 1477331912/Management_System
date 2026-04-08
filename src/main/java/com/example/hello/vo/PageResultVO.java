package com.example.hello.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页返回对象。
 *
 * @param <T> 数据行类型
 */
@Data
public class PageResultVO<T> {
    private Long total;
    private List<T> rows;
}

