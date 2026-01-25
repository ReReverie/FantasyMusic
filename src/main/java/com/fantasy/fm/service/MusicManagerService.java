package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * MusicManagerServer 服务类
 */
public interface MusicManagerService extends IService<MusicFileInfo> {

    /**
     * 保存文件并返回文件路径
     * @param multipartFile 上传的文件
     * @return 文件对象
     * @throws IOException 可能抛出的IO异常
     */
    File saveFile(MultipartFile multipartFile) throws IOException;
}
