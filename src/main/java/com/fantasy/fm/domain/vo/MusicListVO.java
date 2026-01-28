package com.fantasy.fm.domain.vo;

import com.fantasy.fm.domain.entity.Music;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicListVO {
    private Long id;
    /**
     * 歌单标题
     */
    private String title;

    /**
     * 歌单简介
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String cover;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 歌单内的歌曲列表
     */
    private List<Music> musics;
}
