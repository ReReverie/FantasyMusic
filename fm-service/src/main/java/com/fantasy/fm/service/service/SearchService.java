package com.fantasy.fm.service.service;

import com.fantasy.fm.pojo.domain.query.MusicPageQuery;
import com.fantasy.fm.pojo.domain.vo.MusicVO;

import java.util.List;

public interface SearchService {

    /**
     * 根据条件分页查询音乐
     *
     * @param query 查询条件
     * @return 音乐分页结果
     */
    List<MusicVO> searchMusic(MusicPageQuery query);

    /**
     * 搜索建议
     *
     * @param prefix 搜索前缀
     * @return 搜索建议列表
     */
    List<String> searchSuggestMusic(String prefix);
}
