package com.fantasy.fm.controller;

import com.fantasy.fm.annotation.AutoPermissionCheck;
import com.fantasy.fm.enums.OperationPermission;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicService;
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
public class MusicController {

    private final MusicService musicService;

    /**
     * 播放音乐
     *
     * @param musicId 音乐ID
     */
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
    @AutoPermissionCheck(OperationPermission.MUSIC_DELETE)
    @DeleteMapping()
    public Result<Void> deleteMusicBatch(@RequestParam List<Long> ids) {
        log.info("批量删除音乐: ids={}", ids);
        musicService.batchDeleteMusicByIds(ids);
        return Result.success();
    }
}
