package com.fantasy.fm.controller;

import com.fantasy.fm.context.BaseContext;
import com.fantasy.fm.domain.dto.OperaMusicListDTO;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.dto.PageDTO;
import com.fantasy.fm.domain.query.MusicListDetailQuery;
import com.fantasy.fm.domain.query.MusicListPageQuery;
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
     * 分页获取歌单列表
     */
    @Operation(summary = "分页获取歌单列表", description = "根据分页参数获取当前用户的歌单列表")
    @GetMapping("/page")
    public Result<PageDTO<MusicListVO>> getMusicListPage(MusicListPageQuery query) {
        query.setUserId(BaseContext.getCurrentId());
        log.info("分页查询音乐列表,当前用户{}: pageNum={}, pageSize={}", query.getUserId(), query.getPageNum(), query.getPageSize());
        return Result.success(musicListService.queryMusicListPage(query));
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
        return Result.success(musicListService.getDetailByIdOrQuery(query));
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
        musicListService.removeById(id);
        return Result.success();
    }
}
