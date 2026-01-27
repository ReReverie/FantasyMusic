package com.fantasy.fm.controller;

import com.fantasy.fm.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
