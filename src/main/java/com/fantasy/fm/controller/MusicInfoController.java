package com.fantasy.fm.controller;

import com.fantasy.fm.domain.dto.PageDTO;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.query.MusicPageQuery;
import com.fantasy.fm.domain.vo.MusicVO;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicInfoController {

    private final MusicService musicInfoService;

    /**
     * 查询音乐列表
     *
     * @return 音乐列表
     */
    @GetMapping("/list")
    public Result<List<MusicVO>> queryMusicList() {
        log.info("查询音乐列表");
        List<Music> list = musicInfoService.list();
        List<MusicVO> musicVOS = list.stream().map(music -> {
            MusicVO vo = new MusicVO();
            BeanUtils.copyProperties(music, vo);
            return vo;
        }).toList();
        return Result.success(musicVOS);
    }

    /**
     * 分页查询音乐
     */
    @GetMapping("/page")
    public Result<PageDTO<MusicVO>> queryMusicPage(MusicPageQuery query) {
        log.info("分页查询音乐列表: pageNum={}, pageSize={}", query.getPageNum(), query.getPageSize());
        return Result.success(musicInfoService.queryMusicPage(query));
    }
}
