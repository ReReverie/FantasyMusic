package com.fantasy.fm.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.constant.MusicConstant;
import com.fantasy.fm.constant.SystemConstant;
import com.fantasy.fm.exception.MusicFileExistsException;
import com.fantasy.fm.mapper.MusicManagerMapper;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import com.fantasy.fm.service.MusicManagerService;
import com.fantasy.fm.service.MusicService;
import com.fantasy.fm.utils.OSSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * MusicManagerServer 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MusicManagerServiceImpl extends ServiceImpl<MusicManagerMapper, MusicFileInfo> implements MusicManagerService {

    private final OSSUtil ossUtil;
    private final MusicService musicService;

    @Override
    public String saveFile2OSS(MultipartFile multipartFile, String fileHash) {
        if (getByFileHash(fileHash) != null) {
            throw new MusicFileExistsException(MusicConstant.MUSIC_FILE_EXISTS);
        }
        String originalFilename = multipartFile.getOriginalFilename();
        // 处理文件保存逻辑
        if (StrUtil.isBlank(originalFilename)) {
            return null;
        }
        File musicTempFile;
        String ossUrl = null;
        // 保存文件到指定路径
        try {
            //先读字节（流只能用一次，先读出来）
            byte[] bytes = multipartFile.getBytes();
            //创建临时文件并写入（用于解析）
            musicTempFile = File.createTempFile("music_", "_" + originalFilename);
            Files.write(musicTempFile.toPath(), bytes);
            //用字节上传
            String filePath = SystemConstant.OSS_MUSIC_DIR + originalFilename;
            ossUrl = ossUtil.upload(bytes, filePath);
            //保存结果到数据库中
            musicService.saveFileInfo(musicTempFile, fileHash, ossUrl);
        } catch (IOException e) {
            log.error("保存文件失败, {}", e.getMessage());
        }
        return ossUrl;
    }

    @Override
    public File saveFile2Local(MultipartFile multipartFile, String fileHash) {
        if (getByFileHash(fileHash) != null) {
            log.info("文件已存在, 哈希值: {}", fileHash);
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        // 处理文件保存逻辑
        if (StrUtil.isBlank(originalFilename)) {
            return null;
        }
        String filePath;
        String path = System.getProperty("user.dir") + File.separator + "musicfile";
        filePath = path + "/" + originalFilename;
        File musicFile = new File(filePath);
        // 保存文件到指定路径
        try {
            multipartFile.transferTo(musicFile);
            return musicFile;
        } catch (IOException e) {
            log.error("保存文件失败, {}", e.getMessage());
            return null;
        }
    }

    @Override
    public MusicFileInfo getByFileHash(String fileHash) {
        return this.lambdaQuery()
                .eq(MusicFileInfo::getFileHash, fileHash)
                .one();
    }

    @Override
    public String calculateHash(MultipartFile multipartFile) {
        try {
            return SecureUtil.md5(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("计算文件哈希失败, {}", e.getMessage());
            return null;
        }
    }
}
