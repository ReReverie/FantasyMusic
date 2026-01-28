package com.fantasy.fm.handler;

import com.fantasy.fm.Exception.BaseException;
import com.fantasy.fm.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器, 处理业务中可能出现的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获基本业务异常
     */
    @ExceptionHandler
    public Result<Void> exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
