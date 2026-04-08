package com.example.hello.common;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * 全局异常处理。
 * <p>
 * 统一将常见异常转换成 {@link Result} 结构返回，避免前端拿到非约定格式的错误响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验失败（@Valid / @Validated）。
     *
     * @param ex 校验异常
     * @return 失败Result
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidation(Exception ex) {
        log.warn("参数校验失败: {}", ex.getMessage(), ex);
        if (ex instanceof MethodArgumentNotValidException manv) {
            var msg = manv.getBindingResult().getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return Result.error(msg.isBlank() ? "参数校验失败" : msg);
        }
        if (ex instanceof BindException be) {
            var msg = be.getBindingResult().getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return Result.error(msg.isBlank() ? "参数校验失败" : msg);
        }
        return Result.error("参数校验失败");
    }

    /**
     * 处理请求体不可读（例如 JSON 格式错误）。
     *
     * @param ex 异常
     * @return 失败Result
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求体格式错误: {}", ex.getMessage(), ex);
        return Result.error("请求体格式错误");
    }

    /**
     * 处理请求路径不存在（404）。
     *
     * @param ex 异常
     * @return 失败Result
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResource(NoResourceFoundException ex) {
        log.warn("请求路径不存在: {}", ex.getResourcePath(), ex);
        return Result.error("请求路径不存在");
    }

    /**
     * 处理缺少请求参数。
     *
     * @param ex 异常
     * @return 失败Result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.warn("缺少请求参数: {}", ex.getParameterName(), ex);
        return Result.error("缺少请求参数: " + ex.getParameterName());
    }

    /**
     * 处理路径参数/请求参数类型不匹配。
     *
     * @param ex 异常
     * @return 失败Result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("参数类型不匹配: {}", ex.getName(), ex);
        return Result.error("参数类型不匹配: " + ex.getName());
    }

    /**
     * 处理唯一约束冲突（部门名称重复）。
     *
     * @param ex 异常
     * @return 失败Result
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicate(DuplicateKeyException ex) {
        log.warn("唯一约束冲突: {}", ex.getMessage(), ex);
        return Result.error("数据已存在，请检查唯一字段");
    }

    /**
     * 处理数据库访问异常（连接失败、SQL执行异常等）。
     *
     * @param ex 异常
     * @return 失败Result
     */
    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccess(DataAccessException ex) {
        log.error("数据库访问异常", ex);
        return Result.error("数据库访问异常，请稍后重试");
    }

    /**
     * 兜底异常处理。
     *
     * @param ex 异常
     * @return 失败Result
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception ex) {
        log.error("未处理系统异常", ex);
        return Result.error("系统异常");
    }
}

