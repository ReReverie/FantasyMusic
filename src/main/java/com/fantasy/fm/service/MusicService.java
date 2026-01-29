package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.dto.PageDTO;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.query.MusicPageQuery;
import com.fantasy.fm.domain.vo.MusicVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.File;

public interface MusicService extends IService<Music> {
    /**
     * 保存音乐文件信息
     *
     * @param musicFile 音乐文件
     */
    void saveFileInfo(File musicFile);

    /**
     * 播放音乐
     *
     * @param musicId 音乐ID
     */
    ResponseEntity<Resource> playMusic(Long musicId);

    /**
     * 下载音乐
     *
     * @param musicId 音乐ID
     */
    ResponseEntity<Resource> downloadMusic(Long musicId);

    /**
     * 根据音乐ID删除音乐
     *
     * @param musicId 音乐ID
     */
    void deleteByMusicId(Long musicId);

    /**
     * 分页查询音乐
     *
     * @param query 分页查询参数
     * @return 音乐列表
     */
    PageDTO<MusicVO> queryMusicPage(MusicPageQuery query);
}
