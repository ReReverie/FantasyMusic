package com.fantasy.fm.controller;

import com.fantasy.fm.domain.dto.CreateMusicListDTO;
import com.fantasy.fm.domain.entity.MusicList;
import com.fantasy.fm.domain.vo.MusicListVO;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/musiclist")
@RequiredArgsConstructor
public class MusicListController {

    private final MusicListService musicListService;

    /**
     * 创建歌单
     */
    @PostMapping("/create")
    public Result<Void> createMusicList(@RequestBody CreateMusicListDTO createMusicListDTO) {
        //TODO 获取当前用户信息
        log.info("Creating music list: {}", createMusicListDTO);
        musicListService.createMusicList(createMusicListDTO);
        return Result.success();
    }

    /**
     * 获取歌单列表
     */
    @GetMapping("/list")
    public Result<List<MusicListVO>> getMusicLists() {
        //假设当前用户ID是1L
        Long userId = 1L;
        List<MusicList> list = musicListService.lambdaQuery()
                .eq(MusicList::getUserId, userId).list();
        List<MusicListVO> musicListVOS = list.stream().map(musicList -> {
            MusicListVO vo = new MusicListVO();
            BeanUtils.copyProperties(musicList, vo);
            return vo;
        }).toList();
        return Result.success(musicListVOS);
    }
}
