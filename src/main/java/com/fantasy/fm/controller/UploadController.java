package com.fantasy.fm.controller;

import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicService;
import com.fantasy.fm.service.MusicManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 音乐文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/music/upload")
@RequiredArgsConstructor
public class UploadController {

    private final MusicService musicInfoService;
    private final MusicManagerService musicManagerServer;

    /**
     * 上传音乐文件
     *
     * @param multipartFile 上传的音乐文件
     */
    @PostMapping
    public Result<Void> uploadMusic(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        log.info("上传的文件名: {}", multipartFile.getOriginalFilename());
        File musicFile = musicManagerServer.saveFile(multipartFile);
        if (musicFile == null) {
            return Result.error("文件上传失败");
        }
        musicInfoService.saveFileInfo(musicFile);
        return Result.success();
    }
}
