package com.fantasy.fm.controller;

import com.fantasy.fm.context.BaseContext;
import com.fantasy.fm.domain.dto.OperaMusicListDTO;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.dto.UpdateMusicListDTO;
import com.fantasy.fm.domain.query.MusicListDetailQuery;
import com.fantasy.fm.domain.vo.MusicListDetailVO;
import com.fantasy.fm.domain.vo.MusicListVO;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/musiclist")
@RequiredArgsConstructor
@Tag(name = "歌单管理", description = "歌单的创建、查询、删除等接口")
public class MusicListController {

    private final MusicListService musicListService;

    /**
     * 创建歌单
     */
    @Operation(summary = "创建歌单", description = "创建新的音乐歌单")
    @PostMapping("/create")
    public Result<Void> createMusicList(@RequestBody CreateMusicListDTO createMusicListDTO) {
        log.info("Creating music list: {}", createMusicListDTO);
        createMusicListDTO.setUserId(BaseContext.getCurrentId());
        musicListService.createMusicList(createMusicListDTO);
        return Result.success();
    }

    /**
     * 添加音乐到歌单
     */
    @Operation(summary = "添加音乐到歌单", description = "将指定音乐添加到指定歌单中")
    @PostMapping("/addMusic")
    public Result<Void> addMusicToList(@RequestBody OperaMusicListDTO dto) {
        dto.setUserId(BaseContext.getCurrentId());
        log.info("User {} Adding music {} to music list {}", dto.getUserId(), dto.getMusicListId(), dto.getMusicId());
        musicListService.addMusicToList(dto);
        return Result.success();
    }

    /**
     * 获取歌单列表
     */
    @Operation(summary = "获取歌单列表", description = "获取当前用户的所有歌单列表")
    @GetMapping("/list")
    public Result<List<MusicListVO>> getMusicLists() {
        Long userId = BaseContext.getCurrentId();
        log.info("Fetching music lists for user {}", userId);
        return Result.success(musicListService.getMusicListsByUserId(userId));
    }

    /**
     * 获取歌单详情
     */
    @Operation(summary = "获取歌单详情", description = "根据歌单ID获取歌单的详细信息")
    @GetMapping("/{id}")
    public Result<MusicListDetailVO> getMusicListDetail(
            @PathVariable Long id,
            MusicListDetailQuery query) {
        log.info("Fetching details for music list {}, queryConditions {}", id, query);
        query.setMusicListId(id);
        query.setUserId(BaseContext.getCurrentId());
        return Result.success(musicListService.getDetailById(query));
    }

    /**
     * 歌单详情内搜索
     */
    @Operation(summary = "歌单详情内搜索", description = "在指定歌单中搜索音乐")
    @GetMapping("/{id}/search")
    public Result<MusicListDetailVO> searchInMusicList(
            @PathVariable Long id,
            MusicListDetailQuery query) {
        log.info("Searching in music list {}, queryConditions {}", id, query);
        query.setMusicListId(id);
        query.setUserId(BaseContext.getCurrentId());
        return Result.success(musicListService.getDetailQuery(query));
    }

    /**
     * 删除歌单中的音乐
     */
    @Operation(summary = "删除歌单中的音乐", description = "将指定音乐从指定歌单中移除")
    @DeleteMapping("/removeMusic")
    public Result<Void> removeMusicFromList(@RequestBody OperaMusicListDTO dto) {
        dto.setUserId(BaseContext.getCurrentId());
        log.info("User {} Removing music {} from music list {}", dto.getUserId(), dto.getMusicId(), dto.getMusicListId());
        musicListService.removeMusicFromList(dto);
        return Result.success();
    }

    /**
     * 删除歌单
     */
    @Operation(summary = "删除歌单", description = "根据歌单ID删除指定歌单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMusicList(@PathVariable Long id) {
        Long userId = BaseContext.getCurrentId();
        log.info("User {} Deleting music list {}", userId, id);
        musicListService.deleteMusicList(userId, id);
        return Result.success();
    }

    /**
     * 修改歌单信息
     */
    @Operation(summary = "修改歌单信息", description = "更新指定歌单的基本信息")
    @PutMapping("/update")
    public Result<Void> updateMusicList(@RequestBody UpdateMusicListDTO updateDTO) {
        log.info("Updating music list: {}", updateDTO);
        updateDTO.setUserId(BaseContext.getCurrentId());
        musicListService.updateMusicList(updateDTO);
        return Result.success();
    }
}
