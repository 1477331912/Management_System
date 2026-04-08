package com.example.hello.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果。
 * <p>
 * code：1成功，0失败；msg：提示信息；data：返回数据（可为null）。
 *
 * @param <T> data类型
 */
@Data
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应码：1成功，0失败。
     */
    private Integer code;

    /**
     * 提示信息。
     */
    private String msg;

    /**
     * 返回数据。
     */
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.msg = "success";
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }
}

