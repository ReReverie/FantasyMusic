package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.dto.OperaMusicListDTO;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.query.MusicListDetailQuery;
import com.fantasy.fm.domain.vo.MusicListDetailVO;
import com.fantasy.fm.domain.vo.MusicListVO;

import java.util.List;

public interface MusicListService extends IService<MusicList> {
    /**
     * 创建歌单
     */
    void createMusicList(CreateMusicListDTO createMusicListDTO);

    /**
     * 获取歌单详情
     */
    MusicListDetailVO getDetailById(MusicListDetailQuery query);

    /**
     * 歌单详情内搜索
     */
    MusicListDetailVO getDetailQuery(MusicListDetailQuery query);

    /**
     * 添加音乐到歌单
     */
    void addMusicToList(OperaMusicListDTO operaMusicListDTO);

    /**
     * 获取用户的歌单列表
     */
    List<MusicListVO> getMusicListsByUserId(Long userId);

    /**
     * 从歌单中移除音乐
     */
    void removeMusicFromList(OperaMusicListDTO operaMusicListDTO);

    /**
     * 删除歌单
     */
    void deleteMusicList(Long userId, Long id);
}
