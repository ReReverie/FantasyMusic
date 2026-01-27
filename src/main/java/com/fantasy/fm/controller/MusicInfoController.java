package com.fantasy.fm.controller;

import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.vo.MusicListVO;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/music/list")
@RequiredArgsConstructor
public class MusicInfoController {

    private final MusicInfoService musicInfoService;

    /**
     * 查询音乐列表
     *
     * @return 音乐列表
     */
    @GetMapping
    public Result<List<MusicListVO>> queryMusicList() {
        List<Music> list = musicInfoService.list();
        List<MusicListVO> musicListVOs = list.stream().map(music -> {
            MusicListVO vo = new MusicListVO();
            BeanUtils.copyProperties(music, vo);
            return vo;
        }).toList();
        return Result.success(musicListVOs);
    }
}
