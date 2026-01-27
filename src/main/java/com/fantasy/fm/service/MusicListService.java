package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.entity.MusicList;

public interface MusicListService extends IService<MusicList> {
    void createMusicList(CreateMusicListDTO createMusicListDTO);
}
