package com.fantasy.fm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.mapper.MusicManagerMapper;
import com.fantasy.fm.pojo.entity.MusicFileInfo;
import com.fantasy.fm.service.MusicManagerService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * MusicManagerServer 服务实现类
 */
@Service
public class MusicManagerServiceImpl extends ServiceImpl<MusicManagerMapper, MusicFileInfo> implements MusicManagerService {

    @Override
    public File saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        // 处理文件保存逻辑
        String filePath = null;
        if (StrUtil.isNotBlank(originalFilename)) {
            String path = System.getProperty("user.dir") + File.separator + "musicfile";
            filePath = path + "/" + originalFilename;
            File musicFile = new File(filePath);
            // 保存文件到指定路径
            multipartFile.transferTo(musicFile);
            return musicFile;
        }
        return null;
    }
}
