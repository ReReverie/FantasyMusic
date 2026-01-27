package com.fantasy.fm.controller;

import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/musiclist")
@RequiredArgsConstructor
public class MusicListController {

    private final MusicListService musicListService;

    /**
     * 创建歌单
     */
    @PostMapping("/create")
    public Result<Void> createMusicList(@RequestBody CreateMusicListDTO createMusicListDTO) {
        //TODO 获取当前用户信息
        log.info("Creating music list: {}", createMusicListDTO);
        musicListService.createMusicList(createMusicListDTO);
        return Result.success();
    }
}
