package com.fantasy.fm.controller;

import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @GetMapping("/play/{id}")
    public ResponseEntity<Resource> playMusic(@PathVariable("id") Long musicId) {
        log.info("Playing music. ID: {}", musicId);
        return musicService.playMusic(musicId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadMusic(@PathVariable("id") Long musicId) {
        log.info("Downloading music. ID: {}", musicId);
        return musicService.downloadMusic(musicId);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMusic(@PathVariable("id") Long musicId) {
        log.info("Deleting music. ID: {}", musicId);
        musicService.deleteByMusicId(musicId);
        return Result.success();
    }
}
