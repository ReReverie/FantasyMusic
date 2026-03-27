package com.fantasy.fm.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.util.NamedValue;
import com.fantasy.fm.pojo.domain.entity.Music;
import com.fantasy.fm.pojo.domain.query.MusicPageQuery;
import com.fantasy.fm.pojo.domain.vo.MusicVO;
import com.fantasy.fm.service.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient client;

    @Override
    public List<MusicVO> searchMusic(MusicPageQuery query) {
        if (StrUtil.isBlank(query.getKeyword())) {
            return List.of();
        }
        // 构建查询条件
        SearchRequest req = new SearchRequest.Builder()
                .index("music_index")
                .from(query.from())
                .size(query.getPageSize().intValue())
                .query(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .match(m -> m
                                                .field("title")
                                                .query(query.getKeyword())
                                                .analyzer("ik_max_word")
                                                .boost(10.0f)))
                                .should(s -> s
                                        .match(m -> m
                                                .field("artist")
                                                .query(query.getKeyword())
                                                .analyzer("ik_max_word")
                                                .boost(5.0f)))
                                .should(s -> s
                                        .match(m -> m
                                                .field("album")
                                                .query(query.getKeyword())
                                                .analyzer("ik_max_word")
                                                .boost(1.0f)))
                        ))
                .highlight(h -> h
                        .preTags("<em>")
                        .postTags("</em>")
                        .fields(
                                NamedValue.of("title", HighlightField.of(hf -> hf)),
                                NamedValue.of("artist", HighlightField.of(hf -> hf)),
                                NamedValue.of("album", HighlightField.of(hf -> hf))
                        )
                )
                .build();
        //发送ES查询请求并处理结果
        SearchResponse<Music> resp = null;
        try {
            resp = client.search(req, Music.class);
        } catch (IOException e) {
            log.error("ES查询失败: ", e);
            return List.of();
        }
        //处理搜索结果并转换为MusicVO列表
        //返回结果
        return resp.hits().hits().stream().map(hit -> {
            MusicVO vo = new MusicVO();
            if (hit.source() != null) {
                BeanUtils.copyProperties(hit.source(), vo);
            }

            //处理高亮字段
            if (hit.highlight() != null) {
                List<String> titleHighlights = hit.highlight().get("title") != null ? hit.highlight().get("title") : null;
                List<String> artistHighlights = hit.highlight().get("artist") != null ? hit.highlight().get("artist") : null;
                List<String> albumHighlights = hit.highlight().get("album") != null ? hit.highlight().get("album") : null;

                if (CollUtil.isNotEmpty(titleHighlights)) {
                    vo.setTitle(titleHighlights.get(0)); //取第一个高亮片段
                }
                if (CollUtil.isNotEmpty(artistHighlights)) {
                    vo.setArtist(artistHighlights.get(0));
                }
                if (CollUtil.isNotEmpty(albumHighlights)) {
                    vo.setAlbum(albumHighlights.get(0));
                }
            }
            return vo;
        }).toList();
    }

    @Override
    public List<String> searchSuggestMusic(String prefix) {
        //如果前缀为空或仅包含空白字符，则直接返回空列表
        if (prefix != null && prefix.trim().isEmpty()) {
            return List.of();
        }
        // 构建ES查询请求
        SearchResponse<Music> resp = null;
        try {
            resp = client.search(s -> s
                            .index("music_index")
                            .size(0) // 不需要返回实际文档,只返回建议
                            .suggest(sugg -> sugg
                                    .suggesters("music_suggest", sug -> sug
                                            .prefix(prefix)
                                            .completion(c -> c
                                                    .field("suggest")
                                                    .size(10)
                                                    .skipDuplicates(true)
                                            )
                                    )
                            )
                    , Music.class
            );
        } catch (IOException e) {
            log.error("ES搜索建议查询失败: ", e);
            return List.of();
        }
        // 处理ES响应并提取建议结果
        List<String> suggestions = new ArrayList<>();
        List<Suggestion<Music>> musicSuggest = resp.suggest().get("music_suggest");
        if (CollUtil.isNotEmpty(musicSuggest)) {
            musicSuggest.get(0).completion().options().forEach(musicCompletionSuggestOption
                    -> suggestions.add(musicCompletionSuggestOption.text()));
        }
        return suggestions;
    }
}
