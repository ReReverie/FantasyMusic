package com.fantasy.fm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.entity.MusicListTrack;
import com.fantasy.fm.mapper.MusicListMapper;
import com.fantasy.fm.mapper.MusicListTrackMapper;
import com.fantasy.fm.service.MusicListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicListServiceImpl extends ServiceImpl<MusicListMapper, MusicList> implements MusicListService {

    private final MusicListMapper musicListMapper;

    @Override
    public void createMusicList(CreateMusicListDTO createMusicListDTO) {
        //假设用户ID是1L
        Long userId = 1L;
        musicListMapper.insert(MusicList.builder()
                .userId(userId)
                .title(createMusicListDTO.getTitle())
                .description(createMusicListDTO.getDescription())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now()).build());
    }
}
