package com.fantasy.fm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.entity.MusicListTrack;
import com.fantasy.fm.domain.vo.MusicListDetailVO;
import com.fantasy.fm.mapper.MusicListMapper;
import com.fantasy.fm.mapper.MusicListTrackMapper;
import com.fantasy.fm.mapper.MusicMapper;
import com.fantasy.fm.service.MusicListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicListServiceImpl extends ServiceImpl<MusicListMapper, MusicList> implements MusicListService {

    private final MusicListMapper musicListMapper;
    private final MusicListTrackMapper musicListTrackMapper;
    private final MusicMapper musicMapper;

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

    @Override
    public void addMusicToList(Long userId, Long musicListId, Long musicId) {
        MusicList musicList = musicListMapper.selectById(musicListId);
        if (musicList == null || !musicList.getUserId().equals(userId)) {
            log.error("找不到音乐列表或用户未授权：musicListId={}， userId={}", musicListId, userId);
            // throw new RuntimeException("找不到音乐列表或用户未授权");
            return;
        }
        MusicListTrack musicListTrack = MusicListTrack.builder()
                .musicListId(musicListId)
                .musicId(musicId)
                .createTime(LocalDateTime.now())
                .build();
        // 往MusicListTrack表中插入记录
        musicListTrackMapper.insert(musicListTrack);
    }

    @Override
    public MusicListDetailVO getDetailById(Long id) {
        MusicList musicList = musicListMapper.selectById(id);
        //健壮性检查,如果musicList为空,表示没有找到对应的歌单
        if (musicList == null) {
            log.error("找不到对应的歌单：id={}", id);
            return null;
        }
        MusicListDetailVO vo = new MusicListDetailVO();
        BeanUtils.copyProperties(musicList, vo);
        List<MusicListTrack> musicListTracks = musicListTrackMapper.selectList(
                new LambdaQueryWrapper<MusicListTrack>()
                        .eq(MusicListTrack::getMusicListId, id));
        //根据musicListTracks获取对应的MusicID获取音乐列表
        List<Music> musicLists = musicListTracks.stream()
                .map(musicListTrack -> musicMapper.selectById(musicListTrack.getMusicId()))
                .toList();
        vo.setMusics(musicLists);
        return vo;
    }

}
