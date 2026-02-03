package com.fantasy.fm.handler;

import com.fantasy.fm.exception.BaseException;
import com.fantasy.fm.constant.MusicListConstant;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器, 处理业务中可能出现的异常
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MusicService musicService;

    /**
     * 捕获基本业务异常
     */
    @ExceptionHandler
    public Result<Void> exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 歌曲已在歌单中异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<Void> SqlDuplicateHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("异常信息：{}", ex.getMessage());
        //处理异常信息,获取音乐ID
        String musicIdStr = ex.getMessage()
                .split(" ")[2]
                .split("-")[1]
                .replace("'", "");
        Long musicId = Long.valueOf(musicIdStr);
        String title = musicService.getById(musicId).getTitle();
        return Result.error(title + " " + MusicListConstant.MUSIC_ALREADY_IN_LIST);
        //return Result.error(MusicListConstant.MUSIC_ALREADY_IN_LIST);
    }
}
