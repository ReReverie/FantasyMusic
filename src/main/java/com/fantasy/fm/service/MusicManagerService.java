package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * MusicManagerServer 服务类
 */
public interface MusicManagerService extends IService<MusicFileInfo> {

    /**
     * 保存文件到OSS并返回文件路径
     * @param multipartFile 上传的文件
     * @param fileHash 前端计算的哈希值
     * @return 文件对象
     */
    String saveFile2OSS(MultipartFile multipartFile, String fileHash);

    /**
     * 保存文件到本地并返回文件路径
     * @param multipartFile 上传的文件
     * @param fileHash 前端计算的哈希值
     * @return 文件对象
     */
    @Deprecated
    File saveFile2Local(MultipartFile multipartFile, String fileHash);

    /**
     * 根据文件哈希获取音乐文件信息
     * @param fileHash 文件哈希值
     * @return 音乐文件信息
     */
    MusicFileInfo getByFileHash(String fileHash);

    /**
     * 计算文件的哈希值
     * @param multipartFile 上传的文件
     * @return 文件的哈希值
     */
    String calculateHash(MultipartFile multipartFile);
}
