package com.fantasy.fm.controller;

import com.fantasy.fm.domain.dto.PageDTO;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.query.MusicPageQuery;
import com.fantasy.fm.domain.vo.MusicVO;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "音乐信息管理", description = "音乐信息的查询接口")
public class MusicInfoController {

    private final MusicService musicInfoService;

    /**
     * 查询音乐列表
     *
     * @return 音乐列表
     */
    @Operation(summary = "查询音乐列表", description = "获取所有音乐的信息列表")
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
    @Operation(summary = "分页查询音乐", description = "根据分页参数查询音乐列表")
    @GetMapping("/page")
    public Result<PageDTO<MusicVO>> queryMusicPage(MusicPageQuery query) {
        log.info("分页查询音乐列表: pageNum={}, pageSize={}", query.getPageNum(), query.getPageSize());
        return Result.success(musicInfoService.queryMusicPage(query));
    }
}
