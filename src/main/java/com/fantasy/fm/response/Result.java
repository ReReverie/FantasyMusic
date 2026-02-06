package com.fantasy.fm.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一API响应结果封装
 * @param <T> 响应数据类型
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static Result<Void> success() {
        return new Result<>(1, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }

    public static Result<Void> success(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static Result<Void> error() {
        return new Result<>(0, "error", null);
    }

    public static Result<Void> error(String message) {
        return new Result<>(0, message, null);
    }

    public static Result<Void> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
