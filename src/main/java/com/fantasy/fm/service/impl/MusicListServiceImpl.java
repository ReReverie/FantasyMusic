package com.fantasy.fm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.entity.MusicListTrack;
import com.fantasy.fm.domain.vo.MusicListDetailVO;
import com.fantasy.fm.domain.vo.MusicListVO;
import com.fantasy.fm.mapper.MusicListMapper;
import com.fantasy.fm.mapper.MusicListTrackMapper;
import com.fantasy.fm.mapper.MusicMapper;
import com.fantasy.fm.service.MusicListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
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
        //TODO 假设用户ID是1L
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
    public List<MusicListVO> getMusicListsByUserId(Long userId) {
        /*//健壮性检查
        if (userId == null) {
            log.error("用户ID不能为空");
            return List.of();
        }*/
        List<MusicList> list = this.lambdaQuery()
                .orderBy(true, false, MusicList::getCreateTime) // 按创建时间降序
                .eq(MusicList::getUserId, userId).list();
        List<MusicListVO> musicListVOS = list.stream().map(musicList -> {
            MusicListVO vo = new MusicListVO();
            BeanUtils.copyProperties(musicList, vo);
            return vo;
        }).toList();
        if (musicListVOS.isEmpty()) {
            return List.of();
        }
        for (MusicListVO musicListVO : musicListVOS) {
            List<Music> musicLists = getMusicList(musicListVO.getId());
            musicListVO.setMusics(musicLists);
        }
        return musicListVOS;
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
        List<Music> musicLists = getMusicList(id);
        vo.setMusics(musicLists);
        return vo;
    }

    /**
     * 根据歌单ID获取对应的音乐列表
     */
    private @NonNull List<Music> getMusicList(Long id) {
        List<MusicListTrack> musicListTracks = musicListTrackMapper.selectList(
                new LambdaQueryWrapper<MusicListTrack>()
                        .eq(MusicListTrack::getMusicListId, id));
        //根据musicListTracks获取对应的MusicID获取音乐列表
        return musicListTracks.stream()
                .map(musicListTrack -> musicMapper.selectById(musicListTrack.getMusicId()))
                .toList();
    }

}
