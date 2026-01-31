package com.fantasy.fm.controller;

import com.fantasy.fm.annotation.AutoPermissionCheck;
import com.fantasy.fm.enums.OperationPermission;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
@Tag(name = "音乐管理", description = "音乐播放、下载、删除等接口")
public class MusicController {

    private final MusicService musicService;

    /**
     * 播放音乐
     *
     * @param musicId 音乐ID
     */
    @Operation(summary = "播放音乐", description = "根据音乐ID播放音乐文件")
    @GetMapping("/play/{id}")
    public ResponseEntity<Resource> playMusic(@PathVariable("id") Long musicId) {
        log.info("Playing music. ID: {}", musicId);
        return musicService.playMusic(musicId);
    }

    /**
     * 下载音乐
     *
     * @param musicId 音乐ID
     */
    @Operation(summary = "下载音乐", description = "根据音乐ID下载音乐文件")
    @AutoPermissionCheck(OperationPermission.DOWNLOAD)
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadMusic(@PathVariable("id") Long musicId) {
        log.info("Downloading music. ID: {}", musicId);
        return musicService.downloadMusic(musicId);
    }

    /**
     * 删除音乐
     *
     * @param musicId 音乐ID
     */
    @Operation(summary = "删除音乐", description = "根据音乐ID删除音乐文件")
    @AutoPermissionCheck(OperationPermission.MUSIC_DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> deleteMusic(@PathVariable("id") Long musicId) {
        log.info("Deleting music. ID: {}", musicId);
        musicService.deleteByMusicId(musicId);
        return Result.success();
    }


    /**
     * 批量删除音乐
     */
    @Operation(summary = "批量删除音乐", description = "根据音乐Ids列表批量删除音乐文件")
    @AutoPermissionCheck(OperationPermission.MUSIC_DELETE)
    @DeleteMapping()
    public Result<Void> deleteMusicBatch(@RequestParam List<Long> ids) {
        log.info("批量删除音乐: ids={}", ids);
        musicService.batchDeleteMusicByIds(ids);
        return Result.success();
    }
}
