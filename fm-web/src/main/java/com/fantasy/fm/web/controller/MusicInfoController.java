package com.fantasy.fm.web.controller;


import com.fantasy.fm.common.response.Result;
import com.fantasy.fm.pojo.domain.dto.PageDTO;
import com.fantasy.fm.pojo.domain.query.MusicPageQuery;
import com.fantasy.fm.pojo.domain.vo.MusicVO;
import com.fantasy.fm.service.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final MusicService musicService;

    /**
     * 获取音乐列表
     *
     * @return 音乐列表
     */
    @Operation(summary = "查询音乐列表", description = "获取所有音乐的信息列表")
    @GetMapping("/list")
    public Result<List<MusicVO>> getMusicList() {
        log.info("查询音乐列表");
        List<MusicVO> musicVOS = musicInfoService.getMusicInfo();
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

    /**
     * 根据条件查询音乐
     */
    @Operation(summary = "根据条件查询音乐", description = "根据条件查询音乐列表")
    @GetMapping("/search")
    public Result<PageDTO<MusicVO>> queryMusicPageByCondition(MusicPageQuery query) {
        log.info("分页查询音乐列表: pageNum={}, pageSize={}, Condition= Title: {}, Artist:{}",
                query.getPageNum(), query.getPageSize(), query.getTitle(), query.getArtist());
        return Result.success(musicInfoService.getMusicPageByCondition(query));
    }

    /**
     * 获取音乐封面
     */
    @Operation(summary = "获取音乐封面", description = "根据音乐ID获取音乐封面")
    @GetMapping("/cover/{id}")
    public ResponseEntity<Object> getMusicCover(@PathVariable Long id) {
        return musicService.getMusicCoverById(id);
    }
}
