package com.fantasy.fm.web.controller;

import com.fantasy.fm.common.response.Result;
import com.fantasy.fm.pojo.domain.query.MusicPageQuery;
import com.fantasy.fm.pojo.domain.vo.MusicVO;
import com.fantasy.fm.service.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 搜索音乐库
     */
    @Operation(summary = "搜索音乐库", description = "搜索音乐库")
    @GetMapping
    public Result<List<MusicVO>> searchMusic(MusicPageQuery query) {
        // 注释日志，避免日志过多
        // log.info("分页查询音乐列表: pageNum={}, pageSize={}", query.getPageNum(), query.getPageSize());
        return Result.success(searchService.searchMusic(query));
    }

    /**
     * 搜索建议
     */
    @Operation(summary = "搜索建议", description = "搜索建议")
    @GetMapping("/suggest")
    public Result<List<String>> suggestMusic(@RequestParam("queryPrefix") String prefix) {
        // 注释日志，避免日志过多
        // log.info("搜索建议: keyword={}", prefix);
        return Result.success(searchService.searchSuggestMusic(prefix));
    }
}
