package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.vo.MusicListDetailVO;

public interface MusicListService extends IService<MusicList> {
    /**
     * 创建歌单
     */
    void createMusicList(CreateMusicListDTO createMusicListDTO);

    /**
     * 获取歌单详情
     */
    MusicListDetailVO getDetailById(Long id);

    /**
     * 添加音乐到歌单
     */
    void addMusicToList(Long userId, Long musicListId, Long musicId);
}
