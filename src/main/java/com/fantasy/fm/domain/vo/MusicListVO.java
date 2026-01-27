package com.fantasy.fm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
