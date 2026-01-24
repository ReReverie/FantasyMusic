package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.pojo.entity.Music;

import java.io.File;

public interface MusicInfoService extends IService<Music> {
    /**
     * 保存音乐文件信息
     *
     * @param musicFile 音乐文件
     */
    void saveFileInfo(File musicFile);
}
