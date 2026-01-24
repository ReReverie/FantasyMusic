package com.fantasy.fm.controller;

import com.fantasy.fm.pojo.entity.MusicFileInfo;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicManagerServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 音乐文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/music/upload")
@RequiredArgsConstructor
public class UploadController {

    private final MusicManagerServer musicManagerServer;

    /**
     * 上传音乐文件
     *
     * @param multipartFile 上传的音乐文件
     */
    @PostMapping
    public Result<Void> uploadMusic(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        log.info("上传的文件名: {}", originalFilename);
        // 处理文件保存逻辑
        String filePath = null, fileType = null;
        if (originalFilename != null) {
            fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String path = "D:\\Dev\\FantasyMusic\\musicfile";
            filePath = path + "\\" + originalFilename;
            // 保存文件到指定路径
            multipartFile.transferTo(new File(filePath));
        }
        //保存音乐文件信息到数据库
        MusicFileInfo mfi = MusicFileInfo.builder()
                .musicId(1L) // TODO 关联的音乐实体ID
                .fileName(originalFilename)
                .filePath(filePath)
                .fileSize(multipartFile.getSize())
                .fileType(fileType)
                .uploadTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        musicManagerServer.save(mfi);
        return Result.success();
    }
}
