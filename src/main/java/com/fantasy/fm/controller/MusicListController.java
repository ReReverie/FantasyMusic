package com.fantasy.fm.controller;

import com.fantasy.fm.domain.dto.OperaMusicListDTO;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.vo.MusicListDetailVO;
import com.fantasy.fm.domain.vo.MusicListVO;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 添加音乐到歌单
     */
    @PostMapping("/addMusic")
    public Result<Void> addMusicToList(@RequestBody OperaMusicListDTO dto) {
        //TODO 获取当前用户信息
        Long userId = 1L;
        log.info("User {} Adding music {} to music list {}", userId, dto.getMusicListId(), dto.getMusicId());
        musicListService.addMusicToList(userId, dto.getMusicListId(), dto.getMusicId());
        return Result.success();
    }

    /**
     * 获取歌单列表
     */
    @GetMapping("/list")
    public Result<List<MusicListVO>> getMusicLists() {
        //TODO 获取当前用户信息
        //假设当前用户ID是1L
        Long userId = 1L;
        log.info("Fetching music lists for user {}", userId);
        return Result.success(musicListService.getMusicListsByUserId(userId));
    }

    /**
     * 获取歌单详情
     */
    @GetMapping("/{id}")
    public Result<MusicListDetailVO> getMusicListDetail(@PathVariable Long id) {
        log.info("Fetching details for music list {}", id);
        return Result.success(musicListService.getDetailById(id));
    }

    /**
     * 删除歌单中的音乐
     */
    @DeleteMapping("/removeMusic")
    public Result<Void> removeMusicFromList(@RequestBody OperaMusicListDTO dto) {
        //TODO 获取当前用户信息
        Long userId = 1L;
        log.info("User {} Removing music {} from music list {}", userId, dto.getMusicListId(), dto.getMusicId());
        musicListService.removeMusicFromList(userId, dto.getMusicListId(), dto.getMusicId());
        return Result.success();
    }

    /**
     * 删除歌单
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMusicList(@PathVariable Long id) {
        //TODO 获取当前用户信息
        Long userId = 1L;
        log.info("User {} Deleting music list {}", userId, id);
        musicListService.removeById(id);
        return Result.success();
    }
}
