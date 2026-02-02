package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.dto.OperaMusicListDTO;
import com.fantasy.fm.domain.dto.PageDTO;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.query.MusicListDetailQuery;
import com.fantasy.fm.domain.query.MusicListPageQuery;
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
    MusicListDetailVO getDetailByIdOrQuery(MusicListDetailQuery query);

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
     * 分页查询歌单列表
     */
    PageDTO<MusicListVO> queryMusicListPage(MusicListPageQuery query);
}
