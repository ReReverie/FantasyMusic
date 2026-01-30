package com.fantasy.fm.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.mapper.MusicManagerMapper;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import com.fantasy.fm.service.MusicManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * MusicManagerServer 服务实现类
 */
@Slf4j
@Service
public class MusicManagerServiceImpl extends ServiceImpl<MusicManagerMapper, MusicFileInfo> implements MusicManagerService {

    @Override
    public File saveFile(MultipartFile multipartFile, String fileHash) {
        if (getByFileHash(fileHash) != null){
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
