package com.fantasy.fm.controller;

import com.fantasy.fm.constant.MusicConstant;
import com.fantasy.fm.enums.AudioFormatEnum;
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

    private final MusicService musicService;
    private final MusicManagerService musicManagerServer;

    /**
     * 上传音乐文件
     *
     * @param multipartFile 上传的音乐文件
     * @param fileHash      前端计算的文件哈希值
     */
    @PostMapping
    public Result<Void> uploadMusic(@RequestParam("file") MultipartFile multipartFile,
                                    @RequestParam("hash") String fileHash) {
        String oF = multipartFile.getOriginalFilename();
        log.info("上传的文件名: {}, 前端计算的哈希:{}", oF, fileHash);
        //获取文件扩展名
        String ext = oF != null ? oF.substring(oF.lastIndexOf('.') + 1) : "";
        //验证是否支持该音频格式,如果不支持则抛出异常
        AudioFormatEnum.checkExt(ext);

        //计算文件哈希值
        String calculatedHash = musicManagerServer.calculateHash(multipartFile);
        if (calculatedHash == null || !calculatedHash.equals(fileHash)) {
            log.warn("文件哈希验证失败, 计算的哈希: {}, 前端的哈希: {}", calculatedHash, fileHash);
            return Result.error(MusicConstant.MUSIC_UPLOAD_FAILURE);
        }

        //保存音乐文件
        File musicFile = musicManagerServer.saveFile(multipartFile, fileHash);
        if (musicFile == null) return Result.error(MusicConstant.MUSIC_UPLOAD_FAILURE);
        musicService.saveFileInfo(musicFile, fileHash);
        return Result.success();
    }
}
