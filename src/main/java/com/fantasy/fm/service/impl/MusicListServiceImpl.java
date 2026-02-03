package com.fantasy.fm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.constant.MusicListConstant;
import com.fantasy.fm.constant.RedisCacheConstant;
import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.dto.OperaMusicListDTO;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.entity.MusicListTrack;
import com.fantasy.fm.domain.query.MusicListDetailQuery;
import com.fantasy.fm.domain.vo.MusicListDetailVO;
import com.fantasy.fm.domain.vo.MusicListVO;
import com.fantasy.fm.mapper.MusicListMapper;
import com.fantasy.fm.mapper.MusicListTrackMapper;
import com.fantasy.fm.mapper.MusicMapper;
import com.fantasy.fm.service.MusicListService;
import com.fantasy.fm.utils.RedisCacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicListServiceImpl extends ServiceImpl<MusicListMapper, MusicList> implements MusicListService {

    private final MusicListMapper musicListMapper;
    private final MusicListTrackMapper musicListTrackMapper;
    private final MusicMapper musicMapper;
    private final RedisCacheUtil redisCacheUtil;

    @Override
    @CacheEvict(value = RedisCacheConstant.USER_MUSIC_LIST, key = "#createMusicListDTO.userId")
    public void createMusicList(CreateMusicListDTO createMusicListDTO) {
        musicListMapper.insert(MusicList.builder()
                .userId(createMusicListDTO.getUserId())
                .title(createMusicListDTO.getTitle())
                .description(createMusicListDTO.getDescription() != null ? createMusicListDTO.getDescription() : MusicListConstant.NOT_FILLED_MUSIC_LIST_DESC)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now()).build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConstant.USER_MUSIC_LIST, key = "#dto.userId"),
            @CacheEvict(value = RedisCacheConstant.MUSIC_LIST_DETAIL, key = "#dto.musicListId")
    })
    public void addMusicToList(OperaMusicListDTO dto) {
        MusicList musicList = musicListMapper.selectById(dto.getMusicListId());
        if (musicList == null || !musicList.getUserId().equals(dto.getUserId())) {
            log.error("找不到音乐列表或用户未授权：musicListId={}， userId={}", dto.getMusicListId(), dto.getUserId());
            // throw new RuntimeException("找不到音乐列表或用户未授权");
            return;
        }
        MusicListTrack musicListTrack = MusicListTrack.builder()
                .musicListId(dto.getMusicListId())
                .musicId(dto.getMusicId())
                .createTime(LocalDateTime.now())
                .build();
        // 往MusicListTrack表中插入记录
        musicListTrackMapper.insert(musicListTrack);
        // 更新MusicList的更新时间
        musicList.setUpdateTime(LocalDateTime.now());
        musicListMapper.updateById(musicList);
    }

    @Override
    public List<MusicListVO> getMusicListsByUserId(Long userId) {
        //健壮性检查
        if (userId == null) {
            log.error("获取歌单列表错误,用户ID不能为空");
            return List.of();
        }
        //构造redis的key
        String redisKey = RedisCacheConstant.USER_MUSIC_LIST + "::" + userId;
        //从redis中获取数据
        List<MusicListVO> listVOS = redisCacheUtil
                .get(redisKey, new TypeReference<List<MusicListVO>>() {
                });
        if (listVOS != null && !listVOS.isEmpty()) {
            log.info("从Redis中获取了用户歌单数据, userId={}, {}", userId, listVOS);
            return listVOS;
        }

        //处理数据
        //获取用户的歌单列表,按创建时间降序排列
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
        //获取歌单内对应的音乐和数量
        for (MusicListVO musicListVO : musicListVOS) {
            List<Music> musicLists = getMusicList(musicListVO.getId());
            musicListVO.setMusicCount((long) musicLists.size());
        }
        //将查询处理完后的数据存入redis对应位置
        redisCacheUtil.set(redisKey, musicListVOS);

        //返回
        return musicListVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConstant.USER_MUSIC_LIST, key = "#dto.userId"),
            @CacheEvict(value = RedisCacheConstant.MUSIC_LIST_DETAIL, key = "#dto.musicListId")
    })
    public void removeMusicFromList(OperaMusicListDTO dto) {
        MusicList musicList = musicListMapper.selectById(dto.getMusicListId());
        if (musicList == null || !musicList.getUserId().equals(dto.getUserId())) {
            log.error("找不到音乐列表或用户未授权：musicListId={}， userId={}", dto.getMusicListId(), dto.getUserId());
            // throw new RuntimeException("找不到音乐列表或用户未授权");
            return;
        }
        //根据musicListId和musicId删除对应的记录
        musicListTrackMapper.delete(new LambdaQueryWrapper<MusicListTrack>()
                .eq(MusicListTrack::getMusicListId, dto.getMusicListId())
                .eq(MusicListTrack::getMusicId, dto.getMusicId()));
        // 更新MusicList的更新时间
        musicList.setUpdateTime(LocalDateTime.now());
        musicListMapper.updateById(musicList);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConstant.USER_MUSIC_LIST, key = "#userId"),
            @CacheEvict(value = RedisCacheConstant.MUSIC_LIST_DETAIL, key = "#id")
    })
    public void deleteMusicList(Long userId, Long id) {
        this.removeById(id);
    }

    @Override
    @Cacheable(cacheNames = "music:list:detail", key = "#query.musicListId")
    public MusicListDetailVO getDetailById(MusicListDetailQuery query) {
        //根据歌单ID和当前用户ID查询对应的歌单
        MusicList musicList = getUserMusicList(query);
        //健壮性检查,如果musicList为空,表示没有找到对应的歌单
        if (musicList == null) {
            log.error("找不到对应的歌单：id={}", query.getMusicListId());
            return null;
        }
        MusicListDetailVO vo = new MusicListDetailVO();
        BeanUtils.copyProperties(musicList, vo);
        List<Music> musicLists = getMusicList(query.getMusicListId());
        vo.setMusics(musicLists);
        return vo;
    }

    @Override
    public MusicListDetailVO getDetailQuery(MusicListDetailQuery query) {
        MusicList musicList = getUserMusicList(query);
        //健壮性检查,如果musicList为空,表示没有找到对应的歌单
        if (musicList == null) {
            log.error("找不到对应的歌单：id={}", query.getMusicListId());
            return null;
        }
        MusicListDetailVO vo = new MusicListDetailVO();
        BeanUtils.copyProperties(musicList, vo);
        List<Music> musicLists = getMusicList(query);
        vo.setMusics(musicLists);
        return vo;
    }

    /**
     * 根据查询条件获取对应的用户歌单
     */
    private MusicList getUserMusicList(MusicListDetailQuery query) {
        //根据歌单ID和当前用户ID查询对应的歌单
        return musicListMapper.selectOne(
                new LambdaQueryWrapper<MusicList>()
                        .eq(MusicList::getId, query.getMusicListId())
                        .eq(MusicList::getUserId, query.getUserId())
        );
    }

    /**
     * 根据歌单ID获取对应的音乐列表
     */
    private @NonNull List<Music> getMusicList(Long id) {
        List<MusicListTrack> musicListTracks = musicListTrackMapper.selectList(
                new LambdaQueryWrapper<MusicListTrack>()
                        .orderBy(true, true, MusicListTrack::getCreateTime) // 按创建时间升序
                        .eq(MusicListTrack::getMusicListId, id));
        //根据musicListTracks获取对应的MusicID获取音乐列表
        //先获取所有的MusicID
        List<Long> musicIds = musicListTracks.stream().map(MusicListTrack::getMusicId).toList();
        //如果musicIds为空,直接返回空列表
        if (musicIds.isEmpty()) {
            return List.of();
        }
        //根据MusicID列表查询对应的音乐封装到List<Music>中,最后返回
        return musicMapper.selectList(
                new LambdaQueryWrapper<Music>()
                        .in(Music::getId, musicIds));
    }

    /**
     * 重载方法: 根据查询条件获取对应的音乐列表
     */
    private @NonNull List<Music> getMusicList(MusicListDetailQuery query) {
        List<MusicListTrack> musicListTracks = musicListTrackMapper.selectList(
                new LambdaQueryWrapper<MusicListTrack>()
                        .orderBy(true, true, MusicListTrack::getCreateTime) // 按创建时间升序
                        .eq(MusicListTrack::getMusicListId, query.getMusicListId()));
        //根据musicListTracks获取对应的MusicID获取音乐列表
        //如果musicListTracks为空,直接返回空列表
        if (musicListTracks == null || musicListTracks.isEmpty()) {
            return List.of();
        }
        //先获取所有的MusicID
        List<Long> musicIds = musicListTracks.stream().map(MusicListTrack::getMusicId).toList();
        //根据MusicID列表查询对应的音乐封装到List<Music>中,最后返回
        return musicMapper.selectList(
                new LambdaQueryWrapper<Music>()
                        .in(Music::getId, musicIds)
                        //一定要使用and包裹住多条件查询,否则会导致逻辑错误
                        .and(musicWrapper -> musicWrapper
                                .like(Music::getTitle, query.getKeyword())
                                .or().like(Music::getArtist, query.getKeyword())
                                .or().like(Music::getAlbum, query.getKeyword()))
        );
    }
}
